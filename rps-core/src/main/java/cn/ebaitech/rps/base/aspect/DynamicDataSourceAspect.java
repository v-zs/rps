package cn.ebaitech.rps.base.aspect;

import cn.ebaitech.rps.base.expend.DBContextHolder;
import cn.ebaitech.rps.base.config.TransactionConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;

@Aspect
@Component
public class DynamicDataSourceAspect {

    @Around("execution(* cn.ebaitech.rps.core.service.*.*(..))")
    public void setDynamicDataSource(ProceedingJoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        if (isReadOnly(methodName)) {
            DBContextHolder.setDBType(DBContextHolder.DBType.slave);
        } else {
            DBContextHolder.setDBType(DBContextHolder.DBType.master);
        }
        try {
            joinPoint.proceed();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    public boolean isReadOnly(String methodName) {
        for (String methodPattern : TransactionConfig.readonlyPatterns) {
            if (PatternMatchUtils.simpleMatch(methodPattern, methodName)) {
                return true;
            }
        }
        return false;
    }

}
