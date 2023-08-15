package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;

import java.util.List;

public interface FavoriteService {

    BaseResponseDto addFavorite(String uid, String stockId);
    BaseResponseDto removeFavorite(String uid, String stockId);
    List<String> findUserFavoriteStockIds(String uid);

}