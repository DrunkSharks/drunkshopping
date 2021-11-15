package com.drunk.oauth.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@CrossOrigin
@RequestMapping("/drunk")
public class LoginRedirect {

    /***
     * 跳转到登录页面
     * @return
     */
    @GetMapping(value = "/login")
    public String login(@RequestParam(value = "from",required = false)String from, Model model){
        model.addAttribute("from",from);
        return "login";
    }
}
