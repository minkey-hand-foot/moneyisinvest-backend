package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.SignInResultDto;
import org.knulikelion.moneyisinvest.data.dto.SignUpResultDto;

public interface SignService {
    SignUpResultDto signUp(String id, String password, String name, String role);
    SignInResultDto signIn(String id, String password) throws RuntimeException;
}
