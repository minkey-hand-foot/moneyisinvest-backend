package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class KakaoReadyResponseDto {
//    결제 고유 번호
    private String tid;
//    모바일 웹 결제 페이지 URL
    private String next_redirect_mobile_url;
//    PC 결제 페이지 URL
    private String next_redirect_pc_url;
    private String created_at;
}
