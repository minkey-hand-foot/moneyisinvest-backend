package org.knulikelion.moneyisinvest.data.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ChangePasswdRequestDto {
//    기존 비밀번호
    private String currentPasswd;
//    새로운 비밀번호
    private String newPasswd;
//    새로운 비밀번호 재입력
    private String newPasswdRe;
}
