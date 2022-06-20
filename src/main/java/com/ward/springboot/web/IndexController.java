package com.ward.springboot.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

//Page 132

@Controller
public class IndexController {
    @GetMapping("/")
    public String index() {

        return "index";
    }


}
