package org.knulikelion.moneyisinvest.service;

import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.data.dto.response.KakaoApproveResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.KakaoReadyResponseDto;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class KakaoPayService {
    static final String cid = "TC0ONETIME"; // 가맹점 테스트 코드
    static final String admin_Key = "fdbcff172ec17d060abaee5bc8b65568"; // 공개 조심! 본인 애플리케이션의 어드민 키를 넣어주세요
    private KakaoReadyResponseDto kakaoReady;

    public KakaoReadyResponseDto kakaoPayReady() {

        // 카카오페이 요청 양식
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("partner_order_id", "0000000001");
        parameters.add("partner_user_id", "0000000001");
        parameters.add("item_name", "투자가머니(프리미엄)");
        parameters.add("quantity", "1");
        parameters.add("total_amount", "1000");
        parameters.add("vat_amount", "0");
        parameters.add("tax_free_amount", "0");
        parameters.add("approval_url", "http://localhost:8080/api/v1/payment/success"); // 성공 시 redirect url
        parameters.add("cancel_url", "http://localhost:8080/api/v1/payment/cancel"); // 취소 시 redirect url
        parameters.add("fail_url", "http://localhost:8080/api/v1/payment/fail"); // 실패 시 redirect url

        // 파라미터, 헤더
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        // 외부에 보낼 url
        RestTemplate restTemplate = new RestTemplate();

        kakaoReady = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/ready",
                requestEntity,
                KakaoReadyResponseDto.class);

        return kakaoReady;
    }

//    결제 완료 승인
    public KakaoApproveResponseDto ApproveResponse(String pgToken) {

        // 카카오 요청
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("tid", kakaoReady.getTid());
        parameters.add("partner_order_id", "0000000001");
        parameters.add("partner_user_id", "0000000001");
        parameters.add("pg_token", pgToken);

        // 파라미터, 헤더
        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        // 외부에 보낼 url
        RestTemplate restTemplate = new RestTemplate();

        KakaoApproveResponseDto approveResponse = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/approve",
                requestEntity,
                KakaoApproveResponseDto.class);

        return approveResponse;
    }

//    카카오 헤더 값
    private HttpHeaders getHeaders() {
        HttpHeaders httpHeaders = new HttpHeaders();

        String auth = "KakaoAK " + admin_Key;

        httpHeaders.set("Authorization", auth);
        System.out.println(auth);
        httpHeaders.set("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        return httpHeaders;
    }
}
