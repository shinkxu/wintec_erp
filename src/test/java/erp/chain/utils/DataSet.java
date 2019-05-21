package erp.chain.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 数据源.
 */
@Target({TYPE})
@Retention(RUNTIME)
public @interface DataSet {
    /**
     *
     */
    String[] key() default {};

    /**
     *
     */
    String[] path() default {};
}
