package com.nowcoder.mycommunity.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * @author XiaoXin
 * @Description
 * @date 2022-07-12-16:56
 */
//@Component
//@Aspect
@Order(3)
public class AlphaAspect {

    @Pointcut("execution(* com.nowcoder.mycommunity.service.*.*(..))")
    public void pointCut() {

    }

    final String pointCut = "execution(* com.nowcoder.mycommunity.service.*.*(..))";

    @Before("pointCut()")
    public void before() {
        System.out.println("before");
    }

    @After("pointCut()")
    public void after() {
        System.out.println("after");
    }

    @AfterReturning("pointCut()")
    public void afterReturning() {
        System.out.println("afterReturning");
    }

    @AfterThrowing("pointCut()")
    public void afterThrowing() {
        System.out.println("afterThrowing");
    }

    @Around("pointCut()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        System.out.println("beforeAround");
        Object obj = joinPoint.proceed();
        System.out.println("afterAround");
        return obj;
    }
}
