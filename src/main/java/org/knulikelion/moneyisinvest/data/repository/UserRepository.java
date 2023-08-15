package org.knulikelion.moneyisinvest.data.repository;

import com.fasterxml.jackson.annotation.OptBoolean;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    User getByUid(String uid);
    User findByUid(String uid);

}
