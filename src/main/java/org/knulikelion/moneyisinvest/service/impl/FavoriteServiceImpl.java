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
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class FavoriteServiceImpl implements FavoriteService {

    private final UserRepository userRepository;

    private final FavoriteRepository favoriteRepository;
    private final StockService stockService;

    @Override
    public BaseResponseDto addFavorite(String uid, String stockId) {

        BaseResponseDto baseResponseDto = new BaseResponseDto();

        Optional<User> getUser = userRepository.findByUid(uid);

        if(!getUser.isPresent()) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("사용자를 찾을 수 없습니다.");

            return baseResponseDto;
        }

        Favorite existFavorite = favoriteRepository.findByUserAndStockId(getUser.get(),stockId);

        if(existFavorite != null) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("이미 관심 등록한 주식입니다.");
            return baseResponseDto;
        }

        Favorite favorite = new Favorite();
        favorite.setUser(getUser.get());
        favorite.setStockId(stockId);

        favoriteRepository.save(favorite);
        baseResponseDto.setSuccess(true);
        baseResponseDto.setMsg("관심 종목 추가");

        return baseResponseDto;
    }
    @Override
    public BaseResponseDto removeFavorite(String uid, String stockId) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        Optional<User> user = userRepository.findByUid(uid);

        if(!user.isPresent()) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("사용자를 찾을 수 없습니다.");

            return baseResponseDto;
        }

        Favorite existFavorite = favoriteRepository.findByUserAndStockId(user.get(), stockId);

        if (existFavorite != null) {
            existFavorite.setUser(null);
            userRepository.save(user.get());

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
        Optional<User> user = userRepository.findByUid(uid);

        if(!user.isPresent()) {
            throw new RuntimeException();
        }

        List<Favorite> favorites = favoriteRepository.findByUser(user.get());
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

    @Override
    public boolean getFavoriteStatus(String uid, String stockCode) {
        Optional<User> user = userRepository.findByUid(uid);

        if(!user.isPresent()) {
            throw new RuntimeException();
        }

        Favorite favorite = favoriteRepository.findByUserAndStockId(user.get(), stockCode);

        if(favorite == null){
            return false;
        }else {
            return true;
        }
    }
}
