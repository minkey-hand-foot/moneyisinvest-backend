package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignInResultDto extends SignUpResultDto {
    private String token;
    private String username;

    @Builder
    public SignInResultDto(boolean success, int code, String msg, String token, String username) {
        super(success, code, msg);
        this.token = token;
        this.username = username;
    }
}
