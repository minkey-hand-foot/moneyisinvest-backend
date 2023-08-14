package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
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
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/post")
    public BaseResponseDto addSupport(@RequestBody SupportRequestDto supportRequestDto) {
        return supportService.addSupport(supportRequestDto);
    }

    // 문의사항 상세보기
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/getOne")
    public SupportResponseDto getOneSupportById(@RequestParam("uid") String uid, @RequestParam("supportId") Long supportId) {
        return supportService.getOne(uid, supportId);
    }

    // 문의사항 전체 보기
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/getAll")
    public List<SupportResponseDto> getAllSupportById(@RequestParam("uid") String uid) {
        return supportService.getAll(uid);
    }

    // 문의사항 삭제
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/remove")
    public BaseResponseDto removeSupport(@RequestParam("uid") String uid, @RequestParam("supportId") Long supportId) {
        return supportService.removeSupport(uid,supportId);
    }

}
