package com.ward.springboot.web;

import com.ward.springboot.service.PostsService;
import com.ward.springboot.web.dto.PostUpdateRequestDto;
import com.ward.springboot.web.dto.PostsResponseDto;
import com.ward.springboot.web.dto.PostsSaveRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

//Page 105
@RequiredArgsConstructor
@RestController
public class PostsApiController {
    private final PostsService postsService;

    //Page 105
    @PostMapping("/api/v1/posts")
    public Long save(@RequestBody PostsSaveRequestDto requestDto) {
        return postsService.save(requestDto);
    }

    //Page 105 END

    //Page 111
    @PutMapping("/api/v1/posts/{id}")
    public Long update(@PathVariable Long id, @RequestBody PostUpdateRequestDto requestDto){
        return postsService.update(id, requestDto);
    }

    @GetMapping("/api/v1/posts/{id}")
    public PostsResponseDto findById(@PathVariable Long id){
        return postsService.findById(id);
    }
    //Page 111 END

    //Page 160
    @DeleteMapping("/api/v1/posts/{id}")
    public Long delete(@PathVariable Long id){
        postsService.delete(id);
        return id;
    }

}
