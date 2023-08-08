package org.knulikelion.moneyisinvest.data.entity;

import lombok.*;

import javax.persistence.*;
import java.util.List;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@Table(name = "blocks")
public class Block {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "hash_code")
    private String hash;

    @Column(name = "previous_hash")
    private String previousHash;

    @Column
    private long timeStamp;

    @Column
    private long timeTolerance;

    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<Transaction> transactions;
}


