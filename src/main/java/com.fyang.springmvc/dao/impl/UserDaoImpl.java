package com.fyang.springmvc.dao.impl;

import com.fyang.springmvc.annotation.Repository;
import com.fyang.springmvc.dao.UserDao;

@Repository( "userDaoImpl" )
public class UserDaoImpl implements UserDao {
    @Override
    public void insert() {
        System.out.println("UserDaoImpl  println");
    }
}
