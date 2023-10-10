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
    private String stockUrl; // 로고

    @Column
    private String stockCode; // 종목코드

    @Column
    private String stockName; // 종목 이름

    @Column
    private Double rate; // 수익률

    @Column
    private Integer stockAmount; // 총 보유 수량

    @Column
    private Integer real_sum_coin_price; // 실제 종목의 평가 총 코인 가격

    @Column
    private Integer real_sum_price; // 실제 종목의 평가 총 가격

    @Column
    private Integer my_conclusion_sum_coin; // 내 체결 총 코인 가격

    @Column
    private Integer my_conclusion_sum_price; // 내 체결 총 가격

    @Column
    private Integer my_per_conclusion_coin; // 보유 평 코인

    @Column
    private Integer my_per_conclusion_price; // 평 단가

    @Column
    private Integer real_per_coin; // 실제 평 코인

    @Column
    private Integer real_per_price; // 실제 평 단가

    @Column
    private boolean favorite_status; // 찜 여부

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}
