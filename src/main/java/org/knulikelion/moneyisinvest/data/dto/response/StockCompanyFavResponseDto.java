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
    private String stockCode;
    private String stockLogoUrl;
    private String companyName;
    private double preparation_day_before_rate;
    private int price;
    private int stockPrice;
}
