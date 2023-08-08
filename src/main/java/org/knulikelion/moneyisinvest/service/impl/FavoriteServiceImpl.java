package org.knulikelion.moneyisinvest.service.impl;

import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.data.dto.request.FavoriteRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.FavoriteResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Favorite;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.repository.FavoriteRepository;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.FavoriteService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final UserRepository userRepository;

    private final FavoriteRepository favoriteRepository;

    @Override
    public BaseResponseDto addFavorite(String userId, String stockId) {

        BaseResponseDto baseResponseDto = new BaseResponseDto();

        User getUser = userRepository.findByUid(userId);

        Favorite favorite = new Favorite();
        favorite.setUser(getUser);
        favorite.setStockId(stockId);

        favoriteRepository.save(favorite);
        baseResponseDto.setSuccess(true);
        baseResponseDto.setMsg("관심 종목 추가");

        return baseResponseDto;
    }
    @Override
    public BaseResponseDto removeFavorite(String userId, String stockId) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        User user = userRepository.findByUid(userId);

        List<Favorite> favorites = favoriteRepository.findByUserAndStockId(user, stockId);

        if(favorites != null && !favorites.isEmpty()) {

            for(Favorite favorite : favorites) {
                favorite.setUser(null);
                favoriteRepository.delete(favorite);
            }
            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("관심 종목이 삭제 완료되었습니다.");
        }else {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("해당 관심 종목을 찾을 수 없습니다.");
        }
        return baseResponseDto;
    }
    @Override
    public List<String> findUserFavoriteStockIds(String userId) {
        List<Favorite> favorites = favoriteRepository.findByUserId(userId);

        return favorites.stream().map(Favorite::getStockId).collect(Collectors.toList());
    }
}
