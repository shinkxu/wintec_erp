package erp.chain.annotations;

import erp.chain.model.online.BasicModel;

import java.lang.annotation.*;

/**
 * Created by liuyandong on 2018-05-07.
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ResultJSONAction {
    Class<? extends BasicModel> modelClass() default BasicModel.class;

    Class<?> serviceClass() default Object.class;

    String serviceMethodName() default "";

    String error() default "";

    String datePattern() default "";
}
