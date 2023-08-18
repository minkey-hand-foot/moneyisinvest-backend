package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.knulikelion.moneyisinvest.data.entity.StockTransaction;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@ToString
public class StockTransactionResponseDto {

    private String transactionType; // 거래 종류(구매/판매)
    private String stockCode; // 주식 코드
    private String stockName; // 주식 이름
    private int quantity; // 거래 수량
    private Integer unitPrice; // 단가
    private Integer stockPrice; // 스톡가
    private LocalDateTime transactionDate; // 거래 일시

    public StockTransactionResponseDto(StockTransaction transaction) {
        this.transactionType = transaction.isPurchase() ? "구매" : "판매";
        this.stockCode = transaction.getStockCode();
        this.stockName = transaction.getStockName();
        this.quantity = transaction.getQuantity();
        this.unitPrice = transaction.getUnitPrice();
        this.transactionDate = transaction.getTransactionDate();
    }

}