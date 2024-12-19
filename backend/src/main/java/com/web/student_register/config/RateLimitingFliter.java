package com.web.student_register.config;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Bucket4j;
import io.github.bucket4j.Refill;
import org.apache.logging.log4j.ThreadContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitingFliter extends OncePerRequestFilter {
    @Autowired
    private RateLimiterConfig config;
    @Autowired
    private JWTTokenHelper jwtTokenHelper;
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        //Get the IP address of client
        String clientIp = request.getRemoteAddr();
        //Get the URL of client
        String path = request.getRequestURI();

        ThreadContext.put("IP", clientIp);
        ThreadContext.put("Path", path);
        String token = jwtTokenHelper.getToken(request);
        String userName = jwtTokenHelper.getUserNameFromToken(token);


        // Apply different rate limits based on user
        Bucket bucket = buckets.computeIfAbsent(path + userName, key -> newBucket(path));
        long availableTokens = bucket.getAvailableTokens();

        try{
            if (bucket.tryConsume(1)) {
                response.setHeader("X-Rate-Limit", "10");
                response.setHeader("X-Rate-Limit-Remaining", String.valueOf(availableTokens));
                //Pass the request and response to the next filter
                filterChain.doFilter(request, response);
            } else {
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                response.getWriter().write("Too many requests from your IP. Please try again later.");
            }
        }finally {
            ThreadContext.clearAll();
        }

    }

    private Bucket newBucket(String path) {
        // Define rate limits based on the path
        List<Discriptors> discriptors = config.getDiscriptors();
        int capacity;
        long timeFrame;
        for(Discriptors d: discriptors){
            capacity = d.getRateLimit().getRequestsPerUnit();
            timeFrame = d.getRateLimit().getTimeFrame();

            if(path.startsWith(d.getValue())){
                return Bucket4j.builder()
                        .addLimit(Bandwidth.classic(capacity, Refill.greedy(capacity, Duration.ofMinutes(timeFrame)))) // 10 requests per minute
                        .build();
            }
        }

        return null;

    }

}
