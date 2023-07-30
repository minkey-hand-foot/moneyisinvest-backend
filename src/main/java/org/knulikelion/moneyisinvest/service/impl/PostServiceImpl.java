package org.knulikelion.moneyisinvest.service.impl;

import org.knulikelion.moneyisinvest.data.dto.request.PostRequestDto;
import org.knulikelion.moneyisinvest.data.dto.response.PostResponseDto;
import org.knulikelion.moneyisinvest.data.dto.request.PostUpdateRequestDto;
import org.knulikelion.moneyisinvest.data.entity.Post;
import org.knulikelion.moneyisinvest.data.repository.PostRepository;
import org.knulikelion.moneyisinvest.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PostServiceImpl implements PostService {
    private final PostRepository postRepository;

    @Autowired
    public PostServiceImpl(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Override
    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findById(id).get();

        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(post.getId());
        postResponseDto.setTitle(post.getTitle());
        postResponseDto.setContents(post.getContents());

        return postResponseDto;
    }

    @Override
    public PostResponseDto newPost(PostRequestDto postRequestDto) {
        Post post = new Post();
        post.setTitle(postRequestDto.getTitle());
        post.setContents(postRequestDto.getContents());

        Post savedPost = postRepository.save(post);

        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(savedPost.getId());
        postResponseDto.setTitle(savedPost.getTitle());
        postResponseDto.setContents(savedPost.getContents());

        return postResponseDto;
    }

    @Override
    public PostResponseDto modifyPost(PostUpdateRequestDto postUpdateRequestDto) {
        Post post = postRepository.findById(postUpdateRequestDto.getId()).get();
        post.setTitle(postUpdateRequestDto.getTitle());
        post.setContents(postUpdateRequestDto.getContents());
        Post modifiedPost = postRepository.save(post);

        PostResponseDto postResponseDto = new PostResponseDto();
        postResponseDto.setId(modifiedPost.getId());
        postResponseDto.setTitle(modifiedPost.getTitle());
        postResponseDto.setContents(modifiedPost.getContents());

        return postResponseDto;
    }

    @Override
    public void deletePost(Long id) throws Exception {
        postRepository.deleteById(id);
    }
}
