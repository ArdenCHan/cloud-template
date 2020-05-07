package com.xxx.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @Author ArdenChan
 * @Date 2020/5/7
 */
@Service
@FeignClient(name = "example-service")
public interface ExampleService {
    /**
     * 例子
     * @param hello helloWorld
     * @return 测试
     */
    @GetMapping(value = "/example/hello/{hello}")
    String example(@PathVariable("hello") String hello);
}
