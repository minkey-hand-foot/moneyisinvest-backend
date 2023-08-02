package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.request.FavoriteRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.SupportRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.FavoriteResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.SupportResponseDto;

import java.util.List;

public interface SupportService {

    BaseResponseDto addSupport(SupportRequestDto supportRequestDto);
    List<SupportResponseDto> getAllSupportById(Long id);
    BaseResponseDto removerSupport(Long id);
}
