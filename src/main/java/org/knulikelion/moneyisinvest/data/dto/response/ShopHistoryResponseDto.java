package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ShopHistoryResponseDto {
    private String imageUrl;
    private String itemName;
    private String price;
    private LocalDateTime createdAt;
    private boolean isUsed;
}