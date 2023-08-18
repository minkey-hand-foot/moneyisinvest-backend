package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class StockTransactionHistoryResponseDto {
    private String transactionDate;
    private String stockLogo;
    private boolean status;
    private Integer unitPrice;
    private Integer quantity;
    private String stockName;
    private String stockCode;
}