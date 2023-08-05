package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.request.SignInRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.MypageResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.SignInResultDto;
import org.knulikelion.moneyisinvest.data.dto.request.SignUpRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.SignUpResultDto;

public interface SignService {
    SignUpResultDto signUp(SignUpRequestDto signUpRequestDto);
    MypageResponseDto getUserDetail(String token);
    SignInResultDto signIn(SignInRequestDto signInRequestDto) throws RuntimeException;
}
