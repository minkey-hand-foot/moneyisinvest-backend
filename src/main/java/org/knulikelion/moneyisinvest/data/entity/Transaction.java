package org.knulikelion.moneyisinvest.data.entity;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import javax.persistence.*;
import java.util.List;

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
    private double fee;

    @Column
    private double amount;

    @JsonCreator
    public Transaction(@JsonProperty("sender") String from,
                       @JsonProperty("recipient") String to,
                       @JsonProperty("fee") double fee,
                       @JsonProperty("amount") double amount) {
        this.from = from;
        this.to = to;
        this.fee = fee;
        this.amount = amount;
    }
}
