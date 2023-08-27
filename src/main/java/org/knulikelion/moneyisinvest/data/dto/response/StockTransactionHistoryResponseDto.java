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
    private String unitPrice;
    private Integer quantity;
    private String stockPrice; // 스톡가
    private String stockName;
    private String stockCode;
}