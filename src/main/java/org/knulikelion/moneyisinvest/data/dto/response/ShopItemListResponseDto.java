package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ShopItemListResponseDto {
    private Long id;
    private String imageUrl;
    private String category;
    private String itemName;
    private String price;
}
