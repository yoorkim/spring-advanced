package org.example.expert.common.logging;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

@Aspect
@Component
@RequiredArgsConstructor
public class AdminLoggingAspect {

    private final Logger log = LoggerFactory.getLogger(getClass());
    private final ObjectMapper objectMapper;

    @Around("execution(* org.example.expert.domain.comment.controller.CommentAdminController.deleteComment(..)) || " +
            "execution(* org.example.expert.domain.user.controller.UserAdminController.changeUserRole(..))")
    public void doLog(ProceedingJoinPoint joinPoint) throws Throwable {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();

        // 요청한 사용자의 ID 추출
        Long userId = (Long) request.getAttribute("userId");

        Object[] args = joinPoint.getArgs();
        String requestBody = "N/A";  // 기본값 설정

        if (args.length > 1) {
            requestBody = objectMapper.writeValueAsString(args[1]);
        }

        log.info("[Admin API Request] UserId: {}, Time: {}, URL: {}, RequestBody: {}",
                userId, LocalDateTime.now(), request.getRequestURL(), requestBody);

        // 메서드 실행 및 응답
        try {
            Object result = joinPoint.proceed();
            String responseBody = objectMapper.writeValueAsString(result);

            log.info("[Admin API Response] UserId: {}, Time: {}, URL: {}, ResponseBody: {}",
                    userId, LocalDateTime.now(), request.getRequestURL(), responseBody);
        } catch (Throwable ex) {
            log.error("[Admin API Error] UserId: {}, Time: {}, URL: {}, Error: {}",
                    userId, LocalDateTime.now(), request.getRequestURL(), ex.getMessage());
            throw ex;
        }
    }
}
