package org.knulikelion.moneyisinvest.data.dto.response;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class SignInResultDto extends SignUpResultDto {
    private String token;
    private String uid;
    private String name;

    @Builder
    public SignInResultDto(boolean success, int code, String msg, String token, String uid, String name) {
        super(success, code, msg);
        this.token = token;
        this.uid = uid;
        this.name = name;
    }
}
