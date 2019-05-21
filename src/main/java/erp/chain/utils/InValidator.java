package erp.chain.utils;

import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;

/**
 * '@In' 校验器
 */
public class InValidator implements ConstraintValidator<In, Object> {
    private static final Log log = LoggerFactory.make();
    private String[] value;

    public void initialize(In parameters) {
        value = parameters.value();
        validateParameters();
    }

    public boolean isValid(Object val, ConstraintValidatorContext constraintValidatorContext) {
        if (val == null){
            return true;
        }
        String target = val.toString();
        if (Arrays.asList(this.value).contains(target)){
            return true;
        }
        return false;
    }

    private void validateParameters() {
        if (value == null || value.length == 0) {
            log.getNullIsAnInvalidTypeForAConstraintValidatorException();
        }
    }
}
