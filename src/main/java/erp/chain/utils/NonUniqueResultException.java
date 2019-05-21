package erp.chain.utils;

/**
 * 资源不唯一异常
 * @author hxh
 */

public class NonUniqueResultException extends RuntimeException {
    public NonUniqueResultException() {
    }

    public NonUniqueResultException(String message) {
        super(message);
    }

    public NonUniqueResultException(String message, Throwable cause) {
        super(message, cause);
    }

    public NonUniqueResultException(Throwable cause) {
        super(cause);
    }

    public NonUniqueResultException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
