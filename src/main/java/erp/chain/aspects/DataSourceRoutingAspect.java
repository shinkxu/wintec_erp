package erp.chain.aspects;

import erp.chain.common.Constants;
import erp.chain.configurations.DataSourceContextHolder;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Method;

@Aspect
@Order(value = 1)
public class DataSourceRoutingAspect {
    @Before(value = "execution(public * erp.chain.service..*(..))")
    public void setDataSource(JoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();

        boolean setComplete = setDataSourceType(method.getAnnotation(Transactional.class));
        if (setComplete) {
            return;
        }
        setComplete = setDataSourceType(joinPoint.getTarget().getClass().getAnnotation(Transactional.class));
        if (setComplete) {
            return;
        }
        DataSourceContextHolder.setDataSourceType(Constants.PRIMARY_DATA_SOURCE);
    }

    private boolean setDataSourceType(Transactional transactional) {
        if (transactional == null) {
            return false;
        }
        if (transactional.readOnly()) {
            DataSourceContextHolder.setDataSourceType(Constants.SECONDARY_DATA_SOURCE);
        } else {
            DataSourceContextHolder.setDataSourceType(Constants.PRIMARY_DATA_SOURCE);
        }
        return true;
    }
}
