package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.request.CommentRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.BaseResponseDto;
import org.knulikelion.moneyisinvest.data.dto.response.CommentResponseDto;

import java.util.List;

public interface CommunityService {
    BaseResponseDto postComment(CommentRequestDto commentRequestDto);
    List<CommentResponseDto> getAllCommentByStockId(Long stockId);
}
