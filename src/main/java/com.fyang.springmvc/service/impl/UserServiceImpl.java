package com.fyang.springmvc.service.impl;

import com.fyang.springmvc.annotation.Qualifier;
import com.fyang.springmvc.annotation.Service;
import com.fyang.springmvc.dao.UserDao;
import com.fyang.springmvc.service.UserService;

@Service( "userServiceImpl" )
public class UserServiceImpl implements UserService {

    @Qualifier( "userDaoImpl" )
    private UserDao userDao;
    @Override
    public void insert() {
        System.out.println("UserServiceImpl  start");
        userDao.insert();
        System.out.println("UserServiceImpl end");

    }
}
