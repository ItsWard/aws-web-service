package com.ward.springboot.web.dto;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

//Page 73

public class HelloResponseDtoTest {

    @Test
    public void 롬복_기능_테스트() {
        //given -> 테스트에서 구체화하고자 하는 행동을 시작하기 전에 테스트 상태를 설명하는 부분
        String name = "test";
        int amount = 1000;

        //when -> 구체화하고자 하는 그 행동
       HelloResponseDto dto = new HelloResponseDto(name, amount);

        //then -> 어떤 특정한 행동 때문에 발생할거라고 예상되는 변화에 대한 설명
        assertThat(dto.getName()).isEqualTo(name);
        assertThat(dto.getAmount()).isEqualTo(amount);
    }
}
