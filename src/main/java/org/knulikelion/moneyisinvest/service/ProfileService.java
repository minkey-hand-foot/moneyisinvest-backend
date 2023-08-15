package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.MypageResponseDto;
import org.springframework.core.io.Resource;
import org.springframework.web.multipart.MultipartFile;

public interface ProfileService {
    MypageResponseDto getUserDetail(String token);

    BaseResponseDto storeFile(MultipartFile file, String uid);
    Resource loadFileAsResource(String fileName);
}
