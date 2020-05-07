package com.xxx.controller;

import com.alibaba.csp.sentinel.annotation.SentinelResource;
import com.xxx.service.ExampleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Author ArdenChan
 * @Date 2020/5/7
 */
@RestController
@RequestMapping(value = "/api")
public class ExampleController {

    @Resource
    private ExampleService exampleService;

    @SentinelResource(value = "hello")
    @GetMapping(value = "/hello/{hello}")
    public String test(@PathVariable("hello") String hello){
        return exampleService.example(hello);
    }
}
