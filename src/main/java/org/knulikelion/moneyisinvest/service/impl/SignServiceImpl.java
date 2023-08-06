package org.knulikelion.moneyisinvest.service.impl;

import org.knulikelion.moneyisinvest.common.CommonResponse;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.request.SignInRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.MypageResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.SignInResultDto;
import org.knulikelion.moneyisinvest.data.dto.request.SignUpRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.SignUpResultDto;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.ProfileService;
import org.knulikelion.moneyisinvest.service.SignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class SignServiceImpl implements SignService {
    private final Logger LOGGER = LoggerFactory.getLogger(SignServiceImpl.class);

    public UserRepository userRepository;
    public ProfileService profileService;
    public JwtTokenProvider jwtTokenProvider;
    public PasswordEncoder passwordEncoder;

    @Autowired
    public SignServiceImpl(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
                           PasswordEncoder passwordEncoder, ProfileService profileService) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
    }

    @Override
    public SignUpResultDto signUp(SignUpRequestDto signUpRequestDto) {
        LOGGER.info("[getSignUpResult] 회원 가입 정보 전달");
        User user;

        if (signUpRequestDto.getRole().equalsIgnoreCase("admin")) {
            user = User.builder()
                    .uid(signUpRequestDto.getUid())
                    .name(signUpRequestDto.getName())
                    .plan("basic")
                    .profileUrl("default-profile.png")
                    .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                    .roles(Collections.singletonList("ROLE_ADMIN"))
                    .build();
        } else {
            user = User.builder()
                    .uid(signUpRequestDto.getUid())
                    .name(signUpRequestDto.getName())
                    .plan("basic")
                    .profileUrl("default-profile.png")
                    .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();
        }

        SignUpResultDto signUpResultDto = new SignInResultDto();

        if(userRepository.findByUid(user.getUid()) != null) {
            signUpResultDto.setSuccess(false);
            signUpResultDto.setMsg("이미 가입된 회원");
            signUpResultDto.setCode(1);
        } else {
            User savedUser = userRepository.save(user);
            LOGGER.info("[getSignUpResult] userEntity 값이 들어왔는지 확인 후 결과값 주입");
            if (!savedUser.getName().isEmpty()) {
                LOGGER.info("[getSignUpResult] 정상 처리 완료");
                setSuccessResult(signUpResultDto);
            } else {
                LOGGER.info("[getSignUpResult] 실패 처리 완료");
                setFailResult(signUpResultDto);
            }
        }

        return signUpResultDto;
    }

    @Override
    public MypageResponseDto getUserDetail(String token) {
        User user = userRepository.getByUid(jwtTokenProvider.getUsername(token));

        MypageResponseDto mypageResponseDto = new MypageResponseDto();
        mypageResponseDto.setName(user.getName());
        mypageResponseDto.setUid(user.getUid());

        Resource file = profileService.loadFileAsResource(user.getProfileUrl());
        String picUrl = "http://localhost:8080/api/v1/profile/images/" + file.getFilename();

        mypageResponseDto.setProfileUrl(picUrl);

        return mypageResponseDto;
    }

    @Override
    public SignInResultDto signIn(SignInRequestDto signInRequestDto) throws RuntimeException {
        LOGGER.info("[getSignInResult] signDataHandler 로 회원 정보 요청");
        User user = userRepository.getByUid(signInRequestDto.getUid());
        LOGGER.info("[getSignInResult] Id : {}", signInRequestDto.getUid());

        LOGGER.info("[getSignInResult] 패스워드 비교 수행");
        if (!passwordEncoder.matches(signInRequestDto.getPassword(), user.getPassword())) {
            throw new RuntimeException();
        }
        LOGGER.info("[getSignInResult] 패스워드 일치");

        LOGGER.info("[getSignInResult] SignInResultDto 객체 생성");
        SignInResultDto signInResultDto = SignInResultDto.builder()
                .token(jwtTokenProvider.createToken(String.valueOf(user.getUid()),
                        user.getRoles()))
                .uid(user.getUid())
                .build();

        LOGGER.info("[getSignInResult] SignInResultDto 객체에 값 주입");
        setSuccessResult(signInResultDto);

        return signInResultDto;
    }

    // 결과 모델에 api 요청 성공 데이터를 세팅해주는 메소드
    private void setSuccessResult(SignUpResultDto result) {
        result.setSuccess(true);
        result.setCode(CommonResponse.SUCCESS.getCode());
        result.setMsg(CommonResponse.SUCCESS.getMsg());
    }

    // 결과 모델에 api 요청 실패 데이터를 세팅해주는 메소드
    private void setFailResult(SignUpResultDto result) {
        result.setSuccess(false);
        result.setCode(CommonResponse.FAIL.getCode());
        result.setMsg(CommonResponse.FAIL.getMsg());
    }
}
