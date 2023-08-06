package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class FavoriteResponseDto {
    private Long id;
    private String uid;
    private String stockId;
    private String createdAt;
    private String updatedAt;
}
