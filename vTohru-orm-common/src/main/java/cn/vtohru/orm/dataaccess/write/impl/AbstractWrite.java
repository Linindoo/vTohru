package cn.vtohru.orm.dataaccess.write.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import cn.vtohru.orm.IDataStore;
import cn.vtohru.orm.dataaccess.ISession;
import cn.vtohru.orm.dataaccess.impl.AbstractDataAccessObject;
import cn.vtohru.orm.dataaccess.query.IQuery;
import cn.vtohru.orm.dataaccess.write.IWrite;
import cn.vtohru.orm.dataaccess.write.IWriteResult;
import cn.vtohru.orm.mapping.IProperty;
import cn.vtohru.orm.mapping.IStoreObject;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Promise;

/**
 * Abstract implementation of {@link IWrite}
 *
 * @author Michael Remme
 * @param <T>
 *          the underlaying mapper to be used
 */

public abstract class AbstractWrite<T> extends AbstractDataAccessObject<T> implements IWrite<T> {

  private ISession session;

  private static final io.vertx.core.logging.Logger LOGGER = io.vertx.core.logging.LoggerFactory
      .getLogger(AbstractWrite.class);

  private final List<T> objectsToSave = new ArrayList<>();
  private IQuery<T> query;

  protected boolean partialUpdate;

  /**
   * @param mapperClass
   * @param datastore
   */
  public AbstractWrite(final Class<T> mapperClass, final IDataStore<?, ?> datastore) {
    super(mapperClass, datastore);
  }

  /**
   * Get the objects, which were defined to be saved
   *
   * @return
   */
  Iterator<T> getSelection() {
    return objectsToSave.iterator();
  }

  @Override
  public final void save(final Handler<AsyncResult<IWriteResult>> resultHandler) {
    sync(syncResult -> {
      if (syncResult.failed()) {
        resultHandler.handle(Future.failedFuture(syncResult.cause()));
      } else {
        try {
          Promise<IWriteResult> rf = Promise.promise();
          rf.future().onComplete(resultHandler);
          internalSave().onSuccess(rf::complete).onFailure(rf::fail);
        } catch (Exception e) {
          resultHandler.handle(Future.failedFuture(e));
        }
      }
    });
  }


  /**
   * This method is called after the sync call to execute the write action
   *
   */
  protected abstract Future<IWriteResult> internalSave();

  /**
   * Get the objects that shall be saved
   *
   * @return the objectsToSave
   */
  protected final List<T> getObjectsToSave() {
    return objectsToSave;
  }

  @Override
  public final void add(final T mapper) {
    objectsToSave.add(mapper);
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.dataaccess.write.IWrite#add(java.util.List)
   */
  @Override
  public void addAll(final Collection<T> mapperList) {
    for (T mapper : mapperList) {
      add(mapper);
    }
  }

  /**
   * After inserting an instance, the id is placed into the entity and into the IStoreObject.
   *
   * @param id
   *          the id to be stored
   * @param storeObject
   *          the instance of {@link IStoreObject}
   * @param resultHandler
   *          the handler to be informed
   */
  protected void setIdValue(final Object id, final IStoreObject<T, ?> storeObject,
      final Handler<AsyncResult<Void>> resultHandler) {
      IProperty idField = getMapper().getIdInfo().getField();
      idField.writeData(storeObject.getEntity(), id.toString()).onComplete(resultHandler);
  }

  /*
   * (non-Javadoc)
   *
   * @see cn.vtohru.orm.dataaccess.write.IWrite#size()
   */
  @Override
  public int size() {
    return objectsToSave.size();
  }

  /**
   * @return the query
   */
  protected IQuery<T> getQuery() {
    return query;
  }

  @Override
  public void setQuery(final IQuery<T> query) {
    this.query = query;
  }

  @Override
  public void setPartialUpdate(final boolean partialUpdate) {
    this.partialUpdate = partialUpdate;
  }

}
