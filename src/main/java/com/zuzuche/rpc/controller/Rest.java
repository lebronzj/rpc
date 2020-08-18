package com.zuzuche.rpc.controller;

import com.zuzuche.rpc.service.TestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhouj
 * @since 2020-08-04
 */
@RestController
public class Rest {

    @Autowired
    private TestService testService;

    @RequestMapping("/test")
    public String test() {
        return testService.say();
    }
}
