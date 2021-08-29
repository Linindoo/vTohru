package cn.vtohru.orm.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class ErrorObject<E> {
    private Throwable throwable;
    private boolean errorHandled = false;
    private Handler<AsyncResult<E>> handler;

    /**
     * Creates an instance with a handler, which will be informed, when an Exception is added
     * into the current instance
     *
     * @param handler if a handler is set, it is automatically informed, if an error occured
     */
    public ErrorObject(Handler<AsyncResult<E>> handler) {
        this.handler = handler;
    }

    /**
     * did an error occur?
     *
     * @return the error
     */
    public final boolean isError() {
        return throwable != null;
    }

    /**
     * an optional information about Throwable
     *
     * @return the throwable
     */
    public final Throwable getThrowable() {
        return throwable;
    }

    /**
     * Return an occured Throwable as {@link RuntimeException}
     *
     * @return the exception
     */
    public RuntimeException getRuntimeException() {
        return throwable instanceof RuntimeException ? (RuntimeException) throwable : new RuntimeException(throwable);
    }

    /**
     * an optional information about Throwable
     *
     * @param throwable the throwable to set
     */
    public final void setThrowable(Throwable throwable) {
        this.throwable = throwable;
        handleError();
    }

    /**
     * Creates a failed future with the contained Throwable
     *
     * @return the {@link Future}
     */
    public Future<E> toFuture() {
        if (throwable == null)
            throw new NullPointerException("Throwable is null");
        return Future.failedFuture(throwable);
    }

    /**
     * If an error occured or a result exists, the handler will be called with a succeedded or error {@link Future}
     *
     * @param handler
     */
    boolean handleError() {
        if (handler == null)
            return false;
        if (isError()) {
            if (errorHandled)
                return true;
            handler.handle(toFuture());
            errorHandled = true;
            return true;
        }
        return false;
    }

    /**
     * If the method {@link #handleError(Handler)} was called, this returns true
     *
     * @return the errorHandled
     */
    public final boolean isErrorHandled() {
        return errorHandled;
    }

    /**
     * The internal handler to be used for information
     *
     * @return
     */
    protected Handler<AsyncResult<E>> getHandler() {
        return handler;
    }

}
