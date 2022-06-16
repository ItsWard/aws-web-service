package com.ward.springboot.web;

import com.ward.springboot.web.dto.HelloResponseDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController //JSON을 반환하는 컨트롤러로 만들어줌
public class HelloController {


    //Page 60
    @GetMapping("/hello") //GET의 요청을 받을 수 있는 API를 만들어줌
    public String hello() {
        return "hello";
    }

    //Page 75
    @GetMapping("/hello/dto") //DTO 계층간 데이터 교환을 하기위해 사용되는 객체
    public HelloResponseDto helloDto (@RequestParam("name") String name,
                                      @RequestParam("amount") int amount){

        return new HelloResponseDto(name, amount);
    }



}

