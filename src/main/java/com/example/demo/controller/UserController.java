package com.example.demo.controller;

import com.example.demo.entity.Response;
import com.example.demo.entity.User;
import com.example.demo.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author ly
 * @since 2021/5/13
 */
@CrossOrigin//跨域
@RestController
@RequestMapping("/user")
public class UserController {
    @Autowired
    private IUserService iUserService;

    @CrossOrigin
    //登录
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public Response login(@RequestBody Map<String, String> person) {
        String userName = person.get("userName");
        String password = person.get("password");
        System.out.println(userName);
        System.out.println(password);

        if (userName != null && password != null) {
            User user = new User();
            user.setName(userName);
            user.setPassword(password);
            boolean isLoginSuccess = iUserService.login(user);

            if (isLoginSuccess) {
                return new Response("登录成功", 1, true);
            } else {
                return new Response("登录失败：用户名或密码错误", -1, false);
            }
        } else {
            return new Response("登录失败：用户名密码不能为空", -1, false);
        }
    }


//    //注册：
//    @GetMapping("/register")
//    public String register() {
//        return "Register";
//    }
//
//    @GetMapping("/end")
//    public String end(HttpServletRequest req, HttpServletResponse resp) {
//
//        String username = req.getParameter("username");
//        String password = req.getParameter("password");
//
//        User user = new User();
//        user.setName(username);
//        user.setPassword(password);
//        boolean flag = iUserService.register(user);
//        if (flag == true) {
//            return "End1";
//        } else {
//            return "End2";
//        }
//    }

}
