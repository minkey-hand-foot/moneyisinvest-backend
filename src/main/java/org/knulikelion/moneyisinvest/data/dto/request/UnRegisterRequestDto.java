package org.knulikelion.moneyisinvest.data.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class UnRegisterRequestDto {
//    기존 비밀번호
    private String currentPasswd;
}
