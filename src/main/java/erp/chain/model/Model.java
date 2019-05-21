package erp.chain.model;

import erp.chain.utils.BindParamsException;
import erp.chain.utils.SpringContextUtil;
import net.sf.json.JSONObject;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Model基类
 */

public class Model implements Serializable {
    /**
     * 错误信息
     */
    protected final Map<String, String> errors = new HashMap<>();

    /**
     * 校验
     *
     * @return true ：校验通过 false 校验不通过
     */
    public boolean validate(Validator validator) {
        if (validator == null) {
            return true;
        }
        Iterator iterator = validator.validate(this).iterator();
        errors.clear();

        while (iterator.hasNext()) {
            ConstraintViolation violation = (ConstraintViolation) iterator.next();
            errors.put(violation.getPropertyPath().toString(), violation.getMessage());
        }
        if (errors.size() == 0) {
            return true;
        }
        return false;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    /**
     * 校验不通过抛出BindParamsException异常
     */
    public void validateExc(Validator validator) {
        if (!this.validate(validator)) {
            throw new BindParamsException(this.errorToStr());
        }
    }
    /**
     * 校验不通过抛出BindParamsException异常
     */
    public void validateExc() {
        if (!this.validate()) {
            throw new BindParamsException(this.errorToStr());
        }
    }

    public boolean validate() {
        Validator validator = (Validator) SpringContextUtil.getBean("validator");
        return this.validate(validator);
    }

    public String errorToStr() {
        return JSONObject.fromObject(this.errors).toString();
    }
}
