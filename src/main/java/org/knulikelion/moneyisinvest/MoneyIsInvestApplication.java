package org.knulikelion.moneyisinvest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class MoneyIsInvestApplication {
    public static void main(String[] args) {
        SpringApplication.run(MoneyIsInvestApplication.class, args);
    }

}
