package com.learnings.forum.controller;

import com.learnings.forum.common.AppResult;
import com.learnings.forum.exception.ApplicationException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.*;

/**
 * Created with IntelliJ IDEA.
 * Description: 测试类
 * User: 12569
 * Date: 2023-10-04
 * Time: 21:07
 */

@Api(tags = "测试类的相关接口")
@RestController
@RequestMapping("/test")
public class TestController {

    @ApiOperation("测试接口1，显示hello, Spring Boot")
    @GetMapping("/hello")
    public String hello(){
        return "hello, Spring Boot!";
    }

    @ApiOperation("测试接口2，显示抛出的异常信息")
    @GetMapping("/exception")
    public AppResult testException() throws Exception {
        throw new Exception("这是一个异常");
    }

    @ApiOperation("测试接口3，显示自定义的异常信息")
    @GetMapping("/app_exception")
    public AppResult testApplicationException() throws Exception {
        throw new ApplicationException("这是一个application异常");
    }

    @ApiOperation("测试接口4，按传入的姓名显示你好信息")
    @PostMapping("/helloByName")
    public AppResult helloByName (@ApiParam(value = "姓名") @RequestParam("name") String name) {
        return AppResult.success("hello : " + name);
    }
}
