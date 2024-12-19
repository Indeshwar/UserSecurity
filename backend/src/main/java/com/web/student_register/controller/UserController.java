package com.web.student_register.controller;

import com.web.student_register.Dto.UserRequest;
import com.web.student_register.Dto.UserResponse;
import com.web.student_register.Service.CustomUserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Slf4j
@RestController
@RequestMapping("api/v1")
public class UserController {

    @Autowired
    private CustomUserService userService;

    @GetMapping("/hello")
    public String hello(){
        return "Hello World";
    }

    @GetMapping("/student/hi")
    public String sayHi(){
        return "Hello I am authenticated user";
    }
    
    @PostMapping("/saveUser")
    public ResponseEntity<UserResponse> registerUser(@Valid @RequestBody UserRequest request){
        UserResponse response = userService.registerUser(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/user/login")
    public ResponseEntity<UserResponse> userLogIn(@RequestBody UserRequest request){
        UserResponse response =  userService.userLogIn(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


}
