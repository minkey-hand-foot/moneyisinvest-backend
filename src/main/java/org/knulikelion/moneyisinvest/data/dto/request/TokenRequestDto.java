package org.knulikelion.moneyisinvest.data.dto.request;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Builder
public class TokenRequestDto {
    private String refreshToken;
}
