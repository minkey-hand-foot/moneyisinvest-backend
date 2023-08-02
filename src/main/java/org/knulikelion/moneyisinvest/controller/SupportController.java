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


    @PostMapping("/post")
    public BaseResponseDto addSupport(@RequestBody SupportRequestDto supportRequestDto) {
        return supportService.addSupport(supportRequestDto);
    }

    @GetMapping("/get")
    public List<SupportResponseDto> getAllSupportById(Long id) {
        return supportService.getAllSupportById(id);
    }

    @DeleteMapping("/remove")
    public BaseResponseDto removeSupport(Long id) {
        return supportService.removerSupport(id);
    }

}
