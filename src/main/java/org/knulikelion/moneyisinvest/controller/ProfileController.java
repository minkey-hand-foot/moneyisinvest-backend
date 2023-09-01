package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.MypageResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.ProfilePictureUrlResponseDto;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public ProfileController(ProfileService profileService, UserRepository userRepository, JwtTokenProvider jwtTokenProvider) {
        this.profileService = profileService;
        this.userRepository = userRepository;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/upload")
    public BaseResponseDto uploadProfilePicture(@RequestParam("file") MultipartFile file, HttpServletRequest request) {
        return profileService.storeFile(file, jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")));
    }

    @GetMapping("/reset")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    public ResponseEntity<BaseResponseDto> resetProfile(HttpServletRequest request) {
        return ResponseEntity.status(HttpStatus.OK).body(
                profileService.resetProfile(
                        jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN"))
                )
        );
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/user/detail")
    public MypageResponseDto getUserDetail(HttpServletRequest request) {
        return profileService.getUserDetail(request.getHeader("X-AUTH-TOKEN"));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/get")
    public ProfilePictureUrlResponseDto getUserProfilePicUrl(HttpServletRequest request) {
        ProfilePictureUrlResponseDto profilePictureUrlResponseDto = new ProfilePictureUrlResponseDto();
        String pictureUrl = userRepository.getByUid(jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN"))).getProfileUrl();
        profilePictureUrlResponseDto.setUrl(pictureUrl);

        return profilePictureUrlResponseDto;
    }

}
