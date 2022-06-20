package com.ward.springboot.web;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class IndexControllerTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void 메인페이지_로딩(){
        //given -> 테스트에서 구체화하고자 하는 행동을 시작하기 전에 테스트 상태를 설명하는 부분
        //when -> 구체화하고자 하는 그 행동 or 대상
        String body = this.restTemplate.getForObject("/", String.class);
        //then -> 어떤 특정한 행동 때문에 발생할거라고 예상되는 변화에 대한 설명
        assertThat(body).contains("스프링 부트로 시작하는 웹 서비스");
    }

}
