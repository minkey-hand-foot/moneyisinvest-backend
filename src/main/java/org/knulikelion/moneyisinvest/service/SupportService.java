package org.knulikelion.moneyisinvest.service;

import com.fasterxml.jackson.databind.ser.Serializers;
import org.knulikelion.moneyisinvest.data.dto.request.SupportRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.SupportResponseDto;

import java.util.List;

public interface SupportService {

    BaseResponseDto addSupport(SupportRequestDto supportRequestDto, String uid);
    SupportResponseDto getUserSupport(String uid,Long supprot_id);
    List<SupportResponseDto> getAll(String uid);
    BaseResponseDto removeSupport(String uid, Long supportId);
}
