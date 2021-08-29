package cn.vtohru.orm.util;

import io.vertx.core.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractCollectionAsync<E> implements CollectionAsync<E> {
    private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
            .getLogger(AbstractCollectionAsync.class);

    /**
     *
     */
    public AbstractCollectionAsync() {
    }

    @Override
    public boolean isEmpty() {
        return size() == 0;
    }

    @Override
    public void contains(final Object o, final Handler<AsyncResult<Boolean>> handler) {
        toArray(result -> {
            if (result.failed()) {
                handler.handle(Future.failedFuture(result.cause()));
            } else {
                handler.handle(Future.succeededFuture(contains(result.result(), o)));
            }
        });
    }

    /*
     * (non-Javadoc)
     *
     * @see de.braintags.vertx.util.util.CollectionAsync#containsAll(de.braintags.vertx.util.util.CollectionAsync,
     * io.vertx.core.Handler)
     */
    @Override
    public void containsAll(final CollectionAsync<?> c, final Handler<AsyncResult<Boolean>> handler) {
        if (c.isEmpty() || isEmpty()) {
            handler.handle(Future.succeededFuture(false));
            return;
        }

        toArray(sourceResult -> {
            if (sourceResult.failed()) {
                handler.handle(Future.failedFuture(sourceResult.cause()));
            } else {
                c.toArray(checkResult -> {
                    if (checkResult.failed()) {
                        handler.handle(Future.failedFuture(checkResult.cause()));
                    } else {
                        Object[] sourceArray = sourceResult.result();
                        Object[] checkArray = checkResult.result();
                        handler.handle(Future.succeededFuture(containsAll(sourceArray, checkArray)));
                    }
                });
            }
        });
    }

    private boolean containsAll(final Object[] objects, final Object[] searchObjects) {
        for (Object searchObject : searchObjects) {
            if (!contains(objects, searchObject)) {
                return false;
            }
        }
        return true;
    }

    private boolean contains(final Object[] objects, final Object searchObject) {
        for (Object sourceObject : objects) {
            if (sourceObject.equals(searchObject)) {
                return true;
            }
        }
        return false;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.braintags.vertx.util.util.CollectionAsync#toArray(io.vertx.core.Handler)
     */
    @Override
    public void toArray(final Handler<AsyncResult<Object[]>> handler) {
        if (isEmpty()) {
            handler.handle(Future.succeededFuture(new Object[size()]));
        } else {
            @SuppressWarnings("rawtypes")
            List<Future> fl = createFutureList();
            CompositeFuture.all(fl).onComplete(result -> {
                if (result.failed()) {
                    LOGGER.error("error converting results to array", result.cause());
                    handler.handle(Future.failedFuture(result.cause()));
                } else {
                    LOGGER.trace("futurelist finished");
                    handler.handle(Future.succeededFuture(result.result().list().toArray()));
                }
            });
        }
    }

    @SuppressWarnings("rawtypes")
    private List<Future> createFutureList() {
        List<Future> futures = new ArrayList<>();
        IteratorAsync<E> it = iterator();
        while (it.hasNext()) {
            Promise<E> promise = Promise.promise();
            futures.add(promise.future());
            it.next(promise);
        }
        return futures;
    }

    /*
     * (non-Javadoc)
     *
     * @see de.braintags.vertx.util.util.CollectionAsync#add(java.lang.Object)
     */
    @Override
    public boolean add(final E e) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.braintags.vertx.util.util.CollectionAsync#remove(java.lang.Object)
     */
    @Override
    public boolean remove(final Object o) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.braintags.vertx.util.util.CollectionAsync#addAll(de.braintags.vertx.util.util.CollectionAsync)
     */
    @Override
    public boolean addAll(final CollectionAsync<? extends E> c) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.braintags.vertx.util.util.CollectionAsync#removeAll(java.util.Collection)
     */
    @Override
    public boolean removeAll(final Collection<?> c) {
        throw new UnsupportedOperationException();
    }

    /*
     * (non-Javadoc)
     *
     * @see de.braintags.vertx.util.util.CollectionAsync#clear()
     */
    @Override
    public void clear() {
        throw new UnsupportedOperationException();
    }

}
