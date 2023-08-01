package org.knulikelion.moneyisinvest.controller;

import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.data.dto.response.KakaoApproveResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.KakaoReadyResponseDto;
import org.knulikelion.moneyisinvest.service.KakaoPayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/payment")
public class KakaoPayController {
    private final KakaoPayService kakaoPayService;

    @Autowired
    public KakaoPayController(KakaoPayService kakaoPayService) {
        this.kakaoPayService = kakaoPayService;
    }

//    결제 요청
    @PostMapping("/ready")
    public KakaoReadyResponseDto readyToKakaoPay() {
        return kakaoPayService.kakaoPayReady();
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
