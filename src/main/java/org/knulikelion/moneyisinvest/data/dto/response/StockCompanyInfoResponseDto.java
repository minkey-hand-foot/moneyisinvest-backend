package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StockCompanyInfoResponseDto {
    private String stockId;
    private String stockName;
    private String companyName;
    private String companyEnName;
    private String goPublicDate;
    private String establishmentDate;
    private String mainItems;
    private String representativeName;
    private String companyUrl;
}
