package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.response.KakaoApproveResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.KakaoReadyResponseDto;
import org.knulikelion.moneyisinvest.service.KakaoPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/v1/payment/kakao")
public class KakaoPayController {
    private final KakaoPayService kakaoPayService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public KakaoPayController(KakaoPayService kakaoPayService, JwtTokenProvider jwtTokenProvider) {
        this.kakaoPayService = kakaoPayService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

//    결제 요청
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/pay")
    public KakaoReadyResponseDto paySubscriptionFeewithKakaopay(HttpServletRequest request) {
        return kakaoPayService.kakaoPayReady(jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")));
    }

//    결제 성공
    @GetMapping("/success")
    public ResponseEntity afterPayRequest(@RequestParam("pg_token") String pgToken) {

        KakaoApproveResponseDto kakaoApprove = kakaoPayService.ApproveResponse(pgToken);

        return new ResponseEntity<>(kakaoApprove, HttpStatus.OK);
    }

//    결제 진행 중 취소
    @GetMapping("/cancel")
    public void cancel() throws Exception {
        throw new Exception();
//        throw new BusinessLogicException(ExceptionCode.PAY_CANCEL);
    }

//    결제 실패
    @GetMapping("/fail")
    public void fail() throws Exception {
        throw new Exception();
//        throw new BusinessLogicException(ExceptionCode.PAY_FAILED);
    }
}
