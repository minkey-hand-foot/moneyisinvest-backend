package org.knulikelion.moneyisinvest.service;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.knulikelion.moneyisinvest.data.dto.request.SupportRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.SupportResponseDto;

import java.util.List;

public interface SupportService {

    BaseResponseDto addSupport(SupportRequestDto supportRequestDto);
    SupportResponseDto getOne(Long id);
    List<SupportResponseDto> getAll(Long id);
    BaseResponseDto removeSupport(Long id);
}
