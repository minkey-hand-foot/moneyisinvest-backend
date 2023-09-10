package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.request.*;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.SignInResultDto;
import org.knulikelion.moneyisinvest.data.dto.response.SignUpResultDto;

import java.io.IOException;

public interface SignService {
    SignUpResultDto signUp(SignUpRequestDto signUpRequestDto);
    SignInResultDto signIn(SignInRequestDto signInRequestDto) throws RuntimeException;
    SignInResultDto kakaoLogin(String code) throws RuntimeException, IOException, InterruptedException;
    BaseResponseDto changePasswd(ChangePasswdRequestDto changePasswdRequestDto, String uid);
    BaseResponseDto changeName(ChangeNameRequestDto changeNameRequestDto, String uid);
    BaseResponseDto unRegister(UnRegisterRequestDto unRegisterRequestDto, String uid);
}