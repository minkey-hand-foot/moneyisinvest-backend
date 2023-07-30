package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.request.PostRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.PostResponseDto;
import org.knulikelion.moneyisinvest.data.dto.request.PostUpdateRequestDto;

public interface PostService {
    PostResponseDto getPost(Long id);
    PostResponseDto newPost(PostRequestDto postRequestDto);
    PostResponseDto modifyPost(PostUpdateRequestDto postUpdateRequestDto);
    void deletePost(Long id) throws Exception;
}
