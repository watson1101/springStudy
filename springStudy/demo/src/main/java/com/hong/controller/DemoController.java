package com.hong.controller;

import com.hong.tools.Common;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/demo")
public class DemoController {

    @GetMapping("/check")
    public String check(){
        return Common.check();
    }
}
