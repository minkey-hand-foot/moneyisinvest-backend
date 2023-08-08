package org.knulikelion.moneyisinvest.data.entity;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender")
    private String from;

    @Column(name = "recipient")
    private String to;

    @Column
    private double amount;
}
