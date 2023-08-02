package org.knulikelion.moneyisinvest.service.impl;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.knulikelion.moneyisinvest.data.dto.response.StockCompanyInfoResponseDto;
import org.knulikelion.moneyisinvest.service.StockService;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class StockServiceImpl implements StockService {
    @Override
    public StockCompanyInfoResponseDto getCompanyInfoByStockId(String stockId) {
        StockCompanyInfoResponseDto stockCompanyInfoResponseDto = new StockCompanyInfoResponseDto();
        String url = "https://comp.kisline.com/co/CO0100M010GE.nice?stockcd=" + stockId + "&nav=2&header=N"; // 크롤링할 URL 지정
        System.out.println(url);

        try {
            Document document = Jsoup.connect(url).get(); // URL의 HTML 문서 가져오기

            // data-top 속성이 '1'인 section 요소 검색
            Element sectionElement = document.select("section.con[data-top=1]").first();
            stockCompanyInfoResponseDto.setStockId(stockId);
            if (sectionElement != null) {
                // 클래스 이름이 'tbl'인 div 요소 검색
                Element tblDiv = sectionElement.select("div.tbl").first();
                if (tblDiv != null) {
                    // tbody 요소 선택
                    Element tbody = tblDiv.select("tbody").first();
                    if (tbody != null) {
                        // 홈페이지가 포함된 tr 요소 검색
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
                                    System.out.println("홈페이지 URL: " + originalUrl);

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
}
