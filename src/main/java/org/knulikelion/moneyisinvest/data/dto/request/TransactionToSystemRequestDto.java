package org.knulikelion.moneyisinvest.data.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TransactionToSystemRequestDto {
    private String targetUid;
    private double amount;
}
