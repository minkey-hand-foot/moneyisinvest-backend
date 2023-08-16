package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.StockCompanyFavResponseDto;

import java.util.List;

public interface FavoriteService {

    BaseResponseDto addFavorite(String uid, String stockId);
    BaseResponseDto removeFavorite(String uid, String stockId);
    List<StockCompanyFavResponseDto> findUserFavoriteStockIds(String uid);

}