package org.knulikelion.moneyisinvest.service.impl;

import org.knulikelion.moneyisinvest.common.CommonResponse;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.request.ChangePasswdRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.SignInRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.TransactionToSystemRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.MypageResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.SignInResultDto;
import org.knulikelion.moneyisinvest.data.dto.request.SignUpRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.SignUpResultDto;
import org.knulikelion.moneyisinvest.data.entity.User;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.ProfileService;
import org.knulikelion.moneyisinvest.service.SignService;
import org.knulikelion.moneyisinvest.service.StockCoinService;
import org.knulikelion.moneyisinvest.service.StockCoinWalletService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
    public StockCoinService stockCoinService;
    public PasswordEncoder passwordEncoder;
    public StockCoinWalletService stockCoinWalletService;

    @Autowired
    public SignServiceImpl(UserRepository userRepository, JwtTokenProvider jwtTokenProvider,
                           PasswordEncoder passwordEncoder, ProfileService profileService,
                           StockCoinWalletService stockCoinWalletService, StockCoinService stockCoinService) {
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
        this.passwordEncoder = passwordEncoder;
        this.profileService = profileService;
        this.stockCoinWalletService = stockCoinWalletService;
        this.stockCoinService = stockCoinService;
    }

    private static final String EMAIL_REGEX = "^([a-zA-Z0-9_\\-\\.]+)@([a-zA-Z0-9_\\-\\.]+)\\.([a-zA-Z]{2,5})$";

    // 주어진 이메일 주소가 유효한지 검증하는 메서드
    public static boolean validateUid(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public SignUpResultDto signUp(SignUpRequestDto signUpRequestDto) {
        LOGGER.info("[getSignUpResult] 회원 가입 정보 전달");
//      회원가입 시 기본적으로 일반 유저 권한으로 가입 처리
        User user = User.builder()
                    .uid(signUpRequestDto.getUid())
                    .name(signUpRequestDto.getName())
                    .plan("basic")
                    .profileUrl("https://kr.object.ncloudstorage.com/moneyisinvest/default-profile.png")
                    .password(passwordEncoder.encode(signUpRequestDto.getPassword()))
                    .roles(Collections.singletonList("ROLE_USER"))
                    .build();

            LOGGER.info("사용자의 새 지갑 생성, UID: " + signUpRequestDto.getUid());
            BaseResponseDto walletResult = stockCoinWalletService.createWallet(signUpRequestDto.getUid());

            if(walletResult.isSuccess()) {
                stockCoinService.giveSignUpCoin(walletResult.getMsg());
            }
            
        SignUpResultDto signUpResultDto = new SignInResultDto();

//      signUpRequestDto에서 입력받는 uid가 이메일 주소인지 확인
        if(!validateUid(signUpRequestDto.getUid())) {
            signUpResultDto.setSuccess(false);
            signUpResultDto.setMsg("아이디는 이메일 주소 형식입니다.");
            signUpResultDto.setCode(1);
        } else if(userRepository.findByUid(user.getUid()) != null) {
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
                .token(jwtTokenProvider.createAccessToken(String.valueOf(user.getUid()), user.getRoles()))
                .refreshToken(jwtTokenProvider.createRefreshToken(String.valueOf(user.getUid())))
                .uid(user.getUid())
                .name(user.getName())
                .build();

        LOGGER.info("[getSignInResult] SignInResultDto 객체에 값 주입");
        setSuccessResult(signInResultDto);

        return signInResultDto;
    }

    @Override
    public BaseResponseDto changePasswd(ChangePasswdRequestDto changePasswdRequestDto, String uid) {
        User foundUser = userRepository.getByUid(uid);
        BaseResponseDto baseResponseDto = new BaseResponseDto();

//        발견된 사용자가 없을 시
        if(foundUser == null) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("사용자를 찾을 수 없습니다.");

            return baseResponseDto;
        }

//        현재 패스워드와 입력된 현재 패스워드가 일치하지 않을 시
        if(!passwordEncoder.matches(changePasswdRequestDto.getCurrentPasswd(), foundUser.getPassword())) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("기존 비밀번호가 일치하지 않습니다.");

            return baseResponseDto;
        }

//        새 패스워드와 입력된 새 패스워드 재입력 값이 일치하지 않을 시
        if(!changePasswdRequestDto.getNewPasswd().equals(changePasswdRequestDto.getNewPasswdRe())) {
            baseResponseDto.setSuccess(false);
            baseResponseDto.setMsg("새로운 비밀번호와 새로운 비밀번호 재입력이 일치하지 않습니다.");

            return baseResponseDto;
        }

        foundUser.setPassword(passwordEncoder.encode(changePasswdRequestDto.getNewPasswd()));
        userRepository.save(foundUser);

        baseResponseDto.setSuccess(true);
        baseResponseDto.setMsg("비밀번호 변경이 완료되었습니다.");

        return baseResponseDto;
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
