package com.web.student_register.Service;

import com.web.student_register.Dto.UserRequest;
import com.web.student_register.Dto.UserResponse;
import com.web.student_register.config.JWTTokenHelper;
import com.web.student_register.entity.*;
import com.web.student_register.exception.InvalidTokenException;
import com.web.student_register.exception.UserNotFoundException;
import com.web.student_register.repository.RoleRePo;
import com.web.student_register.repository.UserRepo;
import lombok.extern.slf4j.Slf4j;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;



@Slf4j
@Service
public class CustomUserService implements UserDetailsService {
    private UserRepo userRepo;
    private DaoAuthenticationProvider daoAuthenticationProvider;
    private JWTTokenHelper jwtTokenHelper;
    private PasswordEncoder passwordEncoder;
    private RoleRePo roleRepo;
    private static final int MAX_REQUEST = 3;
    // TIME_FRAME is 1 hour
    private static final long TIME_FRAME = 60 * 60 * 1000;

    private static final Logger logger = LogManager.getLogger();

    @Autowired
    private EmailService emailService;

    @Lazy
    @Autowired
    public CustomUserService(UserRepo userRepo,DaoAuthenticationProvider daoAuthenticationProvider, JWTTokenHelper jwtTokenHelper, PasswordEncoder passwordEncoder, RoleRePo roleRepo) {
        this.userRepo = userRepo;
        this.daoAuthenticationProvider = daoAuthenticationProvider;
        this.jwtTokenHelper = jwtTokenHelper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepo = roleRepo;

    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepo.getByUserName(username);

        if(user == null){
            throw new UsernameNotFoundException(username + " does not exist!");
        }
        return new UserPrincipal(user);
    }

    public UserResponse registerUser(UserRequest request) {
        //find the userName already exist or not
        User exitingUser = userRepo.getByUserName(request.getUserName());
        UserResponse response = new UserResponse();
        ThreadContext.put("userName", request.getUserName());
        if(exitingUser != null){
             logger.error("User already exist");
             throw new  UserNotFoundException("User is already exist");
        }
        User newUser = new User();
        try{
            newUser.setUserName(request.getUserName());
            newUser.setPassword(passwordEncoder.encode(request.getPassword()));
            Role role = roleRepo.getByRoleName(request.getRoleName());
            newUser.addRole(role);
            //save the new user
            userRepo.save(newUser);
            //set the success message
            response.setMessage("User is successfully created");
        }catch(Exception e){
            logger.error("Error register user", e);
        }finally{
            ThreadContext.clearAll();
        }


        return response;
    }

    public UserResponse userLogIn(UserRequest request){
        String token = null;
        UserResponse response = new UserResponse();;
        ResponseEntity<UserResponse> responseEntity = null;
        ThreadContext.put("userName", request.getUserName());
        try{
            Authentication authentication = daoAuthenticationProvider.authenticate(new UsernamePasswordAuthenticationToken(request.getUserName(), request.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            token = jwtTokenHelper.generateToken(userDetails.getUsername(), 0);
            response.setToken(token);
            response.setMessage("Successfully log in");
            responseEntity = new ResponseEntity<>(response, HttpStatus.OK);

        }catch(Exception e){
            logger.error("Invalid credentials {}", e.getMessage());
            throw new UserNotFoundException("Ivalid credentials");

        }finally{
            ThreadContext.clearAll();
        }

        return response;
    }

    public String forgetPassword(String email, String token){
        String newToken = null;
        //find the email
        //create onetime used token
        //create reset password link
        //send the link to the user
        try {

            if(token != null){
                String userName = jwtTokenHelper.getUserNameFromToken(token);
                UserDetails userDetails = loadUserByUsername(userName);

                //Validate the token
                if(!jwtTokenHelper.validateToken(token, userDetails)){
                    return "Token expired. Try again";
                }

                int requestCount = jwtTokenHelper.getRequestCount(token);
                long timeStamp = jwtTokenHelper.getTimeStamp(token);

                //Check if user exceed the limit
                if(requestCount > MAX_REQUEST &&  System.currentTimeMillis() - timeStamp < TIME_FRAME){
                    return "To many password reset Request. Try again Later";
                }


                //increment the request count and create new Token
                newToken = jwtTokenHelper.generateToken(userName, requestCount + 1);

            }else{
                //create new token if a user first time request for password reset
                UserDetails user = loadUserByUsername(email);
                newToken = jwtTokenHelper.generateToken(user.getUsername(), 1);
            }

            //Create Url
            String url = createURL(newToken);
            //send reset link to the user
            emailService.sendEmail("indeshc@gmail.com", "Demo URL", "Click the reset password Link " + url);
        }catch(Exception e){
            logger.error("Error user not found", e);
            throw new InvalidTokenException("Token is invalid", e);
        }
        return newToken;
    }

    public String createURL(String token){
        return "https://www.chicagolandtharusociety.org/" + token;
    }

    public void resetPassword(String token, String newPassword){
        String userName = jwtTokenHelper.getUserNameFromToken(token);
        if(userName != null){
            UserDetails userDetails = loadUserByUsername(userName);
            if(jwtTokenHelper.validateToken(token, userDetails)){
                User user = userRepo.getByUserName(userName);
                user.setPassword(passwordEncoder.encode(newPassword));
                userRepo.save(user);
            }
        }

    }


}
