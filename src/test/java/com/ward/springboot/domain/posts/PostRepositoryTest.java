package com.ward.springboot.domain.posts;


import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


//Page.96
@ExtendWith(SpringExtension.class) // RunWith(SpringRunner.class) : junit4 -> junit5(spring 2.4.1 testImplementation 내장)
@SpringBootTest
public class PostRepositoryTest {

    @Autowired //Bean
    PostsRepository postsRepository; // Posts 저장소

    @AfterEach
    public void cleanup() {
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기() {
        //given -> 테스트에서 구체화하고자 하는 행동을 시작하기 전에 테스트 상태를 설명하는 부분
        String title = "테스트 게시글";
        String content = "테스트 본문";

        postsRepository.save(Posts.builder() //Build형태로 저장
                .title(title)
                .content(content)
                .author("ward@ward.com")
                .build());
        //when -> 구체화하고자 하는 그 행동 or 대상

        List<Posts> postsList = postsRepository.findAll(); //postsList에 저장된 모든 postsRepository를 저장

        //then -> 어떤 특정한 행동 때문에 발생할거라고 예상되는 변화에 대한 설명
        Posts posts = postsList.get(0);//어차피 1개 넣었으니까 0번째 확인
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);
    }
    //Page.96 END

    //Page 122~ 123

    @Test
    public void BaseTimeEntity등록() {
        //given -> 테스트에서 구체화하고자 하는 행동을 시작하기 전에 테스트 상태를 설명하는 부분
        LocalDateTime now = LocalDateTime.of(2022,06,18,0,0,0); //현재시간 넣기

        postsRepository.save(Posts.builder()
                .title("title")
                .content("content")
                .author("author")
                .build());
        //when -> 구체화하고자 하는 그 행동 or 대상
        List<Posts> postsList = postsRepository.findAll();

        //then -> 어떤 특정한 행동 때문에 발생할거라고 예상되는 변화에 대한 설명
        Posts posts = postsList.get(0);

        System.out.println(">>>>>>>>>>> createDate=" + posts.getCreateDate() + ", modifiedDate=" + posts.getModifiedDate());

        assertThat(posts.getCreateDate()).isAfter(now);
        assertThat(posts.getModifiedDate()).isAfter(now);

    }


}
