package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class CheckHolidayResponseDto {
    private boolean isOpened;
    private String reason;
}
