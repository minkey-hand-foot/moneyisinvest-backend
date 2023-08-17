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
    public BaseResponseDto addSupport(SupportRequestDto supportRequestDto, String uid) {
        BaseResponseDto baseResponseDto = new BaseResponseDto();

        User getUser = userRepository.findByUid(uid);

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
    public SupportResponseDto getUserSupport(String uid, Long supprotId) {
        if (uid == null) throw new RuntimeException("사용자 id가 없습니다.");
        User user = userRepository.findByUid(uid);
        List<Support> supportList = supportRepository.findByUser_Id(user.getId());

        SupportResponseDto responseDto = null;
        if (supportList.isEmpty()) {
            return null;
        } else {
            for (Support temp : supportList) {
                if (temp.getId().equals(supprotId)) {
                    responseDto = new SupportResponseDto();

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
