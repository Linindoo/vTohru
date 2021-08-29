package cn.vtohru.orm.util;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;

public class ResultObject<E> extends ErrorObject<E> {
    private boolean resultDefined = false;
    private E result;

    /**
     * Constructor with a {@link Handler}, which will be informed about the result, when the
     * method {@link #setResult(Object)} is called
     *
     * @param handler
     *          if a handler is set, it is automatically informed, if an error occured
     */
    public ResultObject(Handler<AsyncResult<E>> handler) {
        super(handler);
    }

    /**
     * @return the result
     */
    public final E getResult() {
        return result;
    }

    /**
     * @param result
     *          the result to set
     */
    public final void setResult(E result) {
        this.result = result;
        this.resultDefined = true;
        handleResult();
    }

    /**
     * @return the resultDefined
     */
    public final boolean isResultDefined() {
        return resultDefined;
    }

    /**
     * If an error occured or a result exists, the handler will be called with a succeedded or error {@link Future}
     *
     * @param handler
     */
    private boolean handleResult() {
        if (super.handleError()) {
            return true;
        } else if (isResultDefined() && getHandler() != null) {
            getHandler().handle(Future.succeededFuture(result));
            return true;
        }
        return false;
    }
}
