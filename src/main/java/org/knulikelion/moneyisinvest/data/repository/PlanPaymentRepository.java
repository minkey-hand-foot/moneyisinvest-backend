package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.PlanPayment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PlanPaymentRepository extends JpaRepository<PlanPayment, Long> {
}
