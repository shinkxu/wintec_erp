package erp.chain.annotations;

import java.lang.annotation.*;

/**
 * Created by liuyandong on 2018-05-07.
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Column {
    String name() default "";
}
