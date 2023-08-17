package org.knulikelion.moneyisinvest.service.impl;

import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.StockCompanyFavResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Favorite;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.repository.FavoriteRepository;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.FavoriteService;
import org.knulikelion.moneyisinvest.service.StockService;
import org.springframework.stereotype.Service;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final UserRepository userRepository;

    private final FavoriteRepository favoriteRepository;
    private final StockService stockService;

    @Override
    public BaseResponseDto addFavorite(String uid, String stockId) {

        BaseResponseDto baseResponseDto = new BaseResponseDto();

        User getUser = userRepository.findByUid(uid);

        Favorite existFavorite = favoriteRepository.findByUserAndStockId(getUser,stockId);

        if(existFavorite != null) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("이미 관심 등록한 주식입니다.");
            return baseResponseDto;
        }

        Favorite favorite = new Favorite();
        favorite.setUser(getUser);
        favorite.setStockId(stockId);

        favoriteRepository.save(favorite);
        baseResponseDto.setSuccess(true);
        baseResponseDto.setMsg("관심 종목 추가");

        return baseResponseDto;
    }
    @Override
    public BaseResponseDto removeFavorite(String uid, String stockId) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        User user = userRepository.findByUid(uid);

        Favorite existFavorite = favoriteRepository.findByUserAndStockId(user, stockId);

        if (existFavorite != null) {
            existFavorite.setUser(null);
            userRepository.save(user);

            favoriteRepository.delete(existFavorite);
            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("관심 종목이 삭제 완료되었습니다.");
        } else {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("해당 관심 종목을 찾을 수 없습니다.");
        }

        return baseResponseDto;
    }
    @Override
    public List<StockCompanyFavResponseDto> findUserFavoriteStockIds(String uid) {
        User user = userRepository.findByUid(uid);
        List<Favorite> favorites = favoriteRepository.findByUser(user);
        List<StockCompanyFavResponseDto> outputList = new ArrayList<>();
        if(!favorites.isEmpty()) {
            for (Favorite temp : favorites) {
                StockCompanyFavResponseDto stockCompanyFavResponseDto = new StockCompanyFavResponseDto();
                stockCompanyFavResponseDto.setStockCode(temp.getStockId());
                stockCompanyFavResponseDto.setStockUrl(stockService.getCompanyInfoByStockId(temp.getStockId()).getStockLogoUrl());
                stockCompanyFavResponseDto.setStockName(stockService.getStockNameByStockId(temp.getStockId()));
                stockCompanyFavResponseDto.setRate(Double.parseDouble(stockService.getDayBeforeRate(temp.getStockId())));

                NumberFormat nf = NumberFormat.getInstance(Locale.getDefault());
                stockCompanyFavResponseDto.setReal_per_price(nf.format(Integer.parseInt(stockService.getCurrentPrice(temp.getStockId()))));
                stockCompanyFavResponseDto.setReal_per_coin(nf.format(Integer.parseInt(stockService.getCurrentPrice(temp.getStockId())) / 100));
                outputList.add(stockCompanyFavResponseDto);
            }
        }

        return outputList;
    }
}
