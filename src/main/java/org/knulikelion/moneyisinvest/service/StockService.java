package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.response.StockCompanyInfoResponseDto;

public interface StockService {
    StockCompanyInfoResponseDto getCompanyInfoByStockId(String stockId);
}
