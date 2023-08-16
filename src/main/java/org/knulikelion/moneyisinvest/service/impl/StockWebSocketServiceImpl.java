package org.knulikelion.moneyisinvest.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.knulikelion.moneyisinvest.data.dto.response.*;
import org.knulikelion.moneyisinvest.service.StockService;
import org.knulikelion.moneyisinvest.service.StockWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Slf4j
@Service
public class StockWebSocketServiceImpl implements StockWebSocketService {
    private String approvalToken;

    @Value("${KIS.APP.KEY}")
    private String app_Key;

    @Value("${KIS.APP.SECRET}")
    private String app_Secret;

    private final StockService stockService;
    @Autowired
    public StockWebSocketServiceImpl(StockService stockService) {
        this.stockService = stockService;
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

    @Override /*종목 코드로 종목 데이터 가져오는 메서드 입니다.*/
    public StockPriceResponseDto getStock(String stockCode) throws IOException, JSONException {
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
                stockPriceResponseDto.setStock_status_code((String) outputs.get("iscd_stat_cls_code"));
                stockPriceResponseDto.setStock_market_index((String) outputs.get("rprs_mrkt_kor_name"));
                stockPriceResponseDto.setBusiness_type((String) outputs.get("bstp_kor_isnm"));
                stockPriceResponseDto.setStock_price((String) outputs.get("stck_prpr"));
                stockPriceResponseDto.setPreparation_day_before((String) outputs.get("prdy_vrss"));
                stockPriceResponseDto.setPreparation_day_before_sign((String) outputs.get("prdy_vrss_sign"));
                stockPriceResponseDto.setPreparation_day_before_rate((String) outputs.get("prdy_ctrt"));
                stockPriceResponseDto.setStock_open_price((String) outputs.get("stck_oprc"));
                stockPriceResponseDto.setStock_high_price((String) outputs.get("stck_hgpr"));
                stockPriceResponseDto.setStock_low_price((String) outputs.get("stck_lwpr"));
                stockPriceResponseDto.setStock_max_price((String) outputs.get("stck_mxpr"));
                stockPriceResponseDto.setStock_price_floor((String) outputs.get("stck_llam"));
                stockPriceResponseDto.setStock_base_price((String) outputs.get("stck_sdpr"));
                stockPriceResponseDto.setWeighted_average_stock_price((String) outputs.get("wghn_avrg_stck_prc"));
                stockPriceResponseDto.setPer((String) outputs.get("per"));
                stockPriceResponseDto.setPbr((String) outputs.get("pbr"));
                return stockPriceResponseDto;
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

    @Override /*종목 거래량 순위를 가져오는 코드 입니다.*/
    public List<StockRankResponseDto> getStockRank() throws IOException, JSONException {
        HttpUrl.Builder urlBuilder = HttpUrl.parse("https://openapi.koreainvestment.com:9443/uapi/domestic-stock/v1/quotations/volume-rank").newBuilder();
        urlBuilder.addQueryParameter("FID_COND_MRKT_DIV_CODE", "J");
        urlBuilder.addQueryParameter("FID_COND_SCR_DIV_CODE", "20171");
        urlBuilder.addQueryParameter("FID_INPUT_ISCD", "0000");/*0000(전체) 기타(업종코드)*/
        urlBuilder.addQueryParameter("FID_DIV_CLS_CODE", "0");/*0(전체) 1(보통주) 2(우선주)*/
        urlBuilder.addQueryParameter("FID_BLNG_CLS_CODE", "1");/*0 : 평균거래량 1:거래증가율 2:평균거래회전율 3:거래금액순 4:평균거래금액회전율*/
        urlBuilder.addQueryParameter("FID_TRGT_CLS_CODE", "111111111");/*1 or 0 9자리 (차례대로 증거금 30% 40% 50% 60% 100% 신용보증금 30% 40% 50% 60%)*/
        urlBuilder.addQueryParameter("FID_TRGT_EXLS_CLS_CODE", "000000");/*1 or 0 6자리 (차례대로 투자위험/경고/주의 관리종목 정리매매 불성실공시 우선주 거래정지)*/
        urlBuilder.addQueryParameter("FID_INPUT_PRICE_1", "100"); /*가격 ~ ex) "0"*/
        urlBuilder.addQueryParameter("FID_INPUT_PRICE_2", "1000000");/*~ 가격 ex) "1000000"*/
        urlBuilder.addQueryParameter("FID_VOL_CNT", "100000");/*거래량 ~ ex) "100000"*/
        urlBuilder.addQueryParameter("FID_INPUT_DATE_1", "");

        String url = urlBuilder.build().toString();/*한국 현재 주식 순위 url*/

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .header("authorization", "Bearer " + approvalToken)
                .header("appkey", app_Key)
                .header("appsecret", app_Secret)
                .header("tr_id", "FHPST01710000")
                .header("custtype", "P")
                .header("content-type", "application/json; charset=utf-8")
                .build();

        List<StockRankResponseDto> outputList = new ArrayList<>();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful() && response.body() != null) {
                JSONObject jsonObject = new JSONObject(response.body().string());
                JSONArray outputs = jsonObject.getJSONArray("output");
                System.out.println(outputs.toString());

                int limit = Math.min(outputs.length(), 5);
                for (int i = 0; i < limit; i++) { /*5등 까지만*/
                    JSONObject obj = outputs.getJSONObject(i);

                    StockRankResponseDto stockRank = new StockRankResponseDto();
                    stockRank.setStockName(obj.getString("hts_kor_isnm"));
                    stockRank.setStockUrl(stockService.getCompanyInfoByStockId(obj.getString("mksc_shrn_iscd")).getStockLogoUrl());
                    stockRank.setStockCode(obj.getString("mksc_shrn_iscd"));
                    double prdyCtrtDouble = Double.parseDouble(obj.getString("prdy_ctrt"));
                    long prdyCtrt = Math.round(prdyCtrtDouble);
                    if(prdyCtrt<0){
                        stockRank.setDay_before_status(false);
                    }else {
                        stockRank.setDay_before_status(true);
                    }
                    double stckPrprDouble = Double.parseDouble(obj.getString("stck_prpr"));
                    int coinPrice = (int) (stckPrprDouble / 100);
                    stockRank.setCoinPrice(String.valueOf(coinPrice));
                    stockRank.setRank(obj.getString("data_rank"));
                    stockRank.setStockPrice(obj.getString("stck_prpr"));
                    stockRank.setPreparation_day_before_rate(obj.getString("prdy_ctrt"));
                    outputList.add(stockRank);
                }
                return outputList;
            }
            return null;
        }
    }

    @Override /*Kospi 데이터를 가져오는 코드 입니다.*/
    public List<KospiResponseDto> getKospi() throws IOException {
        List<KospiResponseDto> outputList = new ArrayList<>();
//
        String google_url = "https://finance.naver.com/sise/sise_index_day.naver?code=KOSPI";
        Document doc = Jsoup.connect(google_url).get();

        // 6
        String kospi_date_selector = "body > div > table.type_1 > tbody > tr:nth-child(12) > td.date";
        Elements kospi_date_element = doc.select(kospi_date_selector);
        String kospi_date = kospi_date_element.text();

        String kospi_price_selector = "body > div > table.type_1 > tbody > tr:nth-child(12) > td:nth-child(2)";
        Elements kospi_price_element = doc.select(kospi_price_selector);
        String kospi_price = kospi_price_element.text();

        String kospi_rate_selector = "body > div > table.type_1 > tbody > tr:nth-child(12) > td:nth-child(4) > span";
        Elements kospi_rate_element = doc.select(kospi_rate_selector);
        String kospi_rate = kospi_rate_element.text();

        KospiResponseDto kospiResponseDto1 = new KospiResponseDto();
        kospiResponseDto1.setDate(kospi_date);
        kospiResponseDto1.setPrice(kospi_price);
        kospiResponseDto1.setRate(kospi_rate);

        outputList.add(kospiResponseDto1);

        // 5
        kospi_date_selector = "body > div > table.type_1 > tbody > tr:nth-child(11) > td.date";
        kospi_date_element = doc.select(kospi_date_selector);
        kospi_date = kospi_date_element.text();

        kospi_price_selector = "body > div > table.type_1 > tbody > tr:nth-child(11) > td:nth-child(2)";
        kospi_price_element = doc.select(kospi_price_selector);
        kospi_price = kospi_price_element.text();

        kospi_rate_selector = "body > div > table.type_1 > tbody > tr:nth-child(11) > td:nth-child(4) > span";
        kospi_rate_element = doc.select(kospi_rate_selector);
        kospi_rate = kospi_rate_element.text();

        KospiResponseDto kospiResponseDto2 = new KospiResponseDto();
        kospiResponseDto2.setDate(kospi_date);
        kospiResponseDto2.setPrice(kospi_price);
        kospiResponseDto2.setRate(kospi_rate);
        outputList.add(kospiResponseDto2);

        // 4
        kospi_date_selector = "body > div > table.type_1 > tbody > tr:nth-child(10) > td.date";
        kospi_date_element = doc.select(kospi_date_selector);
        kospi_date = kospi_date_element.text();

        kospi_price_selector = "body > div > table.type_1 > tbody > tr:nth-child(10) > td:nth-child(2)";
        kospi_price_element = doc.select(kospi_price_selector);
        kospi_price = kospi_price_element.text();

        kospi_rate_selector = "body > div > table.type_1 > tbody > tr:nth-child(10) > td:nth-child(4) > span";
        kospi_rate_element = doc.select(kospi_rate_selector);
        kospi_rate = kospi_rate_element.text();

        KospiResponseDto kospiResponseDto3 = new KospiResponseDto();
        kospiResponseDto3.setDate(kospi_date);
        kospiResponseDto3.setPrice(kospi_price);
        kospiResponseDto3.setRate(kospi_rate);
        outputList.add(kospiResponseDto3);


        // 3
        kospi_date_selector = "body > div > table.type_1 > tbody > tr:nth-child(5) > td.date";
        kospi_date_element = doc.select(kospi_date_selector);
        kospi_date = kospi_date_element.text();

        kospi_price_selector = "body > div > table.type_1 > tbody > tr:nth-child(5) > td:nth-child(2)";
        kospi_price_element = doc.select(kospi_price_selector);
        kospi_price = kospi_price_element.text();

        kospi_rate_selector = "body > div > table.type_1 > tbody > tr:nth-child(5) > td:nth-child(4) > span";
        kospi_rate_element = doc.select(kospi_rate_selector);
        kospi_rate = kospi_rate_element.text();

        KospiResponseDto kospiResponseDto4 = new KospiResponseDto();
        kospiResponseDto4.setDate(kospi_date);
        kospiResponseDto4.setPrice(kospi_price);
        kospiResponseDto4.setRate(kospi_rate);
        outputList.add(kospiResponseDto4);

        // 2
        kospi_date_selector = "body > div > table.type_1 > tbody > tr:nth-child(4) > td.date";
        kospi_date_element = doc.select(kospi_date_selector);
        kospi_date = kospi_date_element.text();

        kospi_price_selector = "body > div > table.type_1 > tbody > tr:nth-child(4) > td:nth-child(2)";
        kospi_price_element = doc.select(kospi_price_selector);
        kospi_price = kospi_price_element.text();

        kospi_rate_selector = "body > div > table.type_1 > tbody > tr:nth-child(5) > td:nth-child(4) > span";
        kospi_rate_element = doc.select(kospi_rate_selector);
        kospi_rate = kospi_rate_element.text();

        KospiResponseDto kospiResponseDto5 = new KospiResponseDto();
        kospiResponseDto5.setDate(kospi_date);
        kospiResponseDto5.setPrice(kospi_price);
        kospiResponseDto5.setRate(kospi_rate);
        outputList.add(kospiResponseDto5);

        // 1
        kospi_date_selector = "body > div > table.type_1 > tbody > tr:nth-child(3) > td.date";
        kospi_date_element = doc.select(kospi_date_selector);
        kospi_date = kospi_date_element.text();

        kospi_price_selector = "body > div > table.type_1 > tbody > tr:nth-child(3) > td:nth-child(2)";
        kospi_price_element = doc.select(kospi_price_selector);
        kospi_price = kospi_price_element.text();

        kospi_rate_selector = "body > div > table.type_1 > tbody > tr:nth-child(3) > td:nth-child(4) > span";
        kospi_rate_element = doc.select(kospi_rate_selector);
        kospi_rate = kospi_rate_element.text();

        KospiResponseDto kospiResponseDto6 = new KospiResponseDto();
        kospiResponseDto6.setDate(kospi_date);
        kospiResponseDto6.setPrice(kospi_price);
        kospiResponseDto6.setRate(kospi_rate);
        outputList.add(kospiResponseDto6);

        // 현재 코스피 지수
        String naver_url = "https://finance.naver.com/sise/";
        doc = Jsoup.connect(naver_url).get();

        kospi_price_selector = "#KOSPI_now";
        kospi_price_element = doc.select(kospi_price_selector);
        kospi_price = kospi_price_element.text();

        kospi_rate_selector = "#KOSPI_change";
        kospi_rate_element = doc.select(kospi_rate_selector);
        kospi_rate = kospi_rate_element.text();
        kospi_rate = kospi_rate.replace("상승","");

        KospiResponseDto kospiResponseDto7 = new KospiResponseDto();

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedDate = dateTime.format(outputFormatter);

        kospiResponseDto7.setDate(formattedDate);
        kospiResponseDto7.setPrice(kospi_price);
        kospiResponseDto7.setRate(kospi_rate);
        outputList.add(kospiResponseDto7);

        return outputList;
    }

    @Override /*Kosdaq 데이터를 가져오는 메서드입니다.*/
    public List<KosdaqResponseDto> getKosdaq() throws IOException {
        List<KosdaqResponseDto> outputList = new ArrayList<>();
        String google_url = "https://finance.naver.com/sise/sise_index_day.naver?code=KOSDAQ";
        Document doc = Jsoup.connect(google_url).get();

        // 6
        String kosdaq_selector = "body > div > table.type_1 > tbody > tr:nth-child(12) > td.date";
        Elements kosdaq_date_element = doc.select(kosdaq_selector);
        String kosdaq_date = kosdaq_date_element.text();

        String kosdaq_price_selector = "body > div > table.type_1 > tbody > tr:nth-child(12) > td:nth-child(2)";
        Elements kosdaq_price_element = doc.select(kosdaq_price_selector);
        String kosdaq_price = kosdaq_price_element.text();

        String kosdaq_rate_selector = "body > div > table.type_1 > tbody > tr:nth-child(12) > td:nth-child(4) > span";
        Elements kosdaq_rate_element = doc.select(kosdaq_rate_selector);
        String kosdaq_rate = kosdaq_rate_element.text();

        KosdaqResponseDto kosdaqResponseDto1 = new KosdaqResponseDto();
        kosdaqResponseDto1.setDate(kosdaq_date);
        kosdaqResponseDto1.setPrice(kosdaq_price);
        kosdaqResponseDto1.setRate(kosdaq_rate);
        outputList.add(kosdaqResponseDto1);

        // 2
        kosdaq_selector = "body > div > table.type_1 > tbody > tr:nth-child(11) > td.date";
        kosdaq_date_element = doc.select(kosdaq_selector);
        kosdaq_date = kosdaq_date_element.text();

        kosdaq_price_selector = "body > div > table.type_1 > tbody > tr:nth-child(11) > td:nth-child(2)";
        kosdaq_price_element = doc.select(kosdaq_price_selector);
        kosdaq_price = kosdaq_price_element.text();

        kosdaq_rate_selector = "body > div > table.type_1 > tbody > tr:nth-child(11) > td:nth-child(4) > span";
        kosdaq_rate_element = doc.select(kosdaq_rate_selector);
        kosdaq_rate = kosdaq_rate_element.text();

        KosdaqResponseDto kosdaqResponseDto2 = new KosdaqResponseDto();
        kosdaqResponseDto2.setDate(kosdaq_date);
        kosdaqResponseDto2.setPrice(kosdaq_price);
        kosdaqResponseDto2.setRate(kosdaq_rate);
        outputList.add(kosdaqResponseDto2);

        // 4
        kosdaq_selector = "body > div > table.type_1 > tbody > tr:nth-child(10) > td.date";
        kosdaq_date_element = doc.select(kosdaq_selector);
        kosdaq_date = kosdaq_date_element.text();

        kosdaq_price_selector = "body > div > table.type_1 > tbody > tr:nth-child(10) > td:nth-child(2)";
        kosdaq_price_element = doc.select(kosdaq_price_selector);
        kosdaq_price = kosdaq_price_element.text();

        kosdaq_rate_selector = "body > div > table.type_1 > tbody > tr:nth-child(10) > td:nth-child(4) > span";
        kosdaq_rate_element = doc.select(kosdaq_rate_selector);
        kosdaq_rate = kosdaq_rate_element.text();

        KosdaqResponseDto kosdaqResponseDto3 = new KosdaqResponseDto();
        kosdaqResponseDto3.setDate(kosdaq_date);
        kosdaqResponseDto3.setPrice(kosdaq_price);
        kosdaqResponseDto3.setRate(kosdaq_rate);
        outputList.add(kosdaqResponseDto3);

        // 3
        kosdaq_selector = "body > div > table.type_1 > tbody > tr:nth-child(5) > td.date";
        kosdaq_date_element = doc.select(kosdaq_selector);
        kosdaq_date = kosdaq_date_element.text();

        kosdaq_price_selector = "body > div > table.type_1 > tbody > tr:nth-child(5) > td:nth-child(2)";
        kosdaq_price_element = doc.select(kosdaq_price_selector);
        kosdaq_price = kosdaq_price_element.text();

        kosdaq_rate_selector = "body > div > table.type_1 > tbody > tr:nth-child(5) > td:nth-child(4) > span";
        kosdaq_rate_element = doc.select(kosdaq_rate_selector);
        kosdaq_rate = kosdaq_rate_element.text();

        KosdaqResponseDto kosdaqResponseDto4 = new KosdaqResponseDto();
        kosdaqResponseDto4.setDate(kosdaq_date);
        kosdaqResponseDto4.setPrice(kosdaq_price);
        kosdaqResponseDto4.setRate(kosdaq_rate);
        outputList.add(kosdaqResponseDto4);

        // 2
        kosdaq_selector = "body > div > table.type_1 > tbody > tr:nth-child(4) > td.date";
        kosdaq_date_element = doc.select(kosdaq_selector);
        kosdaq_date = kosdaq_date_element.text();

        kosdaq_price_selector = "body > div > table.type_1 > tbody > tr:nth-child(4) > td:nth-child(2)";
        kosdaq_price_element = doc.select(kosdaq_price_selector);
        kosdaq_price = kosdaq_price_element.text();

        kosdaq_rate_selector = "body > div > table.type_1 > tbody > tr:nth-child(4) > td:nth-child(4) > span";
        kosdaq_rate_element = doc.select(kosdaq_rate_selector);
        kosdaq_rate = kosdaq_rate_element.text();

        KosdaqResponseDto kosdaqResponseDto5 = new KosdaqResponseDto();
        kosdaqResponseDto5.setDate(kosdaq_date);
        kosdaqResponseDto5.setPrice(kosdaq_price);
        kosdaqResponseDto5.setRate(kosdaq_rate);
        outputList.add(kosdaqResponseDto5);

        // 1
        kosdaq_selector = "body > div > table.type_1 > tbody > tr:nth-child(3) > td.date";
        kosdaq_date_element = doc.select(kosdaq_selector);
        kosdaq_date = kosdaq_date_element.text();

        kosdaq_price_selector = "body > div > table.type_1 > tbody > tr:nth-child(3) > td:nth-child(2)";
        kosdaq_price_element = doc.select(kosdaq_price_selector);
        kosdaq_price = kosdaq_price_element.text();

        kosdaq_rate_selector = "body > div > table.type_1 > tbody > tr:nth-child(3) > td:nth-child(4) > span";
        kosdaq_rate_element = doc.select(kosdaq_rate_selector);
        kosdaq_rate = kosdaq_rate_element.text();

        KosdaqResponseDto kosdaqResponseDto6 = new KosdaqResponseDto();
        kosdaqResponseDto6.setDate(kosdaq_date);
        kosdaqResponseDto6.setPrice(kosdaq_price);
        kosdaqResponseDto6.setRate(kosdaq_rate);
        outputList.add(kosdaqResponseDto6);

        // 현재 코스닥 지수
        String naver_url = "https://finance.naver.com/sise/";
        doc = Jsoup.connect(naver_url).get();

        kosdaq_price_selector = "#KOSDAQ_now";
        kosdaq_price_element = doc.select(kosdaq_price_selector);
        kosdaq_price = kosdaq_price_element.text();

        kosdaq_rate_selector = "#KOSDAQ_change";
        kosdaq_rate_element = doc.select(kosdaq_rate_selector);
        kosdaq_rate = kosdaq_rate_element.text();
        kosdaq_rate = kosdaq_rate.replace("상승","");

        KosdaqResponseDto kosdaqResponseDto7 = new KosdaqResponseDto();

        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd");
        LocalDateTime dateTime = LocalDateTime.now();
        String formattedDate = dateTime.format(outputFormatter);

        kosdaqResponseDto7.setDate(formattedDate);
        kosdaqResponseDto7.setPrice(kosdaq_price);
        kosdaqResponseDto7.setRate(kosdaq_rate);
        outputList.add(kosdaqResponseDto7);

        return outputList;
    }
}
