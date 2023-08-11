package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class TransactionHistoryResponseDto {
    private String type;
    private String datetime;
    private String sender;
    private String recipient;
    private String hashCode;
    private double total;
    private double fee;
    private double amount;
}
