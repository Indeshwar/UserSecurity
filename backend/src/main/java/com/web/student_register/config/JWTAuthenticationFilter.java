package com.web.student_register.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class JWTAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsService userDetailsService;
    @Autowired
    private JWTTokenHelper jwtTokenHelper;


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = jwtTokenHelper.getToken(request);

        if(token != null){
            String userName = jwtTokenHelper.getUserNameFromToken(token);
            if(userName != null){
                System.out.println("User name is " + userName);
                UserDetails userDetails = userDetailsService.loadUserByUsername(userName);
                if(jwtTokenHelper.validateToken(token, userDetails)){
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails.getUsername(),
                            null, userDetails.getAuthorities());
                    authentication.setDetails( new WebAuthenticationDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    System.out.println("Request authenticated");
                }

            }
        }

        //Pass the request and response to the next filter or the servlet for processing the request
        filterChain.doFilter(request, response);
    }



}
