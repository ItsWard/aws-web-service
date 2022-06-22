package com.ward.springboot.web;

import com.ward.springboot.service.PostsService;
import com.ward.springboot.web.dto.PostsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

//Page 132

@RequiredArgsConstructor
@Controller
public class IndexController {

    private final PostsService postsService;
    @GetMapping("/")
    public String index(Model model) {
        //Page 150
        //index.mustache tbody id = tbody ~ {{#posts}} 부분에
        //postService.findAllDesc -> PostsRepository Query 전체조회 데이터
        //Model and View를 이용해 데이터를 넘겨 View에서 조회 가능하게함
        model.addAttribute("posts", postsService.findAllDesc());
        return "index";
    }

    @GetMapping("/posts/save")
    public String postSave() {

        return "posts-save";
    }

    @GetMapping("/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model) {
        //Page 155
        //index.mustache에서 <td><a href="/posts/update/{{id}}">{{title}}</a></td>
        //부분을 클릭하면 @PathVariable로 인해서 id값에 저장이되고,
        //아이디에 해당하는 데이터를 postService에서 찾아 dto에 저장한 후,
        //"posts-update.mustache"에 "post"이름으로 dto를 전달하여 update가 가능한 페이지를 볼 수 있게함.
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post", dto);

        return "posts-update";
    }





}
