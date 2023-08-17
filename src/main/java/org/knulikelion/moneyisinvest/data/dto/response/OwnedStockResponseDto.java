package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class OwnedStockResponseDto {
    private String stockUrl;
    private String stockCode;
    private String stockName;
    private Double rate;
    private int stockAmount;
    private String real_sum_coin_price;
    private String real_sum_price;
    private String my_conclusion_sum_coin;
    private String my_conclusion_sum_price;
    private String my_per_conclusion_coin;
    private String my_per_conclusion_price;
    private String real_per_coin;
    private String real_per_price;
    private boolean favorite_status;
}