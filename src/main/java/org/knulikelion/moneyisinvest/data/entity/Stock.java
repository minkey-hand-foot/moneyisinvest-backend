package org.knulikelion.moneyisinvest.data.entity;

import lombok.*;

import javax.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@Table
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    private String stockCode;

    @Column
    private String stockUrl;

    @Column
    private boolean favorite_status;

    @Column
    private String stockAmount;

    @Column
    private Integer conclusion_price;

    @Column
    private Integer conclusion_coin;

    @Column
    private Double rate;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
