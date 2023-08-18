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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import org.knulikelion.moneyisinvest.data.dto.request.StockBuyRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.StockSellRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.StocksByDayRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.TransactionToSystemRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.*;
import org.knulikelion.moneyisinvest.data.entity.*;
import org.knulikelion.moneyisinvest.data.repository.*;
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
import java.text.NumberFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
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
    private final StockTransactionRepository stockTransactionRepository;

    @Autowired
    public StockServiceImpl(StockHolidayRepository stockHolidayRepository, StockRepository stockRepository, StockCoinService stockCoinService, UserRepository userRepository, FavoriteRepository favoriteRepository, StockTransactionRepository stockTransactionRepository) {
        this.stockHolidayRepository = stockHolidayRepository;
        this.stockRepository = stockRepository;
        this.stockCoinService = stockCoinService;
        this.userRepository = userRepository;
        this.favoriteRepository = favoriteRepository;
        this.stockTransactionRepository = stockTransactionRepository;
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

    public static boolean checkImageExists(String logoUrl) {
        try {
            URL url = new URL(logoUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("HEAD");

            int responseCode = connection.getResponseCode();
            return (responseCode >= 200 && responseCode < 300);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static String createImageFromText(String stockId, String text) {
        Path fileStorageLocation = Paths.get("./moneyisinvest/");
        Path imagePath = fileStorageLocation.resolve(stockId + ".png");

        int width = 500;
        int height = 500;

        String firstCharacter;

        if(text == null) {
            firstCharacter = "M";
        } else {
            firstCharacter = text.substring(0, 1);
        }

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = image.createGraphics();

        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2d.setColor(new Color(133, 214, 209)); // 밝은 파란색 배경 설정
        g2d.fillRect(0, 0, width, height);
        g2d.setColor(Color.WHITE); // 하얀색 글씨 설정

        Font font = new Font("Liberation Sans", Font.PLAIN, 170);
        g2d.setFont(font);
        FontMetrics fontMetrics = g2d.getFontMetrics();

        int textWidth = fontMetrics.stringWidth(firstCharacter);
        int textHeight = fontMetrics.getHeight();
        int x = (width - textWidth) / 2;
        int y = (height - textHeight) / 2 + fontMetrics.getAscent();

        g2d.drawString(firstCharacter, x, y);
        g2d.dispose();

        try {
            Files.createDirectories(fileStorageLocation); // 디렉터리 생성
            File file = new File(imagePath.toString());
            ImageIO.write(image, "png", file);

            return "https://moneyisinvest.kr/api/v1/profile/images/" + stockId + ".png";
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<CompanyResultTableResponseDto> getCompanyResultTable(String stockId) throws IOException {
        List<CompanyResultTableResponseDto> companyResultTableResponseDtoList = new ArrayList<>();
        String url = "https://finance.naver.com/item/main.naver?code=" + stockId;
        List<String> indicators = Arrays.asList("매출액", "영업이익", "당기순이익", "부채비율", "당좌비율", "유보율");

        Document doc = Jsoup.connect(url).get();
        Element table = doc.selectFirst("table.tb_type1.tb_num.tb_type1_ifrs");
        Elements rows = table.select("tbody > tr");

        // 연도 부분만 저장합니다.
        Elements yearElements = table.select("thead > tr > th:contains(.12)");

        List<String> perList = new ArrayList<>();
        List<String> pbrList = new ArrayList<>();

        Elements ratioRows = doc.select("table.tb_type1.tb_num.tb_type1_ifrs > tbody > tr");
        for (Element row : ratioRows) {
            String rowTitle = row.select("th").text();
            if (rowTitle.equals("PER(배)")) {
                Elements tds = row.select("td");
                tds.forEach(td -> perList.add(td.text()));
            } else if (rowTitle.contains("PBR(배)")) {
                Elements tds = row.select("td");
                tds.forEach(td -> pbrList.add(td.text()));
            }
        }

        for (int i = 0; i < rows.size(); i++) {
            Element row = rows.get(i);
            String rowTitle = row.select("th").text();

            if (indicators.contains(rowTitle)) {
                for (int j = 0; j < yearElements.size(); j++) {
                    Map<String, String> resultMap = new HashMap<>();

                    for (int k = 0; k < rows.size(); k++) {
                        String indicator = rows.get(k).selectFirst("th").text();
                        String value = rows.get(k).select("td").get(j).text();
                        resultMap.put(indicator, value);
                    }

                    CompanyResultTableResponseDto dto = CompanyResultTableResponseDto.builder()
                            .date(yearElements.get(j).text())
                            .take(resultMap.get("매출액"))
                            .operatingProfit(resultMap.get("영업이익"))
                            .netIncome(resultMap.get("당기순이익"))
                            .debtRatio(resultMap.get("부채비율"))
                            .quickRatio(resultMap.get("당좌비율"))
                            .retentionRate(resultMap.get("유보율"))
                            .per(perList.get(j))
                            .pbr(pbrList.get(j))
                            .build();

                    companyResultTableResponseDtoList.add(dto);

                    if (companyResultTableResponseDtoList.size() >= 4) {
                        return companyResultTableResponseDtoList;
                    }
                }
            }
        }

        return companyResultTableResponseDtoList;
    }

    @Override
    public StockCompanyInfoResponseDto getCompanyInfoByStockId(String stockId) {
        StockCompanyInfoResponseDto stockCompanyInfoResponseDto = new StockCompanyInfoResponseDto();
        String url = "https://comp.kisline.com/co/CO0100M010GE.nice?stockcd=" + stockId + "&nav=2&header=N";

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

        String logoUrl = "https://file.alphasquare.co.kr/media/images/stock_logo/kr/" + stockId + ".png";

        boolean imageExists = checkImageExists(logoUrl);

        if(!imageExists) {
            stockCompanyInfoResponseDto.setStockLogoUrl(createImageFromText(stockId, stockCompanyInfoResponseDto.getStockName()));
        } else {
            stockCompanyInfoResponseDto.setStockLogoUrl(logoUrl);
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
    public List<StockCompanyNewsResponseDto> getAllNews() throws IOException {
        String url = "https://news.naver.com/main/list.naver?mode=LS2D&mid=shm&sid1=101&sid2=258";
        Document document = Jsoup.connect(url).get();

        Elements ulElements = document.select("#main_content > div.list_body.newsflash_body > ul.type06_headline");

        List<StockCompanyNewsResponseDto> stockCompanyNewsList = new ArrayList<>();

        for (Element ulElement : ulElements) {
            for (Element liElement : ulElement.select("li")) {
                String link = null;
                String title = null;
                String lede = null;
                String writing = null;
                String date = null;
                String thumbnail = null;

                for (Element dtElement : liElement.select("dl > dt")) {
                    if (!dtElement.hasClass("photo")) {
                        Element aElement = dtElement.select("a").first();
                        link = aElement.attr("href");
                        title = aElement.text();
                    } else {
                        Element imgElement = dtElement.select("img").first();
                        if (imgElement != null) {
                            thumbnail = imgElement.attr("src");
                        }
                    }
                }

                Element ledeElement = liElement.select("dd > span.lede").first();
                if (ledeElement != null) {
                    lede = ledeElement.text();
                }

                Element writingElement = liElement.select("dd > span.writing").first();
                if (writingElement != null) {
                    writing = writingElement.text();
                }

                Element dateElement = liElement.select("dd > span.date").first();
                if (dateElement != null) {
                    date = dateElement.text();
                }

                StockCompanyNewsResponseDto stockCompanyNewsResponseDto = new StockCompanyNewsResponseDto();
                stockCompanyNewsResponseDto.setNewsUrl(link);
                stockCompanyNewsResponseDto.setNewsTitle(title);
                stockCompanyNewsResponseDto.setNewsThumbnail(thumbnail);
                stockCompanyNewsResponseDto.setNewsPreview(lede);
                stockCompanyNewsResponseDto.setNewsCreatedAt(date);
                stockCompanyNewsResponseDto.setNewsCompany(writing);

                stockCompanyNewsList.add(stockCompanyNewsResponseDto);
            }
        }

        return stockCompanyNewsList;
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
                stockPriceResponseDto.setCurrent_time(String.valueOf(LocalDateTime.now()));
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
    @Override
    public String getDayBeforeRate(String stockCode) {
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
                stockPriceResponseDto.setCurrent_time(String.valueOf(LocalDateTime.now()));
                String rate = (String) outputs.get("prdy_ctrt");

                return rate;
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
        stockBuyRequestDto.setConclusion_price(stockBuyRequestDto.getConclusion_price().replace(",", ""));
        log.info("[buyStock] 주식 매수 종목 코드 : {}", stockBuyRequestDto.getStockCode());
        BaseResponseDto baseResponseDto = new BaseResponseDto();
        TransactionToSystemRequestDto transactionToSystemRequestDto = new TransactionToSystemRequestDto();

        transactionToSystemRequestDto.setTargetUid(uid);
        transactionToSystemRequestDto.setAmount(Double.parseDouble(stockBuyRequestDto.getConclusion_price()) / 100);

        BaseResponseDto transactionResult = stockCoinService.buyStock(transactionToSystemRequestDto);

        if (transactionResult.isSuccess()) {
            log.info("[buyStock] stock 거래 성공 후 DB 저장");
            Stock foundStock = stockRepository.findByUserIdAndStockCode(userRepository.getByUid(uid).getId(), stockBuyRequestDto.getStockCode());
            if(foundStock == null) {
                log.info("[buyStock] 신규 매수 종목 코드 : {}", stockBuyRequestDto.getStockCode());
                Stock stock = new Stock();
                stock.setStockUrl(getCompanyInfoByStockId(stockBuyRequestDto.getStockCode()).getStockLogoUrl()); // 로고
                stock.setStockCode(stockBuyRequestDto.getStockCode()); // 종목 코드
                stock.setStockName(getStockNameByStockId(stockBuyRequestDto.getStockCode())); // 종목 이름

                /*수익률 = ((현재 가격 × 보유 주식 개수) - (구매 가격 × 보유 주식 개수)) / (구매 가격 × 보유 주식 개수) × 100*/
                Integer myAmount = Integer.parseInt(stockBuyRequestDto.getStockAmount());
                Double real_price = Double.parseDouble(getCurrentPrice(stockBuyRequestDto.getStockCode()));
                Integer my_buy_price = Integer.parseInt(stockBuyRequestDto.getConclusion_price());

                Double rate = ((real_price * myAmount) - (my_buy_price * myAmount)) / (my_buy_price * myAmount) * 100;
                rate = Math.round(rate * 1000) / 1000.0;
                stock.setRate(rate); // 수익률

                stock.setStockAmount(Integer.valueOf(stockBuyRequestDto.getStockAmount())); // 총 종목 수량

                Integer conclusion_price = Integer.valueOf(getCurrentPrice(stockBuyRequestDto.getStockCode()));
                Integer conclusion_amount = Integer.parseInt(stockBuyRequestDto.getStockAmount());
                Integer current_price = conclusion_price * conclusion_amount;
                stock.setReal_sum_coin_price(current_price/100); // 실제 종목의 평가 총 코인 가격

                stock.setReal_sum_price(current_price); // 실제 종목의 평가 총 가격

                Integer my_conclusion_price = Integer.valueOf(stockBuyRequestDto.getConclusion_price());
                stock.setMy_conclusion_sum_coin((my_conclusion_price * conclusion_amount)/100); // 내 체결 총 코인 가격

                stock.setMy_conclusion_sum_price(my_conclusion_price * conclusion_amount); // 내 체결 총 가격

                stock.setMy_per_conclusion_coin(Integer.parseInt(stockBuyRequestDto.getConclusion_price())/100); // 보유 평 코인

                stock.setMy_per_conclusion_price(Integer.valueOf(stockBuyRequestDto.getConclusion_price())); // 평 단가

                stock.setReal_per_coin(Integer.parseInt(getCurrentPrice(stockBuyRequestDto.getStockCode()))/100); // 실제 평 코인

                stock.setReal_per_price(Integer.valueOf(getCurrentPrice(stockBuyRequestDto.getStockCode()))); // 실제 평 단가

                User user = userRepository.getByUid(uid);

                List<Favorite> favoriteList = favoriteRepository.findAllByUserId(user.getId());
                boolean isFavoriteSet = false;
                if (!favoriteList.isEmpty()) { // 찜 여부
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

                stock.setUser(user); // 유저 저장
                stockRepository.save(stock); // Entity 저장
            } else {
                log.info("[withdrawStockCoinToSystem]stock 거래 성공 후 DB 저장");
                log.info("[buyStock] 기존 보유 종목 코드 : {}", stockBuyRequestDto.getStockCode());
//                Stock findStock = foundStock;
//                Stock findStock = stockRepository.findByStockCode(stockBuyRequestDto.getStockCode());

                Integer myAmount = foundStock.getStockAmount() + Integer.parseInt(stockBuyRequestDto.getStockAmount());
                foundStock.setStockAmount(myAmount); // 총 보유 수량

                Integer current_price = Integer.parseInt(getCurrentPrice(stockBuyRequestDto.getStockCode()));
                Integer compare_price = current_price * myAmount;
                Integer my_total_price = foundStock.getMy_conclusion_sum_price() + (Integer.parseInt(stockBuyRequestDto.getConclusion_price()) * Integer.parseInt(stockBuyRequestDto.getStockAmount()));
                Double rate = (((double) compare_price - my_total_price) / my_total_price) * 100;
                rate = Math.round(rate * 1000) / 1000.0;
                foundStock.setRate(rate); // 수익률

                foundStock.setReal_sum_coin_price(((Integer.parseInt(getCurrentPrice(stockBuyRequestDto.getStockCode())))*myAmount) / 100); // 실제 종목의 평가 총 코인 가격

                foundStock.setReal_sum_price(Integer.parseInt(getCurrentPrice(stockBuyRequestDto.getStockCode()))*myAmount); // 실제 종목의 평가 총 가격

                foundStock.setMy_conclusion_sum_coin(my_total_price / 100); // 내 체결 총 코인 가격

                foundStock.setMy_conclusion_sum_price(my_total_price); // 내 체결 총 가격

                foundStock.setMy_per_conclusion_coin((my_total_price / myAmount) / 100); // 보유 평 코인

                foundStock.setMy_per_conclusion_price(my_total_price / myAmount); // 평 단가

                foundStock.setReal_per_coin(Integer.parseInt(getCurrentPrice(stockBuyRequestDto.getStockCode()))/100); // 실제 평 코인

                foundStock.setReal_per_price(Integer.parseInt(getCurrentPrice(stockBuyRequestDto.getStockCode()))); // 실제 평 단가

                User user = userRepository.getByUid(uid);
                List<Favorite> favoriteList = favoriteRepository.findAllByUserId(user.getId());
                boolean isFavoriteSet = false;
                if (!favoriteList.isEmpty()) { // 찜 여부
                    for (Favorite favorite : favoriteList) {
                        if (favorite.getStockId().equals(stockBuyRequestDto.getStockCode())) {
                            foundStock.setFavorite_status(true);
                            isFavoriteSet = true;
                            break;
                        }
                    }
                }
                if (!isFavoriteSet) {
                    foundStock.setFavorite_status(false);
                }
                stockRepository.save(foundStock);
            }

            StockTransaction stockTransaction = StockTransaction.builder()
                    .stockCode(stockBuyRequestDto.getStockCode())
                    .stockName(getCompanyInfoByStockId(stockBuyRequestDto.getStockCode()).getStockName())
                    .quantity(Integer.parseInt(stockBuyRequestDto.getStockAmount()))
                    .isPurchase(true)
                    .transactionDate(LocalDateTime.now())
                    .unitPrice(Double.parseDouble(stockBuyRequestDto.getConclusion_price()))
                    .stockPrice((int) (Double.parseDouble(stockBuyRequestDto.getConclusion_price()) * Integer.parseInt(stockBuyRequestDto.getStockAmount()) /100))
                    .user(userRepository.findByUid(uid))
                    .build();

            stockTransactionRepository.save(stockTransaction);

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
        stockSellRequestDto.setSell_price(stockSellRequestDto.getSell_price().replace(",", ""));
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

                Integer savedAmount = findStock.getStockAmount();
                Integer sellAmount = Integer.parseInt(stockSellRequestDto.getStockAmount());
                Integer myAmount = savedAmount - sellAmount;
                findStock.setStockAmount(myAmount); // 총 종목 수량

                if (myAmount > 0) {
                    Double savedPrice = Double.valueOf(findStock.getMy_conclusion_sum_price());
                    Double sellPrice = Double.valueOf(stockSellRequestDto.getSell_price());
                    Double fixedPrice = savedPrice - (sellPrice * sellAmount);
                    Double current_price = Double.valueOf(getCurrentPrice(stockSellRequestDto.getStockCode()));
                    /*수익률 = ((현재 가격 × 보유 주식 개수) - (구매 가격 × 보유 주식 개수)) / (구매 가격 × 보유 주식 개수) × 100*/

                    Double rate;
                    if (fixedPrice != 0) {
                        rate = (((current_price * myAmount) - fixedPrice) / fixedPrice) * 100;
                        rate = Math.round(rate * 1000) / 1000.0;
                        findStock.setRate(rate); // 수익률
                    } else {
                        findStock.setRate((double) 0);
                    }

                    findStock.setReal_sum_coin_price((Integer.parseInt(getCurrentPrice(stockSellRequestDto.getStockCode()))*myAmount)/100); // 실제 종목의 평가 총 코인 가격

                    findStock.setReal_sum_price(Integer.parseInt(getCurrentPrice(stockSellRequestDto.getStockCode()))*myAmount); // 실제 종목의 평가 총 가격

                    Integer myPrice = findStock.getMy_conclusion_sum_price();

                    findStock.setMy_conclusion_sum_coin(((myPrice - Integer.parseInt(stockSellRequestDto.getSell_price()) * sellAmount))/100); // 내 체결 총 코인 가격

                    findStock.setMy_conclusion_sum_price(myPrice - Integer.parseInt(stockSellRequestDto.getSell_price())*sellAmount); // 내 체결 총 가격

                    findStock.setMy_per_conclusion_coin((((myPrice - Integer.parseInt(stockSellRequestDto.getSell_price())*sellAmount))/100)/myAmount); // 보유 평 코인

                    findStock.setMy_per_conclusion_price((myPrice - Integer.parseInt(stockSellRequestDto.getSell_price())*sellAmount)/myAmount); // 평 단가

                    findStock.setReal_per_coin(Integer.parseInt(getCurrentPrice(stockSellRequestDto.getStockCode()))/100); // 실제 평 코인

                    findStock.setReal_per_price(Integer.valueOf(getCurrentPrice(stockSellRequestDto.getStockCode()))); // 실제 평 단가

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
                    StockTransaction stockTransaction = StockTransaction.builder()
                            .stockCode(stockSellRequestDto.getStockCode())
                            .stockName(getCompanyInfoByStockId(stockSellRequestDto.getStockCode()).getStockName())
                            .quantity(Integer.parseInt(stockSellRequestDto.getStockAmount()))
                            .isPurchase(false)
                            .transactionDate(LocalDateTime.now())
                            .unitPrice(Double.parseDouble(stockSellRequestDto.getSell_price()))
                            .stockPrice((int) (Double.parseDouble(stockSellRequestDto.getSell_price()) * Integer.parseInt(stockSellRequestDto.getStockAmount())/100))
                            .user(userRepository.findByUid(uid))
                            .build();

                    stockTransactionRepository.save(stockTransaction);

                    stockRepository.save(findStock);
                    baseResponseDto.setMsg("주식 매도가 완료되었습니다.");
                    baseResponseDto.setSuccess(true);
                } else if (myAmount == 0) {
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

    @Override
    public String getUsersStockQuantity(String uid, String stockId) {
        Long userId = userRepository.getByUid(uid).getId();
        List<Stock> stock = stockRepository.findByUserId(userId);

        Integer quantity = null;

        for(Stock temp : stock) {
            if(temp.getStockCode().equals(stockId)) {
                quantity = temp.getStockAmount();
            }
        }

        return String.valueOf(quantity);
    }

    @Override
    public BaseResponseDto getNewUsersStockQuantity(String uid, String stockId) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();
        Long userId = userRepository.getByUid(uid).getId();
        List<Stock> stock = stockRepository.findByUserId(userId);

        Integer quantity = null;

        for(Stock temp : stock) {
            if(temp.getStockCode().equals(stockId)) {
                quantity = temp.getStockAmount();
            }
        }

        baseResponseDto.setSuccess(true);
        baseResponseDto.setMsg(String.valueOf(quantity));

        return baseResponseDto;
    }

    @Override
    public List<OwnedStockResponseDto> getUserStock(String uid) {
        User user = userRepository.findByUid(uid);
        List<Stock> stockList = stockRepository.findAllByUser(user);
        if(stockList.isEmpty()){
            return null;
        }
        List<OwnedStockResponseDto> ownedStockResponseDtoList = new ArrayList<>();

        for (Stock temp : stockList){
            NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
            OwnedStockResponseDto ownedStockResponseDto = new OwnedStockResponseDto();
            ownedStockResponseDto.setStockUrl(temp.getStockUrl());
            ownedStockResponseDto.setStockCode(temp.getStockCode());
            ownedStockResponseDto.setStockName(temp.getStockName());
            int current_price = Integer.parseInt(getCurrentPrice(temp.getStockCode()));
            int amount = temp.getStockAmount();
            int my_price = temp.getMy_conclusion_sum_price();

            Double rate = (double) ((((current_price * amount) - my_price) / (double) my_price) * 100);
            rate = Math.round(rate * 1000) / 1000.0;

            ownedStockResponseDto.setRate(rate);
            ownedStockResponseDto.setStockAmount(temp.getStockAmount());


            ownedStockResponseDto.setReal_sum_coin_price(nf.format((current_price * amount) / 100));
            ownedStockResponseDto.setReal_sum_price(nf.format(current_price * amount));
            ownedStockResponseDto.setMy_conclusion_sum_coin(nf.format(temp.getMy_conclusion_sum_coin()));
            ownedStockResponseDto.setMy_conclusion_sum_price(nf.format(temp.getMy_conclusion_sum_price()));
            ownedStockResponseDto.setMy_per_conclusion_coin(nf.format(temp.getMy_per_conclusion_coin()));
            ownedStockResponseDto.setMy_per_conclusion_price(nf.format(temp.getMy_per_conclusion_price()));
            ownedStockResponseDto.setReal_per_coin(nf.format(current_price / 100));
            ownedStockResponseDto.setReal_per_price(nf.format(current_price));

            List<Favorite> favoriteList = favoriteRepository.findAllByUserId(user.getId());
            boolean isFavoriteSet = false;
            if (!favoriteList.isEmpty()) {
                for (Favorite favorite : favoriteList) {
                    if (favorite.getStockId().equals(temp.getStockCode())) {
                        ownedStockResponseDto.setFavorite_status(true);
                        isFavoriteSet = true;
                        break;
                    }
                }
            }
            if (!isFavoriteSet) {
                ownedStockResponseDto.setFavorite_status(false);
            }
            ownedStockResponseDtoList.add(ownedStockResponseDto);
        }
        return ownedStockResponseDtoList;
    }

    @Override
    public List<StockTransactionHistoryResponseDto> getStockTransactionHistory(String uid) {
        List<StockTransactionHistoryResponseDto> stockTransactionHistoryResponseDtoList = new ArrayList<>();

        List<StockTransaction> stockTransactions = stockTransactionRepository.findByUserId(userRepository.getByUid(uid).getId());

        for(StockTransaction temp : stockTransactions) {
            StockTransactionHistoryResponseDto stockTransactionHistoryResponseDto = new StockTransactionHistoryResponseDto();
            stockTransactionHistoryResponseDto.setTransactionDate(temp.getTransactionDate().toString());
            stockTransactionHistoryResponseDto.setStockCode(temp.getStockCode());
            stockTransactionHistoryResponseDto.setStockName(temp.getStockName());
            stockTransactionHistoryResponseDto.setUnitPrice((int) temp.getUnitPrice());
            stockTransactionHistoryResponseDto.setStatus(temp.isPurchase());
            stockTransactionHistoryResponseDto.setQuantity(temp.getQuantity());

            Integer stockPriceInteger = (int) ((temp.getUnitPrice() * temp.getQuantity()) / 100);
            if(stockPriceInteger != null) {
                stockTransactionHistoryResponseDto.setStockPrice(stockPriceInteger);
            }else {
                stockTransactionHistoryResponseDto.setStockPrice(0);
            }

            stockTransactionHistoryResponseDto.setStockLogo(getCompanyInfoByStockId(temp.getStockCode()).getStockLogoUrl());
            stockTransactionHistoryResponseDtoList.add(stockTransactionHistoryResponseDto);
        }

        return stockTransactionHistoryResponseDtoList;
    }

    @Override
    public BaseResponseDto calculateCoin(int amount, String price) {
        String numberWithoutCommas = price.replace(",", "");
        NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
        int result = (amount * Integer.parseInt(numberWithoutCommas)) / 100;

        String formattedResult = nf.format(result) + " ";

        BaseResponseDto baseResponseDto = new BaseResponseDto();

        baseResponseDto.setSuccess(true);
        baseResponseDto.setMsg(formattedResult);

        return baseResponseDto;
    }
}

