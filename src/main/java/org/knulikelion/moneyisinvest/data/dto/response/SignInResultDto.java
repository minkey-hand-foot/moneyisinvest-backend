package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignInResultDto extends SignUpResultDto {
    private String accessToken;
    private String refreshToken;
    private String uid;
    private String name;

    @Builder
    public SignInResultDto(boolean success, int code, String msg, String accessToken, String refreshToken,String uid, String name) {
        super(success, code, msg);
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.uid = uid;
        this.name = name;
    }
}
