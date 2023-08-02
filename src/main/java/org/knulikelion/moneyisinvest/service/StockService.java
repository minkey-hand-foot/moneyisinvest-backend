package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.response.StockCompanyInfoResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.StockCompanyNewsResponseDto;

import java.util.List;

public interface StockService {
    StockCompanyInfoResponseDto getCompanyInfoByStockId(String stockId);
    List<StockCompanyNewsResponseDto> getCompanyNewsByStockId(String stockId);
    String getStockNameByStockId(String stockId);
}
