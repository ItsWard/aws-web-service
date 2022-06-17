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

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }

    //Page 88 ~ 89 END


}
