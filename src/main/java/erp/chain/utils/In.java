package erp.chain.utils;



import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 *
 */
@Documented
@Constraint(validatedBy = InValidator.class)
@Target({FIELD, PARAMETER})
@Retention(RUNTIME)
public @interface In {
    String[] value();
    String message() default "{erp.chain.utils.In.message}";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
    /**
     * Defines several {@code @In} annotations on the same element.
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER })
    @Retention(RUNTIME)
    @Documented
    public @interface List {
        In[] value();
    }
}
