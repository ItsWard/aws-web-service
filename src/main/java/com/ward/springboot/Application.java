package com.ward.springboot;

import org.aspectj.weaver.ast.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.ApplicationPidFileWriter;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.bind.annotation.RequestParam;

//Page 57
// @EnableJpaAuditing
@SpringBootApplication //해당 어노테이션이 있는 부분부터 설정진행, 내장 WAS 톰캣 서버 작동 가능
public class Application {
    public static void main(String[] args) {
        SpringApplication application = new SpringApplication(Application.class);
        application.addListeners(new ApplicationPidFileWriter());
        application.run(args);

    }
}
