package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.request.CommentRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.CommentUpdateRequestDto;
import org.knulikelion.moneyisinvest.data.dto.request.ReplyCommentRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.CommentDetailResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.CommentResponseDto;

import java.util.List;

public interface CommunityService {
    BaseResponseDto postComment(CommentRequestDto commentRequestDto, String token);
    BaseResponseDto replyComment(ReplyCommentRequestDto replyCommentRequestDto, String token);
    List<CommentResponseDto> getAllCommentByStockId(String stockId);
    List<CommentDetailResponseDto> getAllCommentByStockIdContainsAllReply(String stockId);
    BaseResponseDto removeComment(Long id);
    BaseResponseDto updateComment(CommentUpdateRequestDto commentUpdateRequestDto);
    BaseResponseDto updateReplyComment(ReplyCommentRequestDto replyCommentRequestDto, String token);
    BaseResponseDto removeReplyComment(Long replyId, String token);
}
