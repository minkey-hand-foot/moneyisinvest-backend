package org.knulikelion.moneyisinvest.service.impl;

import lombok.extern.slf4j.Slf4j;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.knulikelion.moneyisinvest.config.security.KISApprovalTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.response.*;
import org.knulikelion.moneyisinvest.service.MessageQueueService;
import org.knulikelion.moneyisinvest.service.StockService;
import org.knulikelion.moneyisinvest.service.StockWebSocketService;
import org.springframework.beans.factory.annotation.Autowired;

import org.json.JSONException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import yahoofinance.Stock;
import yahoofinance.YahooFinance;

import javax.print.Doc;
import java.io.IOException;
import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
@Slf4j
@Service
public class StockWebSocketServiceImpl implements StockWebSocketService {
    private final StockService stockService;
    private final MessageQueueService messageQueueService;
    private static final String GET_STOCK_INFO_API_URL = "https://finance.naver.com/item/main.naver?code=";
    private static final String GET_STOCK_RANK_API_URL = "https://finance.naver.com/sise/";
    private static final String KOSPI_INFO_URL = "https://finance.naver.com/sise/sise_index_day.naver?code=KOSPI";
    private static final String KOSDAQ_INFO_URL = "https://finance.naver.com/sise/sise_index_day.naver?code=KOSDAQ";
    private static final String NAVER_SISE_URL = "https://finance.naver.com/sise/";

    @Autowired
    public StockWebSocketServiceImpl(StockService stockService, MessageQueueService messageQueueService) {
        this.stockService = stockService;
        this.messageQueueService = messageQueueService;
    }

    private String changeJSoup(Document doc, String Selector){
        Elements elements = doc.select(Selector);
        return elements.text();
    }

    private String mergePrice(Document doc, String selector){
        Elements elements = doc.select(selector);

        if(elements.size() > 0){
            Elements spans = elements.get(0).select("span"); // 첫 번째 <em> 태그 안의 모든 <span> 태그를 선택
            if (spans.size() > 0) {
                return spans.get(0).text(); // 첫 번째 <span> 태그의 텍스트를 반환
            }
        }

        return "";
    }
    private String mergePrices(Document doc, String selector){
        StringBuilder sb = new StringBuilder();
        Elements elements = doc.select(selector);

        elements.forEach(e ->{
            String price = e.text();
            sb.append(price);
        });

        return sb.toString();
    }





    @Override /*종목 코드로 종목 데이터 가져오는 메서드 입니다.*/
    public StockPriceResponseDto getStock(String stockCode) throws IOException, JSONException {
        log.info("[getStock] : {}",stockCode);
        StockPriceResponseDto stockPriceResponseDto = new StockPriceResponseDto();
        String url = GET_STOCK_INFO_API_URL + stockCode;

        try {
            Document doc = Jsoup.connect(url).get(); // url 설정

            stockPriceResponseDto.setCurrent_time(String.valueOf(LocalDateTime.now())); // 현재 시간

            stockPriceResponseDto.setStock_status_code(changeJSoup(doc,"#time > em > span")); // 종목 상태

            String field = changeJSoup(doc,"#tab_con1 > div.first > table > tbody > tr:nth-child(2) > td");
            field = field.substring(0,3);
            stockPriceResponseDto.setStock_market_index(field.trim()); // 종목 시장

            stockPriceResponseDto.setBusiness_type(changeJSoup(doc,"#content > div.section.trade_compare > h4 > em > a")); // 업종

            NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
            Elements STOCK_PRICE = doc.select("#content > div.section.trade_compare > table > tbody > tr:nth-child(1) > td:nth-child(2)");
            String priceText = STOCK_PRICE.text().replace(",","");
            stockPriceResponseDto.setStock_price(nf.format(Double.parseDouble(priceText))); // 현재가

            stockPriceResponseDto.setStock_coin(nf.format(Double.parseDouble(priceText)/100)); // 스톡가

            String rate = changeJSoup(doc,"#content > div.section.trade_compare > table > tbody > tr:nth-child(2) > td:nth-child(2) > em");
            rate = rate.substring(3,6);

            stockPriceResponseDto.setPreparation_day_before(rate.trim()); // 전일 대비

            stockPriceResponseDto.setPreparation_day_before_sign("4"); // 전일 대비 부호

            String beforeRateString = changeJSoup(doc,"#content > div.section.trade_compare > table > tbody > tr:nth-child(3) > td:nth-child(2) > em");
            beforeRateString=beforeRateString.substring(0,2);
            stockPriceResponseDto.setPreparation_day_before_rate(beforeRateString); // 전일 대비율

            stockPriceResponseDto.setStock_open_price(mergePrice(doc, "#chart_area > div.rate_info > table > tbody > tr:nth-child(2) > td.first > em")); // 주식 시가

            stockPriceResponseDto.setStock_high_price(mergePrice(doc, "#chart_area > div.rate_info > table > tbody > tr:nth-child(1) > td:nth-child(2) > em.no_up")); // 주식 최고가

            stockPriceResponseDto.setStock_low_price(mergePrice(doc, "#chart_area > div.rate_info > table > tbody > tr:nth-child(2) > td:nth-child(2) > em.no_down")); // 주식 최저가

            stockPriceResponseDto.setStock_max_price(mergePrice(doc, "#chart_area > div.rate_info > table > tbody > tr:nth-child(1) > td:nth-child(2) > em.no_cha")); // 주식 상한가

            stockPriceResponseDto.setStock_price_floor(mergePrices(doc, "#chart_area > div.rate_info > table > tbody > tr:nth-child(2) > td:nth-child(2) > em.no_cha")); // 주식 하한가

            stockPriceResponseDto.setStock_base_price(mergePrices(doc, "#chart_area > div.rate_info > table > tbody > tr:nth-child(2) > td:nth-child(2) > em.no_cha")); // 주식 기준가

            stockPriceResponseDto.setWeighted_average_stock_price((stockPriceResponseDto.getStock_price())); // 가중 평균 주식 가격

            stockPriceResponseDto.setPer(changeJSoup(doc, "#content > div.section.trade_compare > table > tbody > tr:nth-child(13) > td:nth-child(2)")); // PER

            stockPriceResponseDto.setPbr(changeJSoup(doc, "#content > div.section.trade_compare > table > tbody > tr:nth-child(14) > td:nth-child(2)")); // PBR

            return stockPriceResponseDto;
        }catch (Exception e){
            log.error("Error fetching data : "+ e.getMessage());
        }
        return null;
    }

    @Override /*종목 거래량 순위를 가져오는 코드 입니다.*/
    public List<StockRankResponseDto> getStockRank() throws IOException, JSONException {
        log.info("[getStockRank] 1위부터 5위까지");
        Document doc = Jsoup.connect(GET_STOCK_RANK_API_URL).get();

        String SELECTOR_PREFIX = "#siselist_tab_0 > tbody";

        List<StockRankResponseDto> stockRankResponseDto = new ArrayList<>();

        for(int i=3; i<=7; i++){
            StockRankResponseDto dto = new StockRankResponseDto();

            dto.setStockName(changeJSoup(doc, SELECTOR_PREFIX+" > tr:nth-child("+i+") > td:nth-child(4) > a")); // 종목 이름

            String stockCode = stockService.searchStockByKeyword(dto.getStockName()).get(0).getStockId();
            dto.setStockCode(stockCode); // 종목 코드

            String url = stockService.getCompanyInfoByStockId(stockCode).getCompanyUrl();
            dto.setStockUrl(url); // url

            dto.setStockPrice(changeJSoup(doc, SELECTOR_PREFIX+" > tr:nth-child("+i+") > td:nth-child(5)")); // 현재가

            String stockPriceStr = dto.getStockPrice();
            if(stockPriceStr != null){
                try{
                    stockPriceStr = stockPriceStr.replace(",", ""); // 콤마 제거
                    int stockPrice = Integer.parseInt(stockPriceStr);
                    dto.setDay_before_status(stockPrice > 0); // 전일 대비 상황
                }catch (NumberFormatException e){
                    log.error("[getStockRank] 전일 대비 상황 NumberFormatException");
                }

                try{
                    int stockPrice = Integer.parseInt(stockPriceStr);
                    dto.setCoinPrice(String.valueOf(stockPrice / 100)); // 종목 스톡 값
                }catch (NumberFormatException e){
                    log.error("[getStockRank] 종목 스톡 값 NumberFormatException");
                }
            }
            dto.setRank(String.valueOf(i-2)); // 종목 순위
            dto.setPreparation_day_before_rate(changeJSoup(doc,SELECTOR_PREFIX+" > tr:nth-child("+i+") > td:nth-child(7) > span")); // 전일대비율

            stockRankResponseDto.add(dto);
        }
        return stockRankResponseDto;
    }

    @Override /*Kospi 데이터를 가져오는 코드 입니다.*/
    public List<KospiResponseDto> getKospi() throws IOException {
        List<KospiResponseDto> outputList = new ArrayList<>();
        Document doc = Jsoup.connect(KOSPI_INFO_URL).get();

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
        doc = Jsoup.connect(NAVER_SISE_URL).get();

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
        Document doc = Jsoup.connect(KOSDAQ_INFO_URL).get();

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
        doc = Jsoup.connect(NAVER_SISE_URL).get();

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
