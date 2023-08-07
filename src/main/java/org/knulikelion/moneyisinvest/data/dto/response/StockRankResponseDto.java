package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class StockRankResponseDto {
    private String stockName;
    /* 주식 이름 hts_kor_isnm */
    private String rank;
    /* 데이터 순위 data_rank */
    private String stockPrice;
    /* 주식 현재가 stck_prpr */
    private String preparation_day_before_rate;
    /* 전일 대비율 prdy_ctrt */
}
