package com.ward.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//Page 57
@SpringBootApplication //해당 어노테이션이 있는 부분부터 설정진행, 내장 WAS 톰캣 서버 작동 가능
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}
