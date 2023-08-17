package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockCompanyFavResponseDto {
    private String stockCode; // 종목 코드

    private String stockUrl; // 회사 로고 Url

    private String stockName; // 회사 이름

    private double rate; // 전일 대비 등락율

    private String real_per_price; // 현재 평 단가

    private String real_per_coin; // 현재 평 스톡
}
