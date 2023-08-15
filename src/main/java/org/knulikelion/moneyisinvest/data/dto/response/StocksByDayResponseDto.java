package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class StocksByDayResponseDto {
    private String current_date;
    /*날짜 stck_bsop_date*/

    private String end_Price;
    /*종가 stck_clpr*/

    private String start_Price;
    /*시가 stck_oprc*/

    private String high_Price;
    /*최고가 stck_hgpr*/

    private String low_Price;
    /*최저가 stck_lwpr*/
}
