package org.knulikelion.moneyisinvest.service.impl;

import org.knulikelion.moneyisinvest.data.dto.request.SupportRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.SupportResponseDto;
import org.knulikelion.moneyisinvest.data.entity.Support;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.enums.SupportStatus;
import org.knulikelion.moneyisinvest.data.repository.SupportRepository;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.SupportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class SupportServiceImpl implements SupportService {

    private final UserRepository userRepository;
    private final SupportRepository supportRepository;

    @Autowired
    public SupportServiceImpl(UserRepository userRepository,
                              SupportRepository supportRepository) {
        this.userRepository = userRepository;
        this.supportRepository = supportRepository;
    }

    @Override
    public SupportResponseDto addSupport(SupportRequestDto supportRequestDto, String uid) {
        Optional<User> user = userRepository.findByUid(uid);
        if (user.isEmpty()) {
            throw new RuntimeException("사용자가 존재하지 않음");
        }

        Support savedSupport = supportRepository.save(
                Support.builder()
                        .user(user.get())
                        .title(supportRequestDto.getTitle())
                        .content(supportRequestDto.getContents())
                        .status(SupportStatus.PENDING)
                        .createdAt(LocalDateTime.now())
                        .updatedAt(LocalDateTime.now())
                        .build()
        );

        return SupportResponseDto.builder()
                .supportId(savedSupport.getId())
                .title(savedSupport.getTitle())
                .contents(savedSupport.getContent())
                .status(
                        (savedSupport.getStatus() == SupportStatus.PENDING) ? "답변 대기 중" : "답변 완료"
                )
                .createdAt(savedSupport.getCreatedAt().toString())
                .updatedAt(savedSupport.getUpdatedAt().toString())
                .build();
    }


    @Override
    public SupportResponseDto getUserSupport(String uid, Long supportId) {
        Optional<User> user = userRepository.findByUid(uid);
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.E");

        if(user.isEmpty()) {
            throw new RuntimeException("사용자를 찾을 수 없음");
        }

        Optional<Support> support = supportRepository.findById(supportId);

        if(support.isEmpty()) {
            throw new RuntimeException("해당 문의 사항을 조회할 수 없음");
        }

        return SupportResponseDto.builder()
                .supportId(support.get().getId())
                .title(support.get().getTitle())
                .contents(support.get().getContent())
                .closed(support.get().getStatus() != SupportStatus.PENDING)
                .closedDate(
                        (support.get().getClosedDate() != null) ? support.get().getClosedDate().format(outputFormatter) : null
                )
                .comment(support.get().getComment())
                .createdAt(support.get().getCreatedAt().format(outputFormatter))
                .updatedAt(support.get().getUpdatedAt().format(outputFormatter))
                .build();
    }

    @Override
    public List<SupportResponseDto> getAll(String uid) {
        List<SupportResponseDto> supportResponseDtoList = new ArrayList<>();
        List<Support> supports = supportRepository.findAllByUser(userRepository.getByUid(uid));
        DateTimeFormatter outputFormatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.E");

        for (Support support : supports) {
            supportResponseDtoList.add(
                    SupportResponseDto.builder()
                            .supportId(support.getId())
                            .title(support.getTitle())
                            .contents(support.getContent())
                            .closed(support.getStatus() != SupportStatus.PENDING)
                            .closedDate(
                                    (support.getClosedDate() != null) ? support.getClosedDate().format(outputFormatter) : null
                            )
                            .status(
                                    (support.getStatus() == SupportStatus.PENDING) ? "답변 대기 중" : "답변 완료"
                            )
                            .comment(support.getComment())
                            .createdAt(support.getCreatedAt().format(outputFormatter))
                            .updatedAt(support.getUpdatedAt().format(outputFormatter))
                    .build()
            );
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
