package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ReissueTokenResponseDto {
    private String accessToken;
    private String refreshToken;
}
