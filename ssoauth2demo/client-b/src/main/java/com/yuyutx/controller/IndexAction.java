package com.yuyutx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * @author jun
 * @date 2020-06-16 06:08
 * @description
 */
@Controller
public class IndexAction {

    @GetMapping(value = "")
    public String index() {
        System.out.println("进入ClientB首页");
        return "index.html";
    }

    @GetMapping(value = "securedPage")
    public String home() {
        System.out.println("进入ClientB securedPage");
        return "securedPage.html";
    }
}

