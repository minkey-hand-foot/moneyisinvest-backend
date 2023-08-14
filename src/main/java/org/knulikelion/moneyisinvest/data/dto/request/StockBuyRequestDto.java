package org.knulikelion.moneyisinvest.data.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class StockBuyRequestDto {
    private String uid;
    private String stockCode;
    private String stockAmount;
    private String conclusion_price;
}
