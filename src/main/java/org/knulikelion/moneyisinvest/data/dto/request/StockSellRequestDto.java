package org.knulikelion.moneyisinvest.data.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class StockSellRequestDto {
    private String stockCode;
    private String stockAmount;
    private String sell_price;
}
