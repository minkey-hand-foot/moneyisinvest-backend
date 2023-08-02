package org.knulikelion.moneyisinvest.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.knulikelion.moneyisinvest.data.dto.response.StockCompanyInfoResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.StockCompanyNewsResponseDto;
import org.knulikelion.moneyisinvest.service.StockService;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class StockServiceImpl implements StockService {
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

                stockCompanyNewsList.add(stockCompanyNewsResponseDto);
            }

            return stockCompanyNewsList;
        } catch (IOException e) {
            System.out.println("Error fetching data: " + e.getMessage());
        }

        return null;
    }
}
