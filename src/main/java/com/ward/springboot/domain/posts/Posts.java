package com.ward.springboot.domain.posts;

import com.ward.springboot.domain.BaseTimeEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import javax.persistence.*;

//Page 88 ~ 89
@Getter
@NoArgsConstructor
@Entity //DB 테이블과 링크될 클래스,
public class Posts extends BaseTimeEntity {

    @Id // PK 필드 Primary Key
    @GeneratedValue(strategy = GenerationType.IDENTITY) //PK 생성규칙 IDENTITY는 auto increment됨
    private Long id;

    @Column( length = 500, nullable = false) //칼럼의 문자열 크기 변경 가능
    private String title;

    @Column(columnDefinition = "TEXT", nullable = false) //해당 데이터베이스의 칼럼의 타입을 TEXT로 변경
    private String content;

    private String author;

    @Builder
    public Posts(String title, String content, String author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }


    //PostsService에서 Transaction이 끝나는 시점에, 쿼리를 따로 날리지않아도
    //JPA의 영속성 컨텍스트(엔티티를 영구 저장하는 환경)에 의해
    //해당 테이블에 변경분을 반영함 -> 더티 체킹
    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }

    //Page 88 ~ 89 END


}
