package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CompanyResultTableResponseDto {
//    최근 연간 실적 일자
    private String date;
//    매출액
    private String take;
//    영업이익
    private String operatingProfit;
//    당기순이익
    private String netIncome;
//    부채비율
    private String debtRatio;
//    당좌비율
    private String quickRatio;
//    유보율
    private String retentionRate;
//    PER
    private String per;
//    PBR
    private String pbr;
}
