package com.ward.springboot.web;


import com.ward.springboot.config.auth.SecurityConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class) // 스프링부트 테스트와 JUnit 사이에 연결자 역할
@WebMvcTest(controllers = HelloController.class, excludeFilters = {
        @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = SecurityConfig.class)
    }
)
public class HelloControllerTest {

    //Page 61~62 , 스프링부트 2.7.0 버전인 경우 이슈쉐어링 확인
    @Autowired // 스프링이 관리하는 Bean 주입 받기
    private MockMvc mvc; // 웹 API 테스트(GET, POST 등 API 테스트)

    @WithMockUser(roles = "USER")
    @Test
    public void returnhelloOk() throws Exception{
        String hello = "hello";

        mvc.perform(get("/hello")) // MockMvc 통해 /hello 주소로 HTTP GET 요청 (아래 검증기능 이어서 선언 가능)
                .andExpect(status().isOk()) // mvc.perform 결과 검증, HTTP Header Status 검증
                .andExpect(content().string(hello)); // Controller => "hello" 리턴 값 맞는지 검증
    }
    @WithMockUser(roles = "USER")
    @Test
    public void helloDto리턴() throws Exception {

        String name = "hello";
        int amount = 1000;

        mvc.perform(
                get("/hello/dto") // Get방식으로 접근
                        .param("name", name)
                        .param("amount", String.valueOf(amount)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name", is(name)))
                .andExpect(jsonPath("$.amount", is(amount)));

    }

}
