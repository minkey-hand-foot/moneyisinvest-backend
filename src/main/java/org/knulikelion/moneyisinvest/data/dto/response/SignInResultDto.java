package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignInResultDto extends SignUpResultDto {
    private String token;
    private String refreshToken;
    private String uid;
    private String name;

    @Builder
    public SignInResultDto(boolean success, int code, String msg, String token, String refreshToken,String uid, String name) {
        super(success, code, msg);
        this.token = token;
        this.refreshToken = refreshToken;
        this.uid = uid;
        this.name = name;
    }
}
