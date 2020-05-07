package com.xxx.service.impl;

import com.xxx.service.ExampleService;
import org.springframework.stereotype.Service;

/**
 * @Author ArdenChan
 * @Date 2020/5/7
 */
@Service
public class ExampleServiceImpl implements ExampleService {
    @Override
    public String example(String hello) {
        return hello;
    }
}
