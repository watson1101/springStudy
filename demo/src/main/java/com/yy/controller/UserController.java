package com.yy.controller;

import java.util.List;

import com.yy.domain.User;
import com.yy.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/user")
public class UserController {
	
	@Autowired
	private UserMapper userMapper1;

	@RequestMapping("/getUsers")
	public List<User> getUsers() {
		List<User> users=userMapper1.getAll();
		return users;
	}
	
    @RequestMapping("/getUser")
    public User getUser(Long id) {
    	User user=userMapper1.getOne(id);
        return user;
    }
    
    @RequestMapping("/add")
    public void save(User user) {
        userMapper1.insert(user);
    }
    
    @RequestMapping(value="update")
    public void update(User user) {
        userMapper1.update(user);
    }
    
    @RequestMapping(value="/delete/{id}")
    public void delete(@PathVariable("id") Long id) {
        userMapper1.delete(id);
    }
    
    
}