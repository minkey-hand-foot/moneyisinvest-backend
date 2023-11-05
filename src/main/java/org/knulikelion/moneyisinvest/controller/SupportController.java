package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.request.SupportRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.SupportResponseDto;
import org.knulikelion.moneyisinvest.service.SupportService;
import org.springframework.http.ResponseEntity;
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
    @PostMapping("/")
    public SupportResponseDto addSupport(@RequestBody SupportRequestDto supportRequestDto, HttpServletRequest request) {
        return supportService.addSupport(supportRequestDto,
                jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")));
    }

    // 문의사항 상세보기
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/")
    public SupportResponseDto getOneSupportById(HttpServletRequest request, @RequestParam Long supportId) {
        return supportService.getUserSupport(
                jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")), supportId);
    }

    // 문의사항 전체 보기
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/all")
    public List<SupportResponseDto> getAllSupportById(HttpServletRequest request) {
        return supportService.getAll(jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")));
    }

    // 문의사항 삭제
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/")
    public BaseResponseDto removeSupport(HttpServletRequest request, @RequestParam Long supportId) {
        return supportService.removeSupport(
                jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")), supportId);
    }

}
