package com.foxx.controller;


import com.foxx.bean.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;

/**
 * 作者：wangsy
 * 日期：2016/6/16 15:31
 * 描述：
 */
@Controller
public class HelloController {

    @RequestMapping("/hello")
    @ResponseBody
    public String hello() {
        return "hello，Spring boot";
    }

    @RequestMapping("/user/{id}")
    @ResponseBody
    public User getUser(@PathVariable int id) {
        User user = new User();
        if (id == 1) {
            user.setUserId("00001");
            user.setUserName("james");
            user.setBirthday(new Date());
        }
        return user;
    }

}
