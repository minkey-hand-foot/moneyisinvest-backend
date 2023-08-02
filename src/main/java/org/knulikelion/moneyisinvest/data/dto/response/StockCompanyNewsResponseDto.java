package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockCompanyNewsResponseDto {
    private String newsTitle;
    private String newsCompany;
    private String newsCreatedAt;
    private String newsUrl;
}
