package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.request.SupportRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.SupportResponseDto;
import org.knulikelion.moneyisinvest.service.SupportService;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/support")
public class SupportController {

    private final SupportService supportService;
    private final JwtTokenProvider jwtTokenProvider;

    // 문의사항 추가하기
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/post")
    public BaseResponseDto addSupport(@RequestBody SupportRequestDto supportRequestDto, HttpServletRequest request) {
        return supportService.addSupport(supportRequestDto,jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")));
    }

    // 문의사항 상세보기
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/get/user-support")
    public SupportResponseDto getOneSupportById(HttpServletRequest request, Long support_id) {
        return supportService.getUserSupport(jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")),support_id);
    }

    // 문의사항 전체 보기
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/getAll")
    public List<SupportResponseDto> getAllSupportById(HttpServletRequest request) {
        return supportService.getAll(jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")));
    }

    // 문의사항 삭제
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/remove")
    public BaseResponseDto removeSupport(HttpServletRequest request, @RequestParam("supportId") Long supportId) {
        return supportService.removeSupport(jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")),supportId);
    }

}
