package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.request.FavoriteRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.FavoriteResponseDto;

import java.util.List;

public interface FavoriteService {

    BaseResponseDto addFavorite(String userId, String stockId);
    BaseResponseDto removeFavorite(String userId, String stockId);
    List<String> findUserFavoriteStockIds(String userId);

}