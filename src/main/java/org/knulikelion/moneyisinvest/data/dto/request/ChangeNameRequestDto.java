package org.knulikelion.moneyisinvest.data.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class ChangeNameRequestDto {
//    새로운 이름
    private String newName;
//    기존 비밀번호
    private String currentPasswd;
}
