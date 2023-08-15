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
    private double preparation_day_before_rate;
    private double price;
    private double stockPrice;
    private boolean favorite;


    public StockCompanyFavResponseDto(String stockLogoUrl, String companyName, double price, double stockPrice, double preparation_day_before_rate,boolean favorite) {
        this.stockLogoUrl = stockLogoUrl;
        this.companyName = companyName;
        this.price = price;
        this.stockPrice = stockPrice;
        this.preparation_day_before_rate = preparation_day_before_rate;
        this.favorite = favorite;
    }
}
