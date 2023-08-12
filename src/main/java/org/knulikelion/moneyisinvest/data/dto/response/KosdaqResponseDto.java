package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class KosdaqResponseDto {
    private String date;
    private String price;
    private String rate;
}
