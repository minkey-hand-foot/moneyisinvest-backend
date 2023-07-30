package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.knulikelion.moneyisinvest.data.dto.request.PostRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.PostResponseDto;
import org.knulikelion.moneyisinvest.data.dto.request.PostUpdateRequestDto;
import org.knulikelion.moneyisinvest.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/post")
public class PostController {
    private final PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @GetMapping("/get")
    public ResponseEntity<PostResponseDto> getPost(Long id) {
        PostResponseDto postResponseDto = postService.getPost(id);

        return ResponseEntity.status(HttpStatus.OK).body(postResponseDto);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/new")
    public ResponseEntity<PostResponseDto> newPost(@RequestBody PostRequestDto postRequestDto) {
        PostResponseDto postResponseDto = postService.newPost(postRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(postResponseDto);
    }

    @PutMapping("/modify")
    public ResponseEntity<PostResponseDto> modifyPost(@RequestBody PostUpdateRequestDto postUpdateRequestDto) throws Exception {
        PostResponseDto postResponseDto = postService.modifyPost(postUpdateRequestDto);

        return ResponseEntity.status(HttpStatus.OK).body(postResponseDto);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<String> deletePost(Long id) throws Exception {
        postService.deletePost(id);

        return ResponseEntity.status(HttpStatus.OK).body("삭제가 정상적으로 완료되었습니다.");
    }
}
