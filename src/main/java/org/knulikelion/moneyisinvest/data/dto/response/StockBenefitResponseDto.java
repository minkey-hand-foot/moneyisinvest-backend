package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class StockBenefitResponseDto {
    private String benefitPrice;
    private String benefitAmount;
    private String losePrice;
    private String lostAmount;
}
