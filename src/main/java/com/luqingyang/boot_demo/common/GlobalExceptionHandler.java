package com.luqingyang.boot_demo.common;

import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 用于捕获和处理乐观锁冲突以及其他异常
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理乐观锁冲突异常
     * 当多个用户同时编辑同一数据时，版本号不匹配会抛出此异常
     */
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public Result<?> handleOptimisticLockingFailure(OptimisticLockingFailureException e) {
        return Result.versionConflict();
    }

    /**
     * 处理通用异常
     */
    @ExceptionHandler(Exception.class)
    public Result<?> handleException(Exception e) {
        return Result.error("系统错误：" + e.getMessage());
    }

    /**
     * 处理自定义的资源不存在异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<?> handleRuntimeException(RuntimeException e) {
        if (e.getMessage().contains("不存在")) {
            return Result.notFound(e.getMessage());
        }
        return Result.error(e.getMessage());
    }
}
