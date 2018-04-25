package com.sprreact.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Value("#{systemProperties['user.dir']}")
    private String dir;

    @GetMapping("/")
    public String index() {
        return "index";
    }
}
