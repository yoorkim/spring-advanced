package org.example.expert.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.expert.domain.user.enums.UserRole;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.time.LocalDateTime;

@Component
public class AdminInterceptor implements HandlerInterceptor {

    private final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String userRole = (String)request.getAttribute("userRole");
        if (!UserRole.valueOf(userRole).equals(UserRole.ADMIN)) {
            throw new Exception("ADMIN 권한이 없습니다.");
        }

        log.info("요청 시각: {}, URL: {}", LocalDateTime.now(), request.getRequestURL());

        return true;
    }
}
