package org.knulikelion.moneyisinvest.data.dto.request;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class SignUpRequestDto {
    private String uid;
    private String password;
    private String name;
    private String phoneNum;
}
