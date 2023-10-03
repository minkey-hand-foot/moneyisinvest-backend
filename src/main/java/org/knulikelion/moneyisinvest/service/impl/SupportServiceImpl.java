package org.knulikelion.moneyisinvest.service.impl;

import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserService userService;

    @Override
    public SupportResponseDto addSupport(SupportRequestDto supportRequestDto, String uid) {
        SupportResponseDto supportResponseDto = new SupportResponseDto();

        Optional<User> getUser = userRepository.findByUid(uid);

        if (!getUser.isPresent()) {
            supportResponseDto.setMsg("사용자가 존재하지 않음");
            return supportResponseDto;
        }

        Support support = new Support();
        support.setUser(getUser.get());
        support.setTitle(supportRequestDto.getTitle());
        support.setStatus("답변 대기중");
        support.setContents(supportRequestDto.getContents());
        support.setCreatedAt(LocalDateTime.now());
        support.setUpdatedAt(LocalDateTime.now());

        Support savedSupport = supportRepository.save(support); // 저장된 Support 객체를 반환받습니다.

        // 필요한 객체의 필드들을 SupportResponseDto 객체로 복사합니다.
        supportResponseDto.setSupportId(savedSupport.getId());
        supportResponseDto.setUid(getUser.get().getUid());
        supportResponseDto.setTitle(savedSupport.getTitle());
        supportResponseDto.setContents(savedSupport.getContents());
        supportResponseDto.setStatus(savedSupport.getStatus());
        supportResponseDto.setCreatedAt(savedSupport.getCreatedAt().toString()); // LocalDateTime 객체를 문자열로 변환
        supportResponseDto.setUpdatedAt(savedSupport.getUpdatedAt().toString()); // LocalDateTime 객체를 문자열로 변환
        supportResponseDto.setMsg("문의 사항 추가");

        return supportResponseDto;
    }


    @Override
    public SupportResponseDto getUserSupport(String uid, Long supprotId) {
        Optional<User> user = userRepository.findByUid(uid);

        if(!user.isPresent()) {
            throw new RuntimeException("사용자를 찾을 수 없습니다.");
        }

        List<Support> supportList = supportRepository.findByUser_Id(user.get().getId());

        SupportResponseDto responseDto = null;
        if (supportList.isEmpty()) {
            return null;
        } else {
            for (Support temp : supportList) {
                if (temp.getId().equals(supprotId)) {
                    responseDto = new SupportResponseDto();

                    responseDto.setSupportId(temp.getId());
                    responseDto.setUid(temp.getUser().getUid());
                    responseDto.setTitle(temp.getTitle());
                    responseDto.setStatus(temp.getStatus());
                    responseDto.setContents(temp.getContents());
                    responseDto.setCreatedAt(temp.getCreatedAt().toString());
                    responseDto.setUpdatedAt(temp.getUpdatedAt().toString());

                    break;
                }
            }
        }
        return responseDto;
    }

    @Override
    public List<SupportResponseDto> getAll(String uid) {

        if(uid ==null) throw new RuntimeException("사용자 uid가 없습니다.");
        List<SupportResponseDto> supportResponseDtoList = new ArrayList<>();

        List<Support> getSupports = supportRepository.findAllByUserUid(uid);

        for(Support getSupport : getSupports) {
            SupportResponseDto supportResponseDto = new SupportResponseDto();
            supportResponseDto.setSupportId(getSupport.getId());
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
