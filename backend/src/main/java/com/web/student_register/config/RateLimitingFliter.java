package com.web.student_register.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.springframework.http.HttpStatus;
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

@Component
public class RateLimitingFliter extends OncePerRequestFilter {
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        //Get the IP address of client
        String clientIp = request.getRemoteAddr();
        //Get the URL of client
        String path = request.getRequestURI();

        // Apply different rate limits based on the URL path
        Bucket bucket = buckets.computeIfAbsent(clientIp + path, key -> newBucket(path));

        if (bucket.tryConsume(1)) {
            //Pass the request and response to the next filter
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Too many requests from your IP. Please try again later.");
        }
    }

    private Bucket newBucket(String path) {
        // Define rate limits based on the path
        if (path.startsWith("/api/v1/student")) {
            return Bucket4j.builder()
                    .addLimit(Bandwidth.classic(3, Refill.greedy(3, Duration.ofMinutes(1)))) // 3 requests per minute
                    .build();
        } else {
            return Bucket4j.builder()
                    .addLimit(Bandwidth.classic(5, Refill.greedy(5, Duration.ofMinutes(1)))) // 5 requests per minute
                    .build();
        }
    }
}
