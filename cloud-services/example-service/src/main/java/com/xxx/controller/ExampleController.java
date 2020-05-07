package com.xxx.controller;

import com.xxx.service.impl.ExampleServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author ArdenChan
 * @Date 2020/5/7
 */
@RestController
@RequestMapping(value = "/example")
public class ExampleController {

    @Autowired
    private ExampleServiceImpl exampleService;

    @GetMapping(value = "/hello/{hello}")
    public String testExample(@PathVariable("hello") String hello){
        return exampleService.example(hello);
    }

}
