package com.ward.springboot.service;

import com.ward.springboot.domain.posts.Posts;
import com.ward.springboot.domain.posts.PostsRepository;
import com.ward.springboot.log.LogExecutionTime;
import com.ward.springboot.web.dto.PostUpdateRequestDto;
import com.ward.springboot.web.dto.PostsResponseDto;
import com.ward.springboot.web.dto.PostsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

//Page 105
@RequiredArgsConstructor
@Service
public class PostsService {
    private final PostsRepository postsRepository;

    @Transactional
    public Long save(PostsSaveRequestDto requestDto) {
        return postsRepository.save(requestDto.toEntity()).getId();
    }
    //Page 105 END

    //Page 113

    @Transactional
    public Long update(Long id, PostUpdateRequestDto requestDto) {
        Posts posts = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));
        posts.update(requestDto.getTitle(), requestDto.getContent());

        return id;
    }


    public PostsResponseDto findById(Long id) {
        Posts entity = postsRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 게시글이 없습니다. id=" + id));

        return new PostsResponseDto(entity);
    }


//Page 113 END
}
