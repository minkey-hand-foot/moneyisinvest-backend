package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.Support;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface SupportRepository extends JpaRepository<Support, Long> {
    void delete (Support getSupport);
    List<Support> findAllByUserUid(String uid);
    List<Support> findAllByUser(User user);
    Optional<Support> findByIdAndUserUid(Long supportId, String uid);
    List<Support> findByUser_Id(Long userId);
}
