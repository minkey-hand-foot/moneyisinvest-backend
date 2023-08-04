package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.Support;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SupportRepository extends JpaRepository<Support, Long> {
    List<Support> findAllSupportId(Long id);
}
