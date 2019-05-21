package erp.chain.aspects;

import com.saas.common.ResultJSON;
import erp.chain.annotations.ApiRestAction;
import erp.chain.annotations.ResultJSONAction;
import erp.chain.exceptions.CustomException;
import erp.chain.model.online.BasicModel;
import erp.chain.utils.ApplicationHandler;
import erp.chain.utils.BeanUtils;
import erp.chain.utils.LogUtils;
import erp.chain.utils.ValidateUtils;
import org.apache.commons.lang.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import saas.api.common.ApiRest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Aspect
@Component
@Order
public class CallActionAspect{
    @Autowired
    private ApplicationContext applicationContext;
    private ConcurrentHashMap<Class<?>, Object> serviceMap = new ConcurrentHashMap<Class<?>, Object>();

    private Object obtainService(Class<?> serviceClass){
        if(!serviceMap.containsKey(serviceClass)){
            serviceMap.put(serviceClass, applicationContext.getBean(serviceClass));
        }
        return serviceMap.get(serviceClass);
    }

    @Around(value = "execution(public * erp.chain.controller.online.*.*(..)) && @annotation(apiRestAction) || execution(public * erp.chain.controller.o2o.*.*(..)) && @annotation(apiRestAction))")
    public Object callApiRestAction(ProceedingJoinPoint proceedingJoinPoint, ApiRestAction apiRestAction){
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        Object returnValue = null;

        Throwable throwable = null;
        try{
            returnValue = callAction(proceedingJoinPoint, requestParameters, apiRestAction.modelClass(), apiRestAction.serviceClass(), apiRestAction.serviceMethodName(), apiRestAction.datePattern());
        }
        catch(InvocationTargetException e){
            throwable = e.getTargetException();
        }
        catch(Throwable t){
            throwable = t;
        }

        if(throwable != null){
            LogUtils.error(apiRestAction.error(), proceedingJoinPoint.getTarget().getClass().getName(), proceedingJoinPoint.getSignature().getName(), throwable, requestParameters);
            if(throwable instanceof CustomException){
                ApiRest apiRest = new ApiRest();
                apiRest.setError(throwable.getMessage());
                apiRest.setIsSuccess(false);
                returnValue = BeanUtils.toJsonStr(apiRest);
            }
            else{
                ApiRest apiRest = new ApiRest();
                apiRest.setError(apiRestAction.error());
                apiRest.setIsSuccess(false);
                returnValue = BeanUtils.toJsonStr(apiRest);
            }
        }
        return returnValue;
    }

    @Around(value = "execution(public * erp.chain.controller.online.*.*(..)) && @annotation(resultJSONAction)")
    public Object callResultJSONAction(ProceedingJoinPoint proceedingJoinPoint, ResultJSONAction resultJSONAction){
        Map<String, String> requestParameters = ApplicationHandler.getRequestParameters();
        Object returnValue = null;

        Throwable throwable = null;
        try{
            returnValue = callAction(proceedingJoinPoint, requestParameters, resultJSONAction.modelClass(), resultJSONAction.serviceClass(), resultJSONAction.serviceMethodName(), resultJSONAction.datePattern());
        }
        catch(InvocationTargetException e){
            throwable = e.getTargetException();
        }
        catch(Throwable t){
            throwable = t;
        }

        if(throwable != null){
            LogUtils.error(resultJSONAction.error(), proceedingJoinPoint.getTarget().getClass().getName(), proceedingJoinPoint.getSignature().getName(), throwable, requestParameters);
            if(throwable instanceof CustomException){
                ResultJSON resultJSON = new ResultJSON();
                resultJSON.setMsg(throwable.getMessage());
                resultJSON.setIsSuccess(false);
                returnValue = BeanUtils.toJsonStr(resultJSON);
            }
            else{
                ResultJSON resultJSON = new ResultJSON();
                resultJSON.setMsg(resultJSONAction.error());
                resultJSON.setIsSuccess(false);
                returnValue = BeanUtils.toJsonStr(resultJSON);
            }
        }
        return returnValue;
    }

    public Object callAction(ProceedingJoinPoint proceedingJoinPoint, Map<String, String> requestParameters, Class<? extends BasicModel> modelClass, Class<?> serviceClass, String serviceMethodName, String datePattern) throws Throwable{
        Object returnValue = null;
        if(modelClass != BasicModel.class && serviceClass != Object.class && StringUtils.isNotBlank(serviceMethodName)){
            BasicModel model = null;
            if(StringUtils.isNotBlank(datePattern)){
                model = ApplicationHandler.instantiateObject(modelClass, requestParameters, datePattern, "");
            }
            else{
                model = ApplicationHandler.instantiateObject(modelClass, requestParameters);
            }
            model.validateAndThrow();

            Method method = serviceClass.getDeclaredMethod(serviceMethodName, modelClass);
            ValidateUtils.notNull(method, "系统异常！");

            method.setAccessible(true);

            returnValue = method.invoke(obtainService(serviceClass), model);
            if(!(returnValue instanceof String)){
                returnValue = BeanUtils.toJsonStr(returnValue);
            }
        }
        else{
            returnValue = proceedingJoinPoint.proceed();
        }
        return returnValue;
    }
}
