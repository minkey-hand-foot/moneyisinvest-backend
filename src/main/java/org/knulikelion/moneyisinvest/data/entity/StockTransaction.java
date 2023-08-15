package org.knulikelion.moneyisinvest.data.entity;


import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table(name = "stock_transaction")
public class StockTransaction {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    @Column
    private String stockId;
    @Column
    private String stockCode;
    @Column
    private String stockName;
    @Column
    private int quantity;
    @Column
    private double unitPrice;
    @Column
    private boolean isPurchase; // 구매 여부 판단.
    @Column
    private LocalDateTime transactionDate;

}
