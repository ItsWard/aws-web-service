package com.ward.springboot.web.dto;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

//Page 61
@Getter // 선언된 모든 필드 내 get 메소드를 생성
@RequiredArgsConstructor //final 필드가 포함된 생성자를 만듦
public class HelloResponseDto {

    private final String name;
    private final int amount;

}
