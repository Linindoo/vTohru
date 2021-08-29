package cn.vtohru.orm.exception;

public class ClassAccessException extends RuntimeException {

    /**
     * @param message
     */
    public ClassAccessException(String message) {
        super(message);
    }

    /**
     * @param message
     * @param cause
     */
    public ClassAccessException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public ClassAccessException(String message, Throwable cause) {
        super(message, cause);
    }
}
