package com.fyang.springmvc.controller;

import com.fyang.springmvc.annotation.Controller;
import com.fyang.springmvc.annotation.Qualifier;
import com.fyang.springmvc.annotation.RequestMapping;
import com.fyang.springmvc.service.UserService;

@Controller( "userController" )
@RequestMapping( "/user" )
public class UserController {

    @Qualifier( "userServiceImpl" )
    private UserService userService;

    @RequestMapping( "/insert" )
    public void insert(){
        userService.insert();
    }
}
