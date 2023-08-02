package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.ProfilePictureUrlResponseDto;
import org.knulikelion.moneyisinvest.data.repository.UserRepository;
import org.knulikelion.moneyisinvest.service.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;

@RestController
@RequestMapping("/api/v1/profile")
public class ProfileController {
    private final ProfileService profileService;
    private final UserRepository userRepository;

    @Autowired
    public ProfileController(ProfileService profileService, UserRepository userRepository) {
        this.profileService = profileService;
        this.userRepository = userRepository;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/upload")
    public BaseResponseDto uploadProfilePicture(@RequestParam("file") MultipartFile file, @RequestParam("uid") String uid) {
        return profileService.storeFile(file, uid);
    }

    @GetMapping("/images/{fileName:.+}")
    public ResponseEntity<Resource> getProfilePicture(@PathVariable String fileName) {
        Resource file = profileService.loadFileAsResource(fileName);

        String contentType;
        try {
            contentType = Files.probeContentType(file.getFile().toPath());
        } catch (IOException e) {
            throw new RuntimeException("Could not determine file type for " + fileName, e);
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/get")
    public ProfilePictureUrlResponseDto getUserProfilePicUrl(String uid) {
        ProfilePictureUrlResponseDto profilePictureUrlResponseDto = new ProfilePictureUrlResponseDto();
        String pictureUrl = userRepository.getByUid(uid).getProfileUrl();
        Resource file = profileService.loadFileAsResource(pictureUrl);

        String picUrl = "http://localhost:8080/api/v1/profile/images/" + file.getFilename();
        profilePictureUrlResponseDto.setUrl(picUrl);

        return profilePictureUrlResponseDto;
    }

}
