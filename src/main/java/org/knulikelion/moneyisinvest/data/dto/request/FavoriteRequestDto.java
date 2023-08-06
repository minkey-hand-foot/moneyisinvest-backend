package org.knulikelion.moneyisinvest.data.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class FavoriteRequestDto {

    private Long id;
    private String stockId;
    private String uid;
}
