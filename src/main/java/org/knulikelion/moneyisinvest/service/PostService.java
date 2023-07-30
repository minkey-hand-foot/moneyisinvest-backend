package org.knulikelion.moneyisinvest.service;

import org.knulikelion.moneyisinvest.data.dto.PostDto;
import org.knulikelion.moneyisinvest.data.dto.PostResponseDto;
import org.knulikelion.moneyisinvest.data.dto.PostUpdateDto;

public interface PostService {
    PostResponseDto getPost(Long id);
    PostResponseDto newPost(PostDto postDto);
    PostResponseDto modifyPost(PostUpdateDto postUpdateDto);
    void deletePost(Long id) throws Exception;
}
