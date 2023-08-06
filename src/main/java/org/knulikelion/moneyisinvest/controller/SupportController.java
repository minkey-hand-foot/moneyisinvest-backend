package org.knulikelion.moneyisinvest.controller;

import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.data.dto.request.SupportRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.SupportResponseDto;
import org.knulikelion.moneyisinvest.service.SupportService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/support")
public class SupportController {

    private final SupportService supportService;

    // 문의사항 추가하기
    @PostMapping("/post")
    public BaseResponseDto addSupport(@RequestBody SupportRequestDto supportRequestDto) {
        return supportService.addSupport(supportRequestDto);
    }

    // 문의사항 상세보기
    @GetMapping("/getOne")
    public SupportResponseDto getOneSupportById(Long id) {
        return supportService.getOne(id);
    }

    // 문의사항 전체 보기
    @GetMapping("/getAll")
    public List<SupportResponseDto> getAllSupportById(Long id) {
        return supportService.getAll(id);
    }

    // 문의사항 삭제
    @DeleteMapping("/remove")
    public BaseResponseDto removeSupport(Long id) {
        return supportService.removeSupport(id);
    }

}
