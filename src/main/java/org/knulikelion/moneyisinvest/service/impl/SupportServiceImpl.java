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
import org.knulikelion.moneyisinvest.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SupportServiceImpl implements SupportService {

    private final UserRepository userRepository;
    private final SupportRepository supportRepository;

    @Autowired
    private UserService userService;

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
            support.setTitle(supportRequestDto.getTitle());
            support.setStatus("답변 대기중");
            support.setContents(supportRequestDto.getContents());
            support.setCreatedAt(LocalDateTime.now());
            support.setUpdatedAt(LocalDateTime.now());

            supportRepository.save(support);

            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("문의 사항 추가");
        }
        return baseResponseDto;
    }

    @Override
    public SupportResponseDto getOne(String uid, Long supportId) {
        if(uid == null) throw new RuntimeException("사용자 id가 없습니다.");
        Optional<Support> support = supportRepository.findByIdAndUserUid(supportId, uid);
        Support foundSupport = support.orElseThrow(() -> new NoSuchElementException("No support found with id:" + uid));
        SupportResponseDto responseDto = new SupportResponseDto();

        responseDto.setId(foundSupport.getId());
        responseDto.setUid(foundSupport.getUser().getUid());
        responseDto.setTitle(foundSupport.getTitle());
        responseDto.setStatus(foundSupport.getStatus());
        responseDto.setContents(foundSupport.getContents());
        responseDto.setCreatedAt(foundSupport.getCreatedAt().toString());
        responseDto.setUpdatedAt(foundSupport.getUpdatedAt().toString());
        return responseDto;
    }

    @Override
    public List<SupportResponseDto> getAll(String uid) {

        if(uid ==null) throw new RuntimeException("사용자 uid가 없습니다.");
        List<SupportResponseDto> supportResponseDtoList = new ArrayList<>();

        List<Support> getSupports = supportRepository.findAllByUserUid(uid);

        for(Support getSupport : getSupports) {
            SupportResponseDto supportResponseDto = new SupportResponseDto();
            supportResponseDto.setId(getSupport.getId());
            supportResponseDto.setUid(getSupport.getUser().getUid());
            supportResponseDto.setTitle(getSupport.getTitle());
            supportResponseDto.setContents(getSupport.getContents());
            supportResponseDto.setStatus(getSupport.getStatus());
            supportResponseDto.setCreatedAt(getSupport.getCreatedAt().toString());
            supportResponseDto.setUpdatedAt(getSupport.getUpdatedAt().toString());

            supportResponseDtoList.add(supportResponseDto);
        }
        return supportResponseDtoList;
    }


    @Override
    public BaseResponseDto removeSupport(String uid, Long supportId) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        Optional<Support> getSupport = supportRepository.findByIdAndUserUid(supportId, uid);

        if(getSupport.isPresent()) {
            supportRepository.delete(getSupport.get());

            baseResponseDto.setSuccess(true);
            baseResponseDto.setMsg("문의사항이 삭제되었습니다.");
        }else {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("문의사항을 찾을 수 없습니다.");
        }
        return baseResponseDto;
    }
}
