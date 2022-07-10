# 05 스프링 시큐리티와 OAuth2.0으로 로그인 기능 구현하기.md

해당 교재에서는 로그인 기능을 따로 구현하지 않고, 외부 소셜 로그인을 통해 진행을한다.

로그인을 구현할 경우 간단하게 로그인 기능만 구현해서 끝나는 것이 아니라 다음과 같이 오버해드가 커지고, 책에서 집중하려고 하는 부분과는 
조금 다르기 떄문에 소셜 로그인으로 진행하는 것 같다.

* 로그인 시 보안 
* 비밀번호 찾기
* 비밀번호 변경
* 회원정보 변경
* 회원가입시 이메일 혹은 전화번호 인증

이와 같은 모든 것들을 구현하는 것 보다, 서비스 부분의 큰 부분을 보고싶기 때문에 다른 프로젝트에서 로그인 기능을 구현할 예정이다.

## 1.1 구글 서비스 등록 

소셜 로그인을 진행하기위해 다음과 같이 의존성을 추가한다.

```C
    implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
    testImplementation 'org.springframework.boot:spring-boot-starter-oauth2-client'
```

또한, 로그인 정보를 프로그램에서 사용하기 위해 설정파일인 properties 를 src/main/resources/디렉토리에 
```application-oauth.properties``` 를 생성한다.

해당 설정파일에는 구글과 네이버 비밀번호를 저장하므로 자세한 내용은 적지않고 진행방법에대해 서술한다.

[구글 API 로그인 등록](https://console.cloud.google.com/apis/credentials?hl=ko&project=ward-springboot-webservice) 해당 페이지로 이동하여 다음과 같이 진행한다.


![image](https://user-images.githubusercontent.com/104341003/178155020-ef53bffc-9f11-4df7-a826-7891a70ef9e5.png)

![image](https://user-images.githubusercontent.com/104341003/178155026-8a4464e7-8c15-4775-9354-1dd3d6972f7d.png)

![image](https://user-images.githubusercontent.com/104341003/178155029-500d1a03-7eeb-4553-a3aa-127ad03e7459.png)

![image](https://user-images.githubusercontent.com/104341003/178155031-9f15c770-452d-4d42-b808-3a5e9bf5e26e.png)

이후 클라이언트 아이디와 보안비밀번호를 다음과같이 ```application-oauth.properties```에 작성한다.

```java
spring.security.oauth2.client.registration.google.client-id=클라이언트ID
spring.security.oauth2.client.registration.google.client-secret=클라이언트PW
spring.security.oauth2.client.registration.google.scope=profile, email
```

또한 기존 application.properties에서 추가한 설정을 사용하기위해 다음과 같이 추가한다

```java
spring.profiles.include=oauth
```

이런 개인정보같은 경우, public한 깃 허브에 올라갈 경우 해킹당할 위험이 크므로, application-oauth.properties를 .gitIgnore에 추가한다.


## 1.2 로그인 기능 구현 

프로젝트/user/domain

유저의 권한설정을 지정하는 부분을 enum으로 해당 부분을 구현한다.
스프링 시큐리티에서는 권한 코드에 항상 ROLE_이 앞에 있어야 한다.     
그러므로 코드별 키 값을 ```ROLE_GEUST```, ```ROLE_USER```등으로 지정하도록 한다.

Role

```java
@Getter
@RequiredArgsConstructor
public enum Role {
    GUEST("ROLE_GUEST", "손님"),
    USER("ROLE_USER", "일반 사용자");

    private final String key;
    private final String title;
}

```
프로젝트/user/domain
구글의 로그인 인증정보의 사용자 정보를 담당할 도메인인 ```User```클래스를 생성한다.


user 클래스


```java
@Getter
@NoArgsConstructor
@Entity
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column
    private String picture;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Builder
    public User(String name, String email, String picture, Role role){
        this.name = name;
        this.email = email;
        this.picture = picture;
        this.role = role;
    }

    public User update(String name, String picture){
        this.name = name;
        this.picture = picture;

        return this;
    }

    public String getRoleKey(){
        return this.role.getKey();
    }
}

```


```@Enumerated(EnumType.STRING)```

* JPA로 데이터베이스로 저장할 때 Enum 값을 어떤 형태로 저장할지를 결정
* 기본적으로 int로 된 숫자가 저장 되므로 문자열 (EnumType.STRING)로 저장될 수 있도록 선언한다.

이후 유저를 DB와 연결할 수 있는 Repository를 생성한다.

```java
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}
```

  
```Optional```

* Null값을 처리하기 위해, 안전한 Optional 사용
  
```findByEmail(String email)```
    
* 소셜 로그인으로 반환되는 값중 email을 통해 이미 생성된 사용자인지 처음 가입하는 사용자인지 판단하기 위한 메소드
* PK 를 사용한 것이 아니라 Unique 를 사용한 것을 알 수 있다.   
  
    
```java
      @Override
    protected void configure(HttpSecurity http) throws Exception{
        http
                .csrf().disable().headers().frameOptions().disable().and()
                .authorizeRequests()
                .antMatchers("/","/css/**","/images/**","/js/**","/h2-console/**", "/profile").permitAll()
                .antMatchers("/api/v1/**").hasRole(Role.USER.name())
                .anyRequest().authenticated()
                .and()
                .logout()
                .logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(customOAuth2UserService);

    }
}
```
  
```@EnableWebSecurity ```  

* Spring Security 설정들을 활성화
  
```http.csrf().disable().headers().frameOptions().disable() ```
  
* 로그인 인증에 필요한 csrf 토큰을 비활성화 함.
* 비활성화 하지않으면, 설정이 매우 복잡함.
  
```.authorizeRequests()```
* URL 별 권한 관리를 설정하는 옵션의 시작점으로 andMatchers() 옵션을 사용할 수 있도록 
  
```.andMatchers("url", "ur2")```    

* 권한 관리 대상을 지정하는 옵션   
* URL, HTTP 메소드별로 관리가 가능.   
* "/" 등 지정된 URL들은 permitAll() 옵션을 통해 전체 열람 권한을 주고    
* "api/v1/**" 주소를 가진 API는 USER 권한을 가진 사람만 진행 할 수 있도록 함.
  
```.anyRequest()```

* 설정된 값 이외 나머지 URL 관련 인증된 사용자(로그인 한 사용자) 관련 옵션
.authenticated()을 추가하여 나머지 URL 들은 모두 인증된 사용자들에게만 허용함.   

```.logout().logoutSuccessUrl("/")```     

* 로그아웃 기능에 대한 여러 설정의 진입점으로 로그아웃 성공시 / 주소로 이동

 
```.oauth2Login()```  

* OAuth2 로그인 기능에 대한 여러 설정의 진입점
  
```.userInfoEndpoint()```   

* OAuth2 로그인 성공 이후 사용자 정보를 가져올 때의 설정을 가지고있는 엔드포인트
  
```.userService()```    

* 소셜 로그인 성공시 후속 조치를 진행할 UserService 인터페이스의 구현체를 등록함  
* 리소스 서버에서 사용자 정보를 가져온 상태에서 추가로 진행하고자 하는 기능을 명시할 수 있음
  
이후 다음과 같은 인증관련 서비스를 제작한다.
customOAuth2UserService
해당 클래스는 구글 로그인 이후 가져온 사용자 정보들을 기반으로 가입 및 정보수정, 세션 저장등의 기능을 지원한다.
  
```java
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);
  
        //현재 로그인 진행중인 서비스를 구분하는 코드, 구글로 로그인, 네이버로 로그인하는지 구분하기 위해 사용되는 코드임
        String registrationId = userRequest.getClientRegistration().getRegistrationId();
  
        //OAuth2 로그인 진행시 키가 되는 필드값 구글의 경우 기본적으로 코드를 지원하지만 ("sub") , 네이버 카카오등은 지원하지않아
        //이후 네이버 로그인과 구글 로그인을 동시 지원할 때 사용
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
                .getUserInfoEndpoint().getUserNameAttributeName();
        
        //OAuth2UserService 로 만들어진 OAuth2User 객체를 참조하는 변수        
        //OAuthAttributes attributes는 OAuth2UserService 를 통해 가져온 OAuth2User 클래스의 attribute를 담을 클래스를 사용
        //User 클래스에 직렬화 코드를 넣게되는 경우 이후 다른 Entity와 관계가 형성될 경우 문제 발생
        //유지보수 측면에서 **OAuthAttributes** 클래스 생성하여 관리 할 수 있도록 함.
        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);
        //세션에 사용자 정보를 저장하기 위한 DTO 클래스
        httpSession.setAttribute("user", new SessionUser(user));

        return new DefaultOAuth2User(Collections.singleton(new SimpleGrantedAuthority(user.getRoleKey())),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes){
        User user = userRepository.findByEmail(attributes.getEmail())
                .map(entity -> entity.update(attributes.getName(), attributes.getPicture()))
                .orElse(attributes.toEntity());

        return userRepository.save(user);
    }
}
```
    
    
위에서 서술한 OAuthAttributes 클래스를 아래와 같이 작성한다.
    
```java
    
@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String name;
    private String email;
    private String picture;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String email, String picture){
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.email = email;
        this.picture = picture;
    }
    // OAuth2User에서 반환하는 사용자 정보는 Map 자료구조 형태이기에 값 하나하나를 변환 해야 함
    public static OAuthAttributes of(String registrationId, String userNameAttributeName, Map<String, Object> attributes){
        System.out.println("registration="+registrationId);
        return ofGoogle(userNameAttributeName, attributes);
    }
       
    private static OAuthAttributes ofGoogle(String userNameAttributeName, Map<String, Object> attributes){
        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .email((String) attributes.get("email"))
                .picture((String) attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .build();
    }

    public User toEntity(){
        return User.builder()
                .name(name)
                .email(email)
                .picture(picture)
                .role(Role.GUEST)
                .build();
    }
}

```
    
SessionUser
    
```java
package com.jojoldu.book.springboot.config.auth.dto;

import com.jojoldu.book.springboot.domain.user.User;
import lombok.Getter;

import java.io.Serializable;

@Getter
public class SessionUser implements Serializable {

    private String name;
    private String email;
    private String picture;

    public SessionUser(User user){
        this.name = user.getName();
        this.email = user.getEmail();
        this.picture = user.getPicture();
    }
}

```    
    
## 1.3 로그인 화면 구현

```index.mustache```에 로그인 버튼과 사용자 이름을 보여줄 수 있도록 화면을 구현한다.

index.mustache    
```mustache  
{{>layout/header}}
    <h1>스프링 부트로 시작하는 웹 서비스 ver.2</h1>
    <div class="col-md-12">
        <!-- 로그인 기능 영역 -->
        <div class="row">
            <div class="col-md-6">
                <a href="/posts/save" role="button" class="btn btn-primary">글 등록</a>
                {{#userName}}
                    Looged in as : <span id="user">{{userName}}</span>
                    <a href="/logout" class="btn btn-info active" role="button">Logout</a>
                {{/userName}}
                {{^userName}}
                    <a href="/oauth2/authorization/google" class="btn btn-success active" role="button">
                        Google Login
                    </a>
                {{/userName}}
            </div>
        </div>
    </div>
    <br>
    <!-- 목록 출력 영역 -->
    <table class="table table-horizontal table-bordered">
        <thead class="thead-string">
            <tr>
                <th>게시글번호</th>
                <th>제목</th>
                <th>작성자</th>
                <th>최종수정일</th>
            </tr>
        </thead>
        <tbody id="tbody">
            {{#posts}}
                <tr>
                    <td>{{id}}</td>
                    <td><a href="/posts/update/{{id}}">{{title}}</a></td>
                    <td>{{author}}</td>
                    <td>{{modifiedDate}}</td>
                </tr>
            {{/posts}}
        </tbody>
    </table>
{{>layout/footer}}

```  


```{{#userName}}```
* 머스테치는 다른 언어와 같은 if문을 제공하지않기 때문에 최종값을 넘겨 갯수만큼 작동되도록 설정

```a href = "/oauth2/authorization/google"```     

* 스프링 시큐리티에서 기본적으로 제공하는 로그인 URL   
* 로그아웃 URL과 마찬가지로 개발자가 별도의 컨트롤러를 생성할 필요가 없음
  
userName을 ```index.mustache```에서도 사용할 수 있도록 model에 저장하는 내용을 컨트롤러에 추가한다.

IndexController

```java
@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;
    private final HttpSession httpSession;
    
    @GetMapping("/")
    public String index(Model model){
        
        model.addAttribute("posts", postsService.findAllDesc());
        Session User user= (SessionUser) httpSession.getAttribute("user");
        if(user != null){
            model.addAttribute("userName", user.getName());
        }
        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave(){
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model){

        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post",dto);
        return "posts-update";
    }
}

```

```(SessionUser) httpSession.getAttribute("user");```
    
* 앞서 작성된 CustomOAuth2UserService에서 로그인 성공 시 세션에 SessionUser를 지정하도록 구성         
* 로그인 성공시 httpSession.getAttribute("user")에서 데이터를 가져올 수 있음

이후 로그인을 테스트해본다.(구글)

## 2.1 어노테이션 기반으로 개선

같은 코드를 계속해서 복사 & 붙여넣기로 만든다면 이후에 수정이 필요할 때 모든 부분을 일일이 수정해줘야 하는 단점이 있다. 
이렇게 될 경우 유지보수하기 어렵기 때문에, 아래와 같은 코드를 어노테이션을 새로 만들어 적용한다.

```java
Session user = (SessionUser) httpSession.getAttribute("user");
```

```config.auth``` 패키지에 ```@LoginUser``` 어노테이션을 생성한다.

```java
@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface LoginUser {
}

```

@Retention(RetentionPolicy.RUNTIME)  

* 어노테이션의 범위
* 런타임 시점까지 어노테이션이 동작되도록 설정

이후 동일한 위치에 ```HandlerMethodArgumentResolver``` 인터페이스를 구현한 클래스```LoginUserArgumentResolver``` 를 생성한다.
```HandlerMethodArgumentResolver```는 구현체가 지정한 값으로 해당 메소드의 파라미터로 넘길 수 있다.

```java
@RequiredArgsConstructor
@Component
public class LoginUserArgumentResolver implements HandlerMethodArgumentResolver {

    private final HttpSession httpSession;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        boolean isLoginUserAnnotation = parameter.getParameterAnnotation(LoginUser.class) != null;
        boolean isUserClass = SessionUser.class.equals(parameter.getParameterType());
        return isLoginUserAnnotation && isUserClass;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        return httpSession.getAttribute("user");
    }
}

```


```java
@Override
public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
   NativeWebRequest webRequest, WebDataBinderFactory binderFactroy) throws Exception {예외처리}
```
* 파라미터에 전달할 객체를 생성함.
* 해당 소스에서는 파라미터에 @LoginUser 어노테이션이 붙어있고, 파라미터 클래스 타입이 SessionUser.class인 경우 true를 반환

이렇게 생성된 ```LoginUserArgumentResolver```를 스프링에서 인식될 수 있도록 config 패키지에 ```WebConfig``` 클래스를 생성하여
```WebMvcConfiguration```을 추가한다.

```java

@RequiredArgsConstructor
@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final LoginUserArgumentResolver loginUserArgumentResolver;

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers){
        argumentResolvers.add(loginUserArgumentResolver);
    }
}
```

설정이 완료 되었으므로```IndexController``` 의 코드에서 반복되는 부분들을 모두 ```@LoginUser```로 개선한다.

```java
@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;

    @GetMapping("/")
    public String index(Model model, @LoginUser SessionUser user){
        model.addAttribute("posts", postsService.findAllDesc());

        if(user != null){
            model.addAttribute("userName", user.getName());
        }
        return "index";
    }

    @GetMapping("/posts/save")
    public String postsSave(){
        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model){

        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post",dto);
        return "posts-update";
    }
}
```

```@LoginUser SessionUser user```  
  
* 기존에(User) httpSession.getAttribute("user")로 가져오던 세션 정보 값이 개선하여     
* 이제는 어느 컨트롤러든지 @LoginUser만 사용함녀 세션 정보를 가져올 수 있다.


## 2.2 세션 저장소로 데이터베이스 사용하기


