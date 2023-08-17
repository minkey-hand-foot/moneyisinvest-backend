package org.knulikelion.moneyisinvest.service;

import lombok.RequiredArgsConstructor;
import org.knulikelion.moneyisinvest.data.dto.response.KakaoApproveResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.KakaoReadyResponseDto;
import org.knulikelion.moneyisinvest.data.entity.PlanPayment;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.repository.PlanPaymentRepository;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
@Transactional
public class KakaoPayService {
    static final String cid = "TC0ONETIME"; // 가맹점 테스트 코드

    @Value("${KAKAOPAY.ADMIN.KEY}")
    private String admin_Key;

    private final PlanPaymentRepository planPaymentRepository;
    private final UserRepository userRepository;
    private KakaoReadyResponseDto kakaoReady;

    public KakaoReadyResponseDto kakaoPayReady(String uid) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("partner_order_id", "0000000001");
        parameters.add("partner_user_id", "0000000001");
        parameters.add("item_name", "투자가머니(프리미엄)");
        parameters.add("quantity", "1");
        parameters.add("total_amount", "3300");
        parameters.add("vat_amount", "0");
        parameters.add("tax_free_amount", "0");
        parameters.add("approval_url", "https://moneyisinvest.kr/messagePage"); // 성공 시 redirect url
        parameters.add("cancel_url", "https://moneyisinvest.kr/messagePage"); // 취소 시 redirect url
        parameters.add("fail_url", "https://moneyisinvest.kr/messagePage"); // 실패 시 redirect url

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate restTemplate = new RestTemplate();

        kakaoReady = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/ready",
                requestEntity,
                KakaoReadyResponseDto.class);

        kakaoReady.setUid(uid);

        return kakaoReady;
    }

//    결제 완료 승인
    public KakaoApproveResponseDto ApproveResponse(String pgToken) {
        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<>();
        parameters.add("cid", cid);
        parameters.add("tid", kakaoReady.getTid());
        parameters.add("partner_order_id", "0000000001");
        parameters.add("partner_user_id", "0000000001");
        parameters.add("pg_token", pgToken);

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(parameters, this.getHeaders());

        RestTemplate restTemplate = new RestTemplate();

        KakaoApproveResponseDto approveResponse = restTemplate.postForObject(
                "https://kapi.kakao.com/v1/payment/approve",
                requestEntity,
                KakaoApproveResponseDto.class);

        PlanPayment planPayment = new PlanPayment();
        planPayment.setEnable(true);
        planPayment.setUser(userRepository.getByUid(kakaoReady.getUid()));
        planPayment.setItemName(approveResponse.getItem_name());
        planPayment.setTotal(approveResponse.getAmount().getTotal());
        planPayment.setApprovedAt(approveResponse.getApproved_at());
        planPayment.setPaymentMethod("kakaopay");

        String inputDate = approveResponse.getApproved_at();
        LocalDateTime dateTime = LocalDateTime.parse(inputDate);
        LocalDateTime dateAfter30Days = dateTime.plusDays(30);

        planPayment.setExpirationAt(dateAfter30Days.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        planPaymentRepository.save(planPayment);

        User user = userRepository.getByUid(kakaoReady.getUid());
        user.setPlan("premium");
        userRepository.save(user);

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
