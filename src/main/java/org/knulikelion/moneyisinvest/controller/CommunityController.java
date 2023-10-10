package org.knulikelion.moneyisinvest.controller;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import org.knulikelion.moneyisinvest.config.security.JwtTokenProvider;
import org.knulikelion.moneyisinvest.data.dto.request.CommentRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.CommentUpdateRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.ReplyCommentRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.CommentDetailResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.CommentResponseDto;
import org.knulikelion.moneyisinvest.service.CommunityService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/api/v1/community")
public class CommunityController {
    private final CommunityService communityService;
    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public CommunityController(CommunityService communityService, JwtTokenProvider jwtTokenProvider) {
        this.communityService = communityService;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/")
    public BaseResponseDto postComment(@RequestBody CommentRequestDto commentRequestDto, HttpServletRequest request) {
        return communityService.postComment(commentRequestDto, request.getHeader("X-AUTH-TOKEN"));
    }

    @GetMapping("/")
    public List<CommentResponseDto> getAllCommentByStockId(String stockId) {
        return communityService.getAllCommentByStockId(stockId);
    }

    @GetMapping("/detail")
    public List<CommentDetailResponseDto> getAllCommentByStockIdContainsAllReply(String stockId, HttpServletRequest request) {
        if(request.getHeader("X-AUTH-TOKEN") == null) {
            return communityService.getAllCommentByStockIdContainsAllReply(stockId, null);
        } else {
            return communityService.getAllCommentByStockIdContainsAllReply(stockId, jwtTokenProvider.getUsername(request.getHeader("X-AUTH-TOKEN")));
        }
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/")
    public BaseResponseDto removeCommentById(Long id) {
        return communityService.removeComment(id);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PutMapping("/")
    public BaseResponseDto updateCommentById(@RequestBody CommentUpdateRequestDto commentUpdateRequestDto) {
        return communityService.updateComment(commentUpdateRequestDto);
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PostMapping("/reply")
    public BaseResponseDto replyComment(@RequestBody ReplyCommentRequestDto replyCommentRequestDto, HttpServletRequest request) {
        return communityService.replyComment(replyCommentRequestDto, request.getHeader("X-AUTH-TOKEN"));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @PutMapping("/reply")
    public BaseResponseDto updateReplyComment(@RequestBody ReplyCommentRequestDto replyCommentRequestDto,
                                              HttpServletRequest request) {
        return communityService.updateReplyComment(replyCommentRequestDto, request.getHeader("X-AUTH-TOKEN"));
    }

    @ApiImplicitParams({
            @ApiImplicitParam(name = "X-AUTH-TOKEN", value = "로그인 성공 후 발급 받은 access_token", required = true, dataType = "String", paramType = "header")
    })
    @DeleteMapping("/reply")
    public BaseResponseDto removeReplyComment(@RequestParam Long replyId, HttpServletRequest request) {
        return communityService.removeReplyComment(replyId, request.getHeader("X-AUTH-TOKEN"));
    }
}
