package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.Support;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SupportRepository extends JpaRepository<Support, Long> {
    void delete (Support getSupport);
    List<Support> findAllByUserUid(String uid);

    Optional<Support> findByIdAndUserUid(Long supportId, String uid);

    /*@Transactional
    void deleteByIdAndUserId(Long supportId, Long userId);*/
}
