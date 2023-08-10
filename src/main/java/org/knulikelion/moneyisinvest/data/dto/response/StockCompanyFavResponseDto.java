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

    private String stockId;
    private String stockLogoUrl;
    private String companyName;
    private String preparation_day_before_rate;
    private String price;
    private String stockPrice;
    private boolean favorite;

    public StockCompanyFavResponseDto(String stockId, String logoUrl, String companyName, double price, double stockPrice) {
        this.stockId = stockId;
        this.stockLogoUrl = logoUrl;
        this.companyName = companyName;
        this.price = String.valueOf(price);
        this.stockPrice = String.valueOf(stockPrice);
    }
}
