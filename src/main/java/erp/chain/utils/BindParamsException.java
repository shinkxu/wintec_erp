package erp.chain.utils;

import java.util.Locale;

/**
 * �󶨲������
 *
 * @author hxh
 */

public class BindParamsException extends RuntimeException {
    public BindParamsException() {
        super();
    }

    public BindParamsException(String message) {
        super(message);
    }

    public BindParamsException(String message, Throwable cause) {
        super(message, cause);
    }

    public BindParamsException(Throwable cause) {
        super(cause);
    }

    public BindParamsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
