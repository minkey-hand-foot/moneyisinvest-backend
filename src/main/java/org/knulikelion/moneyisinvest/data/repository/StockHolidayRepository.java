package org.knulikelion.moneyisinvest.data.repository;

import org.knulikelion.moneyisinvest.data.entity.StockHoliday;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockHolidayRepository extends JpaRepository<StockHoliday, Long> {
    List<StockHoliday> getAllByYear(Integer year);
}