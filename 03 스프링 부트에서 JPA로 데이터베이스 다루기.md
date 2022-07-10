# 03 스프링 부트에서 JPA로 데이터베이스 다루기

JPA라는 자바 표준 ORM 기술을 사용해 **객체를 테이블에 맞추어 데이터를 전달하는 형식** 으로 변환하여 데이터베이스를 다뤄보도록 한다.
현대의 웹 애플리케이션에서 관계형 데이터베이스는 빠질 수 없는 요소이다 보니 이전 코드들이 모든 코드는 SQL 중심이 되기 시작했고          

1. 애플리케이션 코드보다 SQL의 비중이 커지고,
2. 각각의 관계형 데이터베이스마다 쿼리문이 달라 프로그램이 제각각인 문제가 있었고,
3. 객체지향과 관계형 데이터 베이스간의 패러다임 불일치 문제가 있었음
* 관계형 데이터베이스 : 2차원 구조 모델, 어떻게 데이터를 저장 해야하는지 
* 객체지향 프로그래밍 : 메시지를 기반으로 기능과 속성을 한 곳에서 관리하는 기술


```
User user = findUser();
Group group = user.getGroup();
```
위 코드와 같이 User와 Group 부모 자식 관계로 User를 통해서 Group을 얻을 수 있지만
여기에 데이터베이스 코드가 들어가게 된다면  
```
User user = userDao.findUser();
Group group = GroupDao.findGroup(user.getGroupId());
```
상속, 1:N 등 다양한 객체 모델링을 데이터베이스로는 구현을 할 수 없어 각각에 DAO를 생성해주어 따로따로 조회를 해야하는 번거로움이 생긴다.          
이렇다 보니 웹 애플리케이션 개발은 점점 데이터베이스 모델링에만 집중하게 되었음. 


**JPA**는 서로 지향하는 바가 다른 2개를 패러다임 일치를 시켜주기 위한 기술로    
JPA가 이를 관계형 데이터베이스에 맞게 SQL을 대신 생성해서 실행하기 때문에 개발자는 객체지향 프로그래밍만 신경쓰면 되는 것으로 SQL에 종속적인 개발을 하지 않아도 된다.  


## 1.1. SpringData JPA 

JPA는 인터페이스로 자바 표준 명세서이다.  
  
Spring에서는 이러한 구현체를 직접 다루지 않고 이 위에 SpringData JPA 모듈을 이용하여 JPA 기술을 다룬다.  

```
JPA <- Hibernate <- SpringData JPA
```  

이렇게 사용하는 이유는 유지보수를 편하기 하기 위해서이다.   
    
1. 구현체 교체의 용이성  
2. 저장소 교체의 용이성   



**저장소 교체의 용이성**

> 관계형 데이터베이스 외에 다른 저장소로 쉽게 교체한다.

서비스 초기에는 관계형 데이터베이스로 모든 기능을 처리했지만,  
점점 트래픽이 많아져 관계형 데이터베이스로는 도저히 감당이 안될 때 noSql로 교체를 할 수도 있다.(Mongo DB, Redis)   
이때 개발자는 교체를 원한다면 SpringData JPA 에서 SpringData MongoDB로 의존성만 교체하면 동일하게 사용할 수 있다.
   
이는 SpringData의 하위 프로젝트들은 기본적으로 CRUD의 인터페이스가 같기 때문에 그렇다보니 저장소가 교체되어도 기본적인 기능은 변경할 것이 없다. 


## 1.3 요구사항 분석
     
**게시판 기능**   
   
* 게시글 조회
* 게시글 등록
* 게시글 수정
* 게시글 삭제


**회원 기능**   
    
* 구글 / 네이버 로그인    
* 로그인한 사용자 글 작성 권한    
* 본인 작성 글에 대한 권한 관리    


## 2.1 게시글 클래스 

```spring-boot-starter-data-jpa```
  * 스프링 부트용 Spring Data Jap 추상화 라이브러리 
  * 스프링 부트 버전에 맞춰 자동으로 JPA 관련 라이브러리들의 버전을 관리함.
  
```h2```
  * 인메모리 관계형 데이터베이스    
  * 별도의 설치가 필요 없이 프로젝트 의존성만으로 관리할 수 있음.   
  * 메모리에서 실행되기 때문에 애플리케이션을 재시작할 때마다 초기화된다는 점을 이용하여 테스트 용도로 많이 사용함.     


게시글 기능을 사용하기 위해 DB와 매칭될 클래스 생성을 한다.

**Post 클래스 생성**

Posts 클래스는 실제 DB의 테이블과 매칭될 클래스이며 보통 Entity 클래스라고 부른다.     
JPA를 사용한다면 DB 데이터에 작업할 경우 실제 쿼리를 날리기보다는, 이 Entity 클래스의 수정을 통해 작업한다.


```
@Getter
@NoArgsConstructor
@Entity
public class Posts extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long Id;

    @Column(length = 500, nullable = false)
    private String title;

    @Column( columnDefinition = "TEXT", nullable = false)
    private String content;

    private String author;

    @Builder
    public Posts(String title, String content, String author){
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public void update(String title, String content){
        this.title = title;
        this.content = content;
    }
}
```

Posts 클래스는 실제 DB의 테이블과 매칭될 클래스이며 보통 Entity 클래스라고 부름.     
영속성 컨텍스트에 의해 관리되며, 실제로 업데이트 전 캐시로 사용됨.


웬만하면 Entity의 PK는 Long 타입의 Auto_increment를 사용해야함.(id를 많이 사용하게 됨)

주민등록번호와 같이 비즈니스상 유니크 키나, 여러 키를 조합한 복합키로 PK를 선정할 경우 
   
1. FK 를 맺을때 다른 테이블에서 복합키 전부를 갖고 있거나, 중간 테이블을 하나 더 두는 상황이 생긴다.  
2. 인덱스에 좋은 영향을 끼치지 못한다.  
3. 유니크한 조건이 변경될 경우 PK 전체를 수정해야 하는 일이 발생  


```@Entity```

  * 테이블과 링크될 클래스 
  * 기본값으로 필드의 이름을 언더스코어 네이밍으로 테이블 이름을 매칭함(Hibernate Naming 규칙에 의해) 
  
  
```@Id```

  * 해당 테이블의 PK 필드를 나타낸다.
  * Entity 를 선언한 경우 반드시 있어야함.


```@GerneratedValue```

  * PK의 생성 규칙
  * 스프링부트 2.0 에서는 GenerationType.IDENTITY 옵션을 추가해야만 auto_increment가 된다.   

```@Column```

  * 테이블의 칼럼을 나타내며 굳이 선언하지 않더라도 해당 클래스의 필드는 모두 칼럼이 된다.  
  * 사용하는 이유는, 기본값 외에 추가로 변경이 필요한 옵션이 있으면 사용한다.  
  * 문자열의 경우 VARCHAR(255)가 기본값.

```@NoArgsConstructor```

  * 기본 생성자 자동 추가 -> ```public Posts(){}```

```@Getter```

  * 클래스 내 모든 필드의 Getter 메소드를 자동생성  
  
```@Builder```
  * 해당 클래의 빌더 패턴 클래스를 생성
  * 생성자 상단에 선언시 생성자에 포함된 빌드만 빌더에 포함  
  
  
@Setter를 사용하는 경우 인스턴스가 언제 어디서 변해야 하는지 코드상으로 명확하게 구분할 수가 없어, 유지보수가 힘들어짐    
그래서 @Entity 클래스에서는 절대 Setter 메소드를 만들지 않는 것이 좋음.     

    
- Setter가 없이 DB 데이터를 입력할 수 있는 방법은?       
    
생성자를 통해 데이터를 채운 후 DB에 삽입 하는 것이며,      
값 변경이 필요한 경우 해당 이벤트에 맞는 public 메소드를 호출하여 변경하는 것을 전제로한다.(불변성 유지)   
           
기본 생성자 대신에 @Builder를 통해 제공되는 빌더 클래스를 사용하는 경우 지금 채워야 할 필드가 무엇인지 명확하게 지정을 할 수 있다.
[빌더 패턴 이란?](https://its-ward.tistory.com/entry/Spring-%EB%B9%8C%EB%8D%94-%ED%8C%A8%ED%84%B4Bulider-Pattern%EC%97%90-%EB%8C%80%ED%95%B4-%EC%95%8C%EC%95%84%EB%B3%B4%EC%9E%90)



## 2.2 게시글 데이터 베이스 접근을 위한 JPA Repository 생성

public interface PostsRepository extends JpaRepository<Posts,Long> {
}
```   
DAO를 JPA에서는 Repository라고 부르며 인터페이스로 생성한다.        
단순히 인터페이스를 생성한 후, ```JpaRepository<Entity 클래스, PK 타입>```을 상속하면      
**기본적인 CRUD 메소드가 자동으로 생성된다.**      
     
**Entity 클래스와 EntityRepository는 함께 위치하는 것을 권장한다.**          
프로젝트 규모가 커졌을시에 함께 움직여야 하므로 도메인 패키지에서 함께 관리하는 것이 좋다.



### 2.3 Spring Data JPA 테스트 코드 작성

test 디렉토리에 domain.posts 패키지를 생성하고, 테스트 클래스는 PostsRepositoryTest란 이름으로 생성한다.

```
@ExtendWith(SpringRunner.class) // Junit5에서 
@SpringBootTest
public class PostsRepositoryTest {

    @Autowired
    PostsRepository postsRepository;

    @After
    public void cleanup(){
        postsRepository.deleteAll();
    }

    @Test
    public void 게시글저장_불러오기(){
        // given
        String title = "테스트 게시글";
        String content = "테스트 본문";

        postsRepository.save(Posts.builder()
                                    .title(title)
                                    .author("ward.ward.com")
                                    .content(content)
                                    .build());

        // when
        List<Posts> postsList = postsRepository.findAll();

        //then
        Posts posts = postsList.get(0);
        assertThat(posts.getTitle()).isEqualTo(title);
        assertThat(posts.getContent()).isEqualTo(content);
    }
}


```@AfterEach```

     * Junit5에서 단위 테스트가 끝날 때마다 수행되는 메소드를 지정  
     * H2에 데이터가 그대로 남아 있어 다음 테스트 실행 시 테스트가 실패할 수 있기때문에, 이후 비워주는 역할로 챕터에서 진행

```postsRepository.save()```

     * 테이블에 posts에 insert/update 쿼리를 실행한다. - 영속성 더티체킹
     * id값이 있다면, update가 없다면 insert 쿼리가 실행된다.  

```postsRepository.findAll()```

     * 테이블 posts에 있는 모든 데이터를 조회해오는 메소드(JpaRepository 상속 메소드) 
     
     
     
## 3.1 등록/수정/조회 API 만들기  


API를 만들기 위해 총 3개의 클래스가 필요하다.    
    
1. Request 데이터를 받을 Dto        
2. API 요청을 받을 Controller     
3. 트랜잭션, 도메인 기능 간의 순서를 보장하는 Service    
  
여기서 많은 사람들이 **Service 에서 비즈니스 로직을 처리**해야 된다고 생각하고 있다.       
하지만 이는 과거이고 큰 오해이며 현재는 Service는 **트랜잭션, 도메인 간 순서 보장의 역할**만 한다.          
    

* Web Layer
     * 흔히 사용하는 @Controller 와 JSP/Freemarker 등의 뷰 템플릿 영역  
     * 이외에도 필터, 인터셉터, 컨트롤러 어드바이스등 외부 요청과 응답에 대한 전반적인 영역임. 
  
* Service Layer
     * @Service에 사용되는 서비스 영역 
     * 일반적으로 Controller와 DAO의 중간 영역에서 사용됨 
     * @Transactional 이 사용되어야 하는 영역이기도 한다.  

* Repository Layer
     * Database와 같이 데이터 저장소에 접근하는 영역 

* Dto
     * Dto 는 계층 간에 데이터 교환을 위한 객체
     * 뷰 템플릿 엔진에서 사용될 객체나 Repositroy Layer에서 결과로 넘겨준 객체

* Domain Model  
     * 도메인이라 불리는 개발 대상을 모든 사람이 동일한 관점에서 이해할 수 있고 공유할 수 있도록 단순화시킨 것을 도메인 모델이라 한다.    
     * @Entity를 사용하는 클래스도 도메인 모델이라 할 수 있다.  
     * 다만 무조건 데이터베이스의 테이블과 관계가 있어야만 하는 것은 아니며 VO처럼 값 객체들도 이 영역에 해당한다.
     

자세한 내용은 [스프링의 특징과 계층구조](https://its-ward.tistory.com/entry/Spring-Spring%EC%9D%98-%ED%8A%B9%EC%A7%95%EA%B3%BC-%EA%B3%84%EC%B8%B5-%EA%B5%AC%EC%A1%B0)에서 확인 할 수 있다.

### 3.2 H2 데이터베이스에 접근해보기  
로컬 환경에선 데이터베이스로 H2를 주로 사용한다.      
메모리에서 실행하기 때문에 직접 접근하려면 웹 콘솔을 사용해야만 함.     
       
먼저 아래와 같은 방법으로 웹 콘솔 옵션을 활성화 할 수 있다.  
   
**application.properties** 에 아래 코드를 추가한다.

```
spring.h2.console.enable=true
```

추가한 뒤 ```Application.class``` 의 main 메소드를 실행하고  
웹 브라우저에 ```http://localhost:8080/h2-console``` 로 접속하자   
그 후 JDBC URL이 ```jdbc:h2:mem:testdb```가 쓰여져 있는지 확인 후 connect 버튼을 눌러주자

이후 ```select * from posts```와 같은 간단한 쿼리를 입력해보면 쿼리가 정상적으로 실행된다.  
물론 아직 insert를 하지 않았지만 insert 후에 확인해보면 데이터가 정상 출력되는 것을 알 수 있다.  

## 4.1 JPA Auditing으로 생성시간/수정시간 자동화하기   
보통 엔티티에는 해당 데이터의 생성시간과 수정시간을 포함한다.               
언제 만들어졌는지, 언제 수정되었는지 등은 차후 유지보수에 있어 굉장히 중요한 정보이기 때문이다.           
그래서 DB에 삽입/갱신하기 전에 날짜 데이터를 등록해주는데            
이런 단순하고 반복적인 코드가 모든 테이블과 서비스 메소드에 포함되어있다면 이는 매우 귀찮고 지저분해진다.             
그래서 이러한 문제를 해결하기 위해서 JPA Auditing을 사용하자          

### 4.2 LocalDate 사용  
 LocalDate 객체를 사용하여 domain 패키지에 BaseTimeEntity 클래스를 생성한다.    
 BaseTimeEntity 클래스는 모든 Entity의 상위 클래스가 되어        
Entity들의 createDate, modifiedDate를 자동으로 관리하는 클래스로 사용할 예정이다.   
   
 ```
@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseTimeEntity {

    @CreatedDate
    private LocalDateTime createdDate;

    @LastModifiedDate
    private LocalDateTime modifiedDate;

}
```


```@MappedSuperclass```

* JPA Entity 클래스들이 BaseTimeEntity을 상속할 경우 해당 필드의 createdDate 와 modifiedDate 'Column'으로 인식하도록 함.  

```@EntityListeners(AuditingEntityListener.class)```

* BaseTimeEntity 클래스에 데이터베이스의 작업을 모니터링 하고, 기록 정보를 수집 하는 기능을 포함시킨다.  

@CreateDate

* Entity가 생성되어 저장될 때 현재 시간이 자동으로 저장됨  

@LastModifiedDate

* 조회한 Entity의 값을 변경할 때 현재 시간이 자동으로 저장됨.



