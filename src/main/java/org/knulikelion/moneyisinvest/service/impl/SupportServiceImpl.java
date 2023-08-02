package org.knulikelion.moneyisinvest.service.impl;

import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.data.dto.request.SupportRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.SupportResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Support;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.repository.SupportRepository;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.SupportService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {

    private final UserRepository userRepository;
    private final SupportRepository supportRepository;

    @Override
    public BaseResponseDto addSupport(SupportRequestDto supportRequestDto) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        User getUser = userRepository.findByUid(supportRequestDto.getUid());

        if(getUser == null) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("사용자가 존재하지 않음");
        }else{
            Support support = new Support();
            support.setUser(getUser);
            support.setId(support.getId());
            support.setTitle(support.getTitle());
            support.setContents(support.getContents());
            support.setCreatedAt(LocalDateTime.now());
            support.setUpdatedAt(LocalDateTime.now());

            supportRepository.save(support);

            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("문의 사항 추가");
        }
        return baseResponseDto;
    }

    @Override
    public List<SupportResponseDto> getAllSupportById(Long id) {
        List<SupportResponseDto> supportResponseDtoList = new ArrayList<>();

        List<Support> getSupports = supportRepository.findAllSupportId(id);

        for(Support getSupport : getSupports) {
            SupportResponseDto supportResponseDto = new SupportResponseDto();
            supportResponseDto.setId(getSupport.getId());
            supportResponseDto.setUid(getSupport.getUser().getUid());
            supportResponseDto.setTitle(getSupport.getTitle());
            supportResponseDto.setContents(getSupport.getContents());
            supportResponseDto.setCreatedAt(getSupport.getCreatedAt().toString());
            supportResponseDto.setUpdatedAt(getSupport.getUpdatedAt().toString());

            supportResponseDtoList.add(supportResponseDto);
        }
        return supportResponseDtoList;
    }

    @Override
    public BaseResponseDto removerSupport(Long id) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        Support getSupport =supportRepository.getById(id);

        if(getSupport != null) {
            getSupport.setUser(null);
            supportRepository.delete(getSupport);

            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("문의사항이 삭제되었습니다.");
        }else {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("문의사항을 찾을 수 없습니다.");
        }
        return baseResponseDto;
    }
}
