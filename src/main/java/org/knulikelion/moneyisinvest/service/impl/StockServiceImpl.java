package org.knulikelion.moneyisinvest.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONArray;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.knulikelion.moneyisinvest.data.dto.request.StockBuyRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.StockSellRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.StocksByDayRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.TransactionToSystemRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.*;
import org.knulikelion.moneyisinvest.data.entity.Favorite;
import org.knulikelion.moneyisinvest.data.entity.Stock;
import org.knulikelion.moneyisinvest.data.entity.StockHoliday;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.repository.FavoriteRepository;
import org.knulikelion.moneyisinvest.data.repository.StockHolidayRepository;
import org.knulikelion.moneyisinvest.data.repository.StockRepository;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.StockCoinService;
import org.knulikelion.moneyisinvest.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.json.JSONException;
import org.json.JSONObject;

import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
@Slf4j
@Service
public class StockServiceImpl implements StockService {
    private String approvalToken;

    @Value("${KIS.APP.KEY}")
    private String app_Key;

    @Value("${KIS.APP.SECRET}")
    private String app_Secret;
    private final StockHolidayRepository stockHolidayRepository;
    private final StockRepository stockRepository;
    private final StockCoinService stockCoinService;
    private final UserRepository userRepository;
    private final FavoriteRepository favoriteRepository;

    @Autowired
    public StockServiceImpl(StockHolidayRepository stockHolidayRepository, StockRepository stockRepository, StockCoinService stockCoinService, UserRepository userRepository, FavoriteRepository favoriteRepository) {
        this.stockHolidayRepository = stockHolidayRepository;
        this.stockRepository = stockRepository;
        this.stockCoinService = stockCoinService;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
    }

    @PostConstruct
    protected void init() {
        log.info("[init] ApprovalToken 초기화 시작");
        scheduleTokenRefresh();
        log.info("[init] ApprovalToken 초기화 완료");
    }

    private void scheduleTokenRefresh() {
        TimerTask timerTask = new TimerTask() {
            @SneakyThrows
            @Override
            public void run() {
                try {
                    JSONObject body = createBody();
                    approvalToken = createApprovalToken(body);
                } catch (JSONException | IOException e) {
                    throw new RuntimeException(e);
                }

            }
        };
        Timer timer = new Timer();
        long delay = 0;
        long interval = 12 * 60 * 60 * 1000;

        timer.scheduleAtFixedRate(timerTask, delay, interval);
    }

    public JSONObject createBody() throws JSONException { /*승인 키 받아올 때 사용되는 body 생성 코드 입니다.*/
        JSONObject body = new JSONObject();
        body.put("grant_type", "client_credentials");
        body.put("appkey", app_Key);
        body.put("appsecret", app_Secret);
        return body;
    }

    public String createApprovalToken(JSONObject body) throws IOException, JSONException { /*승인 키 반환하는 코드 입니다.*/
        String apiUrl = "https://openapi.koreainvestment.com:9443/oauth2/tokenP";
        JSONObject result;
        HttpURLConnection connection;
        URL url = new URL(apiUrl);

        connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        try (OutputStream os = connection.getOutputStream()) { /*outPutStream 으로 connection 형태 가져옴*/
            byte[] input = body.toString().getBytes("utf-8"); /*body 값을 json 형태로 입력*/
            os.write(input, 0, input.length);
        }
        try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            result = new JSONObject(response.toString());
        }
        return result.getString("access_token");
    }


    @Override
    public StockCompanyInfoResponseDto getCompanyInfoByStockId(String stockId) {
        StockCompanyInfoResponseDto stockCompanyInfoResponseDto = new StockCompanyInfoResponseDto();
        String url = "https://comp.kisline.com/co/CO0100M010GE.nice?stockcd=" + stockId + "&nav=2&header=N";

        String logoUrl = "https://file.alphasquare.co.kr/media/images/stock_logo/kr/" + stockId + ".png";
        stockCompanyInfoResponseDto.setStockLogoUrl(logoUrl);

        try {
            Document document = Jsoup.connect(url).get();

            Element biztopDiv = document.select("div.biztop").first();
            if (biztopDiv != null) {
                Element h2Element = biztopDiv.select("h2").first();
                if (h2Element != null) {
                    h2Element.select("small").remove();
                    stockCompanyInfoResponseDto.setStockName(h2Element.text());
                }
            }
            Element sectionElement = document.select("section.con[data-top=1]").first();
            stockCompanyInfoResponseDto.setStockId(stockId);
            if (sectionElement != null) {
                Element tblDiv = sectionElement.select("div.tbl").first();
                if (tblDiv != null) {
                    Element tbody = tblDiv.select("tbody").first();
                    if (tbody != null) {
                        Elements trElements = tbody.select("tr");

                        for (Element trElement : trElements) {
                            Elements thElements = trElement.select("th[scope=row]");
                            Elements tdElements = trElement.select("td");

                            for (int i = 0; i < thElements.size(); i++) {
                                Element thElement = thElements.get(i);
                                if (thElement != null) {
                                    String headerText = thElement.text();
                                    if (headerText.equals("상장일") || headerText.equals("설립일")) {
                                        Element tdElement = tdElements.get(i);
                                        if (headerText.equals("상장일")) {
                                            stockCompanyInfoResponseDto.setGoPublicDate(tdElement.text());
                                        } else {
                                            stockCompanyInfoResponseDto.setEstablishmentDate(tdElement.text());
                                        }
                                    }
                                }
                            }
                        }

                        for (Element trElement : trElements) {
                            Elements thElements = trElement.select("th[scope=row]");
                            Elements tdElements = trElement.select("td");

                            for (int i = 0; i < thElements.size(); i++) {
                                Element thElement = thElements.get(i);
                                if (thElement != null) {
                                    String headerText = thElement.text();

                                    if (headerText.equals("기업영문명") || headerText.equals("기업명")) {
                                        Element tdElement = tdElements.get(i);
                                        if (headerText.equals("기업영문명")) {
                                            stockCompanyInfoResponseDto.setCompanyEnName(tdElement.text());
                                        } else {
                                            stockCompanyInfoResponseDto.setCompanyName(tdElement.text());
                                        }
                                    }
                                }
                            }
                        }

                        for (Element trElement : trElements) {
                            Element thElement = trElement.select("th[scope=row]").first();

                            // 대표자 가져오기
                            if (thElement != null && thElement.text().equals("대표자")) {
                                Element tdElement = trElement.select("td").first();
                                if (tdElement != null) {
                                    String representativeName = tdElement.text();
                                    stockCompanyInfoResponseDto.setRepresentativeName(representativeName);
                                }
                            }

                            // 주요품목 항목 가져오기
                            if (thElement != null && thElement.text().equals("주요품목 항목")) {
                                Element tdElement = trElement.select("td").first();
                                if (tdElement != null) {
                                    String mainItems = tdElement.text();
                                    stockCompanyInfoResponseDto.setMainItems(mainItems);
                                }
                            }

                            // 회사 대표 홈페이지 주소 가져오기
                            if (thElement != null && thElement.text().equals("홈페이지")) {
                                Element tdElement = trElement.select("td").first();
                                if (tdElement != null) {
                                    String originalUrl = tdElement.text();

                                    // URL이 http:// 또는 https://로 시작하지 않으면, 프로토콜(http://)추가
                                    if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
                                        originalUrl = "http://" + originalUrl;
                                    }
                                    stockCompanyInfoResponseDto.setCompanyUrl(originalUrl);
                                }
                            }
                        }
                    }
                }
            }

        } catch (IOException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }

        return stockCompanyInfoResponseDto;
    }

    @Override
    public List<StockCompanyNewsResponseDto> getCompanyNewsByStockId(String stockId) {
        List<StockCompanyNewsResponseDto> stockCompanyNewsList = new ArrayList<>();
        String url = "https://finance.naver.com/item/news_news.naver?code=" + stockId + "&page=&sm=title_entity_id.basic&clusterId=";

        try {
            Document document = Jsoup.connect(url).get();
            Elements trElements = document.select("tbody > tr.first, tbody > tr.last, tbody > tr:not([class]), tbody > tr.first.relation_tit, tbody > tr.last.relation_tit");

            for (Element trElement : trElements) {
                StockCompanyNewsResponseDto stockCompanyNewsResponseDto = new StockCompanyNewsResponseDto();

                if (trElement.select("td.title a").text().isEmpty()) {
                    break;
                }

                String newsTitle = trElement.select("td.title a").text();
                stockCompanyNewsResponseDto.setNewsTitle(newsTitle);

                String newsCompany = trElement.select("td.info").text();
                stockCompanyNewsResponseDto.setNewsCompany(newsCompany);

                String newsDate = trElement.select("td.date").text();
                stockCompanyNewsResponseDto.setNewsCreatedAt(newsDate);

                String newsUrl = trElement.select("td.title a").attr("href");
                Pattern articleIdPattern = Pattern.compile("article_id=(\\d+)");
                Pattern officeIdPattern = Pattern.compile("office_id=(\\d+)");
                Matcher articleIdMatcher = articleIdPattern.matcher(newsUrl);
                Matcher officeIdMatcher = officeIdPattern.matcher(newsUrl);
                String articleId = null;
                String officeId = null;

                if (articleIdMatcher.find()) {
                    articleId = articleIdMatcher.group(1);
                }

                if (officeIdMatcher.find()) {
                    officeId = officeIdMatcher.group(1);
                }
                stockCompanyNewsResponseDto.setNewsUrl("https://n.news.naver.com/article/" + officeId + "/" + articleId);

                Document newsDocument = Jsoup.connect("https://n.news.naver.com/article/" + officeId + "/" + articleId).get();
                Element thumbnailElement = newsDocument.select("head meta[property=og:image]").first();
                Element previewElement = newsDocument.select("head meta[property=og:description]").first();

                if (thumbnailElement != null) {
                    stockCompanyNewsResponseDto.setNewsThumbnail(thumbnailElement.attr("content"));
                }

                if (previewElement != null) {
                    stockCompanyNewsResponseDto.setNewsPreview(previewElement.attr("content"));
                }

                stockCompanyNewsList.add(stockCompanyNewsResponseDto);
            }

            return stockCompanyNewsList;
        } catch (IOException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }

        return null;
    }

    @Override
    public String getStockNameByStockId(String stockId) {
        String url = "https://comp.kisline.com/co/CO0100M010GE.nice?stockcd=" + stockId + "&nav=2&header=N";

        try {
            Document document = Jsoup.connect(url).get();

            Element biztopDiv = document.select("div.biztop").first();
            if (biztopDiv != null) {
                Element h2Element = biztopDiv.select("h2").first();
                if (h2Element != null) {
                    h2Element.select("small").remove();
                    return h2Element.text();
                }
            }
        } catch (IOException e) {
            return "해당 종목 이름을 가져올 수 없거나, 존재하지 않은 종목 코드임";
        }

        return "해당 종목 이름을 가져올 수 없거나, 존재하지 않은 종목 코드임";
    }

    @Override
    public List<StockSearchResponseDto> searchStockByKeyword(String keyword) throws UnsupportedEncodingException {
        String encodedKeyword = URLEncoder.encode(keyword, "EUC_KR");

        String url = "https://finance.naver.com/search/searchList.naver?query=" + encodedKeyword;

        List<StockSearchResponseDto> stockSearchResponseDtoList = new ArrayList<>();

        try {
            Document document = Jsoup.connect(url).get();

            Elements aElements = document.select("div.section_search > table > tbody > tr > td.tit > a");

            for (Element aElement : aElements) {
                StockSearchResponseDto stockSearchResponseDto = new StockSearchResponseDto();

                String hrefValue = aElement.attr("href");
                Pattern pattern = Pattern.compile("code=(\\d+)");
                Matcher matcher = pattern.matcher(hrefValue);
                if (matcher.find()) {
                    stockSearchResponseDto.setStockId(matcher.group(1));
                }

                String linkText = aElement.text();
                stockSearchResponseDto.setStockName(linkText);

                stockSearchResponseDtoList.add(stockSearchResponseDto);
            }
        } catch (IOException e) {
            return stockSearchResponseDtoList;
        }

        return stockSearchResponseDtoList;
    }

    @Override
    public CheckHolidayResponseDto checkIsHolidayNow() {
//        API 호출 시 현재 시간 조회
        LocalDateTime currentDate = LocalDateTime.now();

        List<StockHoliday> getHolidays = stockHolidayRepository.getAllByYear(currentDate.getYear());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

//        주식 장 거래 시간: 오전 9시 ~ 오후 3시 30분
        LocalTime startTime = LocalTime.of(9, 0);
        LocalTime endTime = LocalTime.of(15, 30);

        CheckHolidayResponseDto checkHolidayResponseDto = new CheckHolidayResponseDto();

        for (StockHoliday temp : getHolidays) {
//            현재 날짜가 DB에 저장된 공휴일 날짜와 같은지 판단
            if (temp.getDate().toString().equals(currentDate.format(formatter))) {
                checkHolidayResponseDto.setOpened(false);
                checkHolidayResponseDto.setReason(temp.getReason());

                return checkHolidayResponseDto;
            }
        }

//        주말 여부 판단
        if (currentDate.getDayOfWeek() == DayOfWeek.SATURDAY || currentDate.getDayOfWeek() == DayOfWeek.SUNDAY) {
            checkHolidayResponseDto.setOpened(false);
            checkHolidayResponseDto.setReason("주말으로 인한 주식 거래 제한 (토요일, 일요일)");

            return checkHolidayResponseDto;
//            장 거래 시간 판단
        } else if (currentDate.toLocalTime().isBefore(startTime) || currentDate.toLocalTime().isAfter(endTime)) {
            checkHolidayResponseDto.setOpened(false);
            checkHolidayResponseDto.setReason("주식 거래 시간이 아님 (오전 9시 ~ 오후 3시 30분)");

            return checkHolidayResponseDto;
        } else {
            checkHolidayResponseDto.setOpened(true);
            checkHolidayResponseDto.setReason("장 거래 중 (오전 9시 ~ 오후 3시 30분)");

            return checkHolidayResponseDto;
        }
    }

    @Override
    public List<HolidayResponseDto> getAllHoliday() {
        //        API 호출 시 현재 시간 조회
        LocalDateTime currentDate = LocalDateTime.now();

        List<StockHoliday> getHolidays = stockHolidayRepository.getAllByYear(currentDate.getYear());

        List<HolidayResponseDto> holidayResponseDtoList = new ArrayList<>();

        for (StockHoliday temp : getHolidays) {
            HolidayResponseDto holidayResponseDto = new HolidayResponseDto();

            holidayResponseDto.setDate(temp.getDate().toString());
            holidayResponseDto.setReason(temp.getReason());

            holidayResponseDtoList.add(holidayResponseDto);
        }

        return holidayResponseDtoList;
    }

    public StockCompanyFavResponseDto getCompanyFavByStockId(String stockId) {
        String url = "https://comp.kisline.com/co/CO0100M010GE.nice?stockcd=" + stockId + "&nav=2&header=N";

        // stockLogoUrl을 설정하기위한 로고 URL
        String logoUrl = "https://file.alphasquare.co.kr/media/images/stock_logo/kr/" + stockId + ".png";

        // 설정할 companyName, price 값 초기화
        String companyName = "";
        double price = 0.0;
        double stockPrice = 0.0; // 이전 종가 값을 초기화

        try {
            Document document = Jsoup.connect(url).get();

            // 회사 이름 가져오기
            Element h2Element = document.select("div.biztop h2").first();
            if (h2Element != null) {
                h2Element.select("small").remove();
                companyName = h2Element.text();
            }

            // 가격 정보 가져오기
            Element priceElement = document.select("#curr_price").first();
            if (priceElement != null) {
                price = Double.parseDouble(priceElement.text().replaceAll(",", ""));
            }

            // 이전 종가(daily 종가) 정보 가져오기
            Element stockPriceElement = document.select("#prevday_price").first();
            if (stockPriceElement != null) {
                stockPrice = Double.parseDouble(stockPriceElement.text().replaceAll(",", ""));
            }

        } catch (IOException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }

        // DTO 객체 생성 및 반환
        return new StockCompanyFavResponseDto(stockId, logoUrl, companyName, price, stockPrice);
    }

    public String getCurrentPrice(String stockCode) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations/inquire-price").newBuilder();
        urlBuilder.addQueryParameter("FID_COND_MRKT_DIV_CODE", "J");
        urlBuilder.addQueryParameter("FID_INPUT_ISCD", stockCode);
        String url = urlBuilder.build().toString(); /*한국 현재 주식 시세 url*/

        OkHttpClient client = new OkHttpClient();
        StockPriceResponseDto stockPriceResponseDto = new StockPriceResponseDto();

        Request request = new Request.Builder()
                .url(url)
                .header("authorization", "Bearer " + approvalToken)
                .header("appkey", app_Key)
                .header("appsecret", app_Secret)
                .header("tr_id", "FHKST01010100")
                .header("Content-type", "application/json; charset=utf-8")
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONObject outputs = jsonObject.getJSONObject("output");
                stockPriceResponseDto.setCurrnet_time(String.valueOf(LocalDateTime.now()));
                String price = (String) outputs.get("stck_prpr");

                return price;
            } else {
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return null;
    }




    @Override /*주식 매수*/
    public BaseResponseDto buyStock(String uid, StockBuyRequestDto stockBuyRequestDto) throws JSONException, IOException {
        log.info("[buyStock] 주식 매수 종목 코드 : {}", stockBuyRequestDto.getStockCode());
        BaseResponseDto baseResponseDto = new BaseResponseDto();
        TransactionToSystemRequestDto transactionToSystemRequestDto = new TransactionToSystemRequestDto();

        transactionToSystemRequestDto.setTargetUid(uid);
        transactionToSystemRequestDto.setAmount(Double.parseDouble(stockBuyRequestDto.getConclusion_price()) / 100);

        BaseResponseDto transactionResult = stockCoinService.buyStock(transactionToSystemRequestDto);

        if (transactionResult.isSuccess()) {
            log.info("[buyStock] stock 거래 성공 후 DB 저장");
            log.info("[buyStock] 신규 매수 종목 코드 : {}", stockBuyRequestDto.getStockCode());
            if (stockRepository.findByStockCode(stockBuyRequestDto.getStockCode()) == null) {
                Stock stock = new Stock();
                stock.setStockUrl(getCompanyInfoByStockId(stockBuyRequestDto.getStockCode()).getStockLogoUrl());
                stock.setStockCode(stockBuyRequestDto.getStockCode()); // 종목
                stock.setStockAmount(stockBuyRequestDto.getStockAmount()); // 종목 수량

                Integer conclusion_price = Integer.parseInt(stockBuyRequestDto.getConclusion_price());
                Integer amount = Integer.parseInt(stockBuyRequestDto.getStockAmount());
                Integer current_price = conclusion_price * amount;
                stock.setConclusion_price(current_price); // 체결가

                stock.setConclusion_coin(stock.getConclusion_price() / 100); // 스톡가


                Double myPrice = Double.parseDouble(stockBuyRequestDto.getConclusion_price());
                Double myAmount = Double.parseDouble(stockBuyRequestDto.getStockAmount());
                Double currentPrice = Double.parseDouble(getCurrentPrice(stockBuyRequestDto.getStockCode()));
                Double rate = (((currentPrice * myAmount) - (myPrice * myAmount)) / (myPrice * myAmount)) * 100;
                stock.setRate(rate); /*수익률 계산*/

                User user = userRepository.getByUid(uid);

                stock.setUser(user); // user

                List<Favorite> favoriteList = favoriteRepository.findAllByUserId(user.getId());
                boolean isFavoriteSet = false;
                if (!favoriteList.isEmpty()) {
                    for (Favorite favorite : favoriteList) {
                        if (favorite.getStockId().equals(stockBuyRequestDto.getStockCode())) {
                            stock.setFavorite_status(true);
                            isFavoriteSet = true;
                            break;
                        }
                    }
                }
                if (!isFavoriteSet) {
                    stock.setFavorite_status(false);
                }
                stockRepository.save(stock);
            } else {
                log.info("[withdrawStockCoinToSystem]stock 거래 성공 후 DB 저장");
                log.info("[buyStock] 기존 보유 종목 코드 : {}", stockBuyRequestDto.getStockCode());
                Stock findStock = stockRepository.findByStockCode(stockBuyRequestDto.getStockCode());

                Integer sumAmount = Integer.parseInt(findStock.getStockAmount()) + Integer.parseInt(stockBuyRequestDto.getStockAmount());
                findStock.setStockAmount(String.valueOf(sumAmount)); // 종목 체결 수량

                Integer conclusion_price = Integer.parseInt(stockBuyRequestDto.getConclusion_price());
                Integer amount = Integer.parseInt(stockBuyRequestDto.getStockAmount());
                Integer current_price = conclusion_price * amount;
                Integer price = findStock.getConclusion_price() + current_price;
                findStock.setConclusion_price(price); // 현재가 체결

                findStock.setConclusion_coin(price / 100); // 스톡가

                Double myPrice = Double.parseDouble(String.valueOf(price));
                Double comparePrice = Double.parseDouble(String.valueOf(sumAmount)) * Double.parseDouble(getCurrentPrice(stockBuyRequestDto.getStockCode()));
                Double rate = ((comparePrice - myPrice) / myPrice) * 100;
                findStock.setRate(rate);

                User user = userRepository.getByUid(uid);
                List<Favorite> favoriteList = favoriteRepository.findAllByUserId(user.getId());
                boolean isFavoriteSet = false;
                if (!favoriteList.isEmpty()) {
                    for (Favorite favorite : favoriteList) {
                        if (favorite.getStockId().equals(stockBuyRequestDto.getStockCode())) {
                            findStock.setFavorite_status(true);
                            isFavoriteSet = true;
                            break;
                        }
                    }
                }
                if (!isFavoriteSet) {
                    findStock.setFavorite_status(false);
                }

                stockRepository.save(findStock);
            }
            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("주식 매수가 완료되었습니다.");
            return baseResponseDto;
        } else {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("주식 매수에 실패하였습니다.");
            return baseResponseDto;
        }
    }

    @Override /*주식 매도*/
    public BaseResponseDto sellStock(String uid, StockSellRequestDto stockSellRequestDto) {
        log.info("[sellStock] 주식 매수 종목 코드 : {}", stockSellRequestDto.getStockCode());

        BaseResponseDto baseResponseDto = new BaseResponseDto();

        TransactionToSystemRequestDto transactionToSystemRequestDto = new TransactionToSystemRequestDto();

        if (stockRepository.findByStockCode(stockSellRequestDto.getStockCode()) == null) {
            baseResponseDto.setMsg("보유하지 않은 종목입니다.");
            baseResponseDto.setSuccess(false);
        } else {
            transactionToSystemRequestDto.setTargetUid(uid);
            transactionToSystemRequestDto.setAmount(Double.parseDouble(stockSellRequestDto.getSell_price()) / 100);

            BaseResponseDto transactionResult = stockCoinService.sellStock(transactionToSystemRequestDto);
            if (transactionResult.isSuccess()) {
                log.info("[sellStock] stock 거래 성공 후 DB 저장");
                Stock findStock = stockRepository.findByStockCode(stockSellRequestDto.getStockCode());
                Integer myAmount = Integer.parseInt(findStock.getStockAmount());
                Integer sellAmount = Integer.parseInt(stockSellRequestDto.getStockAmount());
                findStock.setStockAmount(String.valueOf(myAmount - sellAmount)); // 종목 수량
                if (myAmount - sellAmount > 0) {
                    Integer myPrice = findStock.getConclusion_price();
                    Integer sellPrice = Integer.parseInt(stockSellRequestDto.getSell_price());
                    Integer fixedPrice = myPrice - sellPrice * sellAmount;
                    findStock.setConclusion_price(fixedPrice); // 체결가

                    findStock.setConclusion_coin(fixedPrice / 100); // 스톡가

                    Double getPrice = Double.parseDouble(getCurrentPrice(stockSellRequestDto.getStockCode()));
                    Double fixPrice = Double.parseDouble(String.valueOf(fixedPrice));
                    Double Amount = Double.parseDouble(String.valueOf(myAmount - sellAmount));

                    findStock.setRate((((getPrice * Amount) - fixPrice) / fixPrice) * 100); // 수익률 계산

                    User user = userRepository.getByUid(uid);
                    List<Favorite> favoriteList = favoriteRepository.findAllByUserId(user.getId());
                    boolean isFavoriteSet = false;
                    if (!favoriteList.isEmpty()) {
                        for (Favorite favorite : favoriteList) {
                            if (favorite.getStockId().equals(stockSellRequestDto.getStockCode())) {
                                findStock.setFavorite_status(true);
                                isFavoriteSet = true;
                                break;
                            }
                        }
                    }
                    if (!isFavoriteSet) {
                        findStock.setFavorite_status(false);
                    }
                    stockRepository.save(findStock);
                    baseResponseDto.setMsg("주식 매도가 완료되었습니다.");
                    baseResponseDto.setSuccess(true);
                } else if (myAmount - sellAmount == 0) {
                    findStock.setUser(null);
                    stockRepository.delete(findStock);
                    baseResponseDto.setMsg("보유 종목 수량이 0 입니다.");
                    baseResponseDto.setSuccess(true);
                } else {
                    baseResponseDto.setMsg("보유 종목 수량을 초과하였습니다.");
                    baseResponseDto.setSuccess(false);
                }
            }
        }
        return baseResponseDto;
    }

    @Override
    public List<StocksByDayResponseDto> getStockByDay(StocksByDayRequestDto stocksByDayRequestDto) throws IOException {
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        LocalDateTime dateTime = LocalDateTime.now();

        // 어제 날짜 계산
        LocalDateTime yesterdayDate = dateTime.minusDays(1);
        String formattedYesterday = yesterdayDate.format(outputFormatter);
        // 한 달 전 날짜 계산
        LocalDateTime oneMonthAgoDate = dateTime.minusMonths(1);
        String formattedOneMonthAgo = oneMonthAgoDate.format(outputFormatter);

        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations/inquire-daily-itemchartprice").newBuilder();
        urlBuilder.addQueryParameter("FID_COND_MRKT_DIV_CODE","J");
        urlBuilder.addQueryParameter("FID_INPUT_ISCD",stocksByDayRequestDto.getStockCode());
        urlBuilder.addQueryParameter("FID_PERIOD_DIV_CODE", "D");
        urlBuilder.addQueryParameter("FID_ORG_ADJ_PRC","0");
        urlBuilder.addQueryParameter("FID_INPUT_DATE_1",formattedOneMonthAgo); // 시작
        urlBuilder.addQueryParameter("FID_INPUT_DATE_2",formattedYesterday); // 끝

        String url = urlBuilder.build().toString(); /*한달 기준 주식 data url*/

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("authorization","Bearer "+approvalToken)
                .header("appkey",app_Key)
                .header("appsecret",app_Secret)
                .header("tr_id","FHKST03010100")
                .header("content-type","application/json; charset=utf-8")
                .build();

        List<StocksByDayResponseDto> outputList = new ArrayList<>();

        try(Response response = client.newCall(request).execute()){
            if(response.isSuccessful() && response.body() != null){
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray outputs = jsonObject.getJSONArray("output2");

                for(int i =0; i<outputs.length(); i++){
                    JSONObject output = outputs.getJSONObject(i);

                    StocksByDayResponseDto stocksByDayResponseDto = StocksByDayResponseDto.builder()
                            .current_date(output.getString("stck_bsop_date"))
                            .end_Price(output.getString("stck_clpr"))
                            .start_Price(output.getString("stck_oprc"))
                            .high_Price(output.getString("stck_hgpr"))
                            .low_Price(output.getString("stck_lwpr"))
                            .build();
                    outputList.add(stocksByDayResponseDto);
                }
                return outputList;
            }
        }
        return null;
    }
}


