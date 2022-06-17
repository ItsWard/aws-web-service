package com.ward.springboot.web.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//Page 112
@Getter
@NoArgsConstructor
public class PostUpdateRequestDto {
    private String title;
    private String content;

    @Builder
    public PostUpdateRequestDto(String title, String content){
        this.title = title;
        this.content = content;
    }

    //Page 112 END
}
