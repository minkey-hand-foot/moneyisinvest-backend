package org.knulikelion.moneyisinvest.data.entity;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Table(name = "stock_coin_wallet")
public class StockCoinWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String address;

    @Column
    private double balance;

    @Column
    private LocalDateTime createdAt;
}
