package org.knulikelion.moneyisinvest.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.knulikelion.moneyisinvest.data.dto.response.*;
import org.knulikelion.moneyisinvest.data.entity.StockHoliday;
import org.knulikelion.moneyisinvest.data.repository.StockHolidayRepository;
import org.knulikelion.moneyisinvest.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StockServiceImpl implements StockService {
    private final StockHolidayRepository stockHolidayRepository;

    @Autowired
    public StockServiceImpl(StockHolidayRepository stockHolidayRepository) {
        this.stockHolidayRepository = stockHolidayRepository;
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

                if(trElement.select("td.title a").text().isEmpty()) {
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
                Element metaElement = newsDocument.select("head meta[property=og:image]").first();

                if(metaElement != null) {
                    stockCompanyNewsResponseDto.setNewsThumbnail(metaElement.attr("content"));
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
            if(temp.getDate().toString().equals(currentDate.format(formatter))) {
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
        }
        else {
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

        for(StockHoliday temp : getHolidays) {
            HolidayResponseDto holidayResponseDto = new HolidayResponseDto();

            holidayResponseDto.setDate(temp.getDate().toString());
            holidayResponseDto.setReason(temp.getReason());

            holidayResponseDtoList.add(holidayResponseDto);
        }

        return holidayResponseDtoList;
    }
}
