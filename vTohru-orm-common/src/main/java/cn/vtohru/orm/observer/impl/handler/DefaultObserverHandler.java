/*
 * #%L
 * vertx-pojo-mapper-common
 * %%
 * Copyright (C) 2017 Braintags GmbH
 * %%
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * #L%
 */
package cn.vtohru.orm.observer.impl.handler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.vtohru.orm.annotation.Observer;
import cn.vtohru.orm.annotation.ObserverOption;
import cn.vtohru.orm.annotation.VersionInfo;
import cn.vtohru.orm.dataaccess.delete.IDelete;
import cn.vtohru.orm.dataaccess.delete.IDeleteResult;
import cn.vtohru.orm.dataaccess.delete.impl.AfterDeleteHandler;
import cn.vtohru.orm.dataaccess.delete.impl.BeforeDeleteHandler;
import cn.vtohru.orm.dataaccess.query.IQuery;
import cn.vtohru.orm.dataaccess.query.IQueryResult;
import cn.vtohru.orm.dataaccess.write.IWrite;
import cn.vtohru.orm.dataaccess.write.IWriteResult;
import cn.vtohru.orm.dataaccess.write.impl.AfterInsertHandler;
import cn.vtohru.orm.dataaccess.write.impl.AfterUpdateHandler;
import cn.vtohru.orm.dataaccess.write.impl.BeforeInsertHandler;
import cn.vtohru.orm.dataaccess.write.impl.BeforeUpdateHandler;
import cn.vtohru.orm.exception.MappingException;
import cn.vtohru.orm.init.ObserverDefinition;
import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.observer.IObserver;
import cn.vtohru.orm.observer.IObserverContext;
import cn.vtohru.orm.observer.IObserverHandler;
import cn.vtohru.orm.observer.ObserverEventType;
import cn.vtohru.orm.versioning.ExecuteVersionConverter;
import io.vertx.core.Future;
import io.vertx.core.Promise;

/**
 * Default implementation for {@link IObserverHandler}
 * 
 * @author Michael Remme
 * 
 */
public class DefaultObserverHandler implements IObserverHandler {
  private List<ObserverDefinition<?>> observerList = new ArrayList<>();
  private Map<ObserverEventType, List<IObserver>> eventObserverCache = new HashMap<>();
  private IMapper<?> mapper;
  private BeforeInsertHandler beforeInsertHandler = new BeforeInsertHandler();
  private BeforeUpdateHandler beforeUpdateHandler = new BeforeUpdateHandler();
  private AfterInsertHandler afterInsertHandler = new AfterInsertHandler();
  private AfterUpdateHandler afterUpdateHandler = new AfterUpdateHandler();
  private AfterLoadHandler afterLoadHandler = new AfterLoadHandler();
  private BeforeLoadHandler beforeLoadHandler = new BeforeLoadHandler();
  private AfterDeleteHandler afterDeleteHandler = new AfterDeleteHandler();
  private BeforeDeleteHandler beforeDeleteHandler = new BeforeDeleteHandler();
  private AfterMappingHandler afterMappingHandler = new AfterMappingHandler();

  /**
   * Create a new instance, where usable observers are examined
   * 
   * @param mapper
   */
  public DefaultObserverHandler(IMapper<?> mapper) {
    this.mapper = mapper;
    computeObserver();
  }

  /**
   * Computes the list of all observers, which can be executed for the current mapper class.
   */
  private void computeObserver() {
    List<ObserverDefinition<?>> tmpList = mapper.getMapperFactory().getDataStore().getSettings().getObserverSettings()
        .getObserverDefinitions(mapper);
    Observer ob = mapper.getAnnotation(Observer.class);
    if (ob != null) {
      ObserverDefinition<?> os = new ObserverDefinition<>(ob.observerClass());
      os.setPriority(ob.priority());
      ObserverEventType[] tl = ob.eventTypes();
      for (ObserverEventType t : tl) {
        os.getEventTypeList().add(t);
      }
      ObserverOption[] ooptions = ob.observerOptions();
      for (ObserverOption option : ooptions) {
        os.getObserverProperties().setProperty(option.key(), option.value());
      }
      tmpList.add(os);
    }
    tmpList.sort((os1, os2) -> Integer.compare(os2.getPriority(), os1.getPriority()));
    observerList = tmpList;
  }

  @Override
  public List<IObserver> getObserver(ObserverEventType event) {
    if (!eventObserverCache.containsKey(event)) {
      List<IObserver> ol = new ArrayList<>();
      observerList.stream().filter(os -> os.isApplicableFor(event)).forEach(os -> {
        try {
          IObserver observer = os.getObserverClass().newInstance();
          observer.getObserverProperties().putAll(os.getObserverProperties());
          observer.init(mapper.getMapperFactory().getDataStore().getVertx());
          ol.add(observer);
        } catch (Exception e) {
          throw new MappingException(e);
        }
      });
      if (mapper.getVersionInfo() != null && event.equals(mapper.getVersionInfo().eventType())) {
        VersionInfo vi = mapper.getVersionInfo();
        ol.add(new ExecuteVersionConverter(vi));
      }
      eventObserverCache.put(event, ol);
    }
    return eventObserverCache.get(event);
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.observer.IObserverHandler#handleBeforeSave(cn.vtohru.orm.dataaccess.
   * write.IWrite)
   */
  @Override
  public <T> Future<Void> handleBeforeUpdate(IWrite<T> writeObject, T entity, IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.BEFORE_UPDATE);
    Promise<Void> f = Promise.promise();
    if (ol.isEmpty() || writeObject.size() <= 0) {
      f.complete();
    } else {
      return getBeforeUpdateHandler().handle(writeObject, entity, context, ol);
    }
    return f.future();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.observer.IObserverHandler#handleAfterUpdate(cn.vtohru.orm.dataaccess.
   * write.IWrite, cn.vtohru.orm.dataaccess.write.IWriteResult,
   * cn.vtohru.orm.observer.IObserverContext)
   */
  @Override
  public <T> Future<Void> handleAfterUpdate(IWrite<T> writeObject, IWriteResult writeResult, IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.AFTER_UPDATE);
    Promise<Void> f = Promise.promise();
    if (ol.isEmpty() || writeObject.size() <= 0) {
      f.complete();
    } else {
      return getAfterUpdateHandler().handle(writeObject, writeResult, context, ol);
    }
    return f.future();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.observer.IObserverHandler#handleBeforeInsert(cn.vtohru.orm.dataaccess.
   * write.IWrite, java.lang.Object, cn.vtohru.orm.observer.IObserverContext)
   */
  @Override
  public <T> Future<Void> handleBeforeInsert(IWrite<T> writeObject, T entity, IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.BEFORE_INSERT);
    Promise<Void> f = Promise.promise();
    if (ol.isEmpty() || writeObject.size() <= 0) {
      f.complete();
    } else {
      return getBeforeInsertHandler().handle(writeObject, entity, context, ol);
    }
    return f.future();
  }

  @Override
  public <T> Future<Void> handleAfterInsert(IWrite<T> writeObject, IWriteResult writeResult, IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.AFTER_INSERT);
    Promise<Void> f = Promise.promise();
    if (ol.isEmpty() || writeObject.size() <= 0) {
      f.complete();
    } else {
      return getAfterInsertHandler().handle(writeObject, writeResult, context, ol);
    }
    return f.future();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.observer.IObserverHandler#handleBeforeLoad(cn.vtohru.orm.dataaccess.
   * query.IQuery, cn.vtohru.orm.observer.IObserverContext)
   */
  @Override
  public <T> Future<Void> handleBeforeLoad(IQuery<T> queryObject, IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.BEFORE_LOAD);
    Promise<Void> f = Promise.promise();
    if (ol.isEmpty()) {
      f.complete();
    } else {
      return getBeforeLoadHandler().handle(queryObject, null, context, ol);
    }
    return f.future();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.observer.IObserverHandler#handleAfterLoad(cn.vtohru.orm.dataaccess.
   * query.IQuery, cn.vtohru.orm.dataaccess.query.IQueryResult,
   * cn.vtohru.orm.observer.IObserverContext)
   */
  @Override
  public <T> Future<Void> handleAfterLoad(IQuery<T> queryObject, IQueryResult<T> queryResult,
      IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.AFTER_LOAD);
    Promise<Void> f = Promise.promise();
    if (ol.isEmpty() || queryResult.isEmpty()) {
      f.complete();
    } else {
      return getAfterLoadHandler().handle(queryObject, queryResult, context, ol);
    }
    return f.future();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.observer.IObserverHandler#handleBeforeDelete(cn.vtohru.orm.dataaccess.
   * delete.IDelete, cn.vtohru.orm.observer.IObserverContext)
   */
  @Override
  public <T> Future<Void> handleBeforeDelete(IDelete<T> deleteObject, IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.BEFORE_DELETE);
    Promise<Void> f = Promise.promise();
    if (ol.isEmpty()) {
      f.complete();
    } else {
      return getBeforeDeleteHandler().handle(deleteObject, null, context, ol);
    }
    return f.future();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.observer.IObserverHandler#handleAfterDelete(cn.vtohru.orm.dataaccess.
   * delete.IDelete, cn.vtohru.orm.dataaccess.delete.IDeleteResult,
   * cn.vtohru.orm.observer.IObserverContext)
   */
  @Override
  public <T> Future<Void> handleAfterDelete(IDelete<T> deleteObject, IDeleteResult deleteResult,
      IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.AFTER_DELETE);
    Promise<Void> f = Promise.promise();
    if (ol.isEmpty() || deleteObject.size() <= 0) {
      f.complete();
    } else {
      return getAfterDeleteHandler().handle(deleteObject, deleteResult, context, ol);
    }
    return f.future();
  }

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.observer.IObserverHandler#handleAfterMapping(cn.vtohru.orm.mapping.
   * IMapper, cn.vtohru.orm.observer.IObserverContext)
   */
  @Override
  public <T> Future<Void> handleAfterMapping(IMapper<T> mapper, IObserverContext context) {
    List<IObserver> ol = getObserver(ObserverEventType.AFTER_MAPPING);
    Promise<Void> f = Promise.promise();
    if (ol.isEmpty()) {
      f.complete();
    } else {
      return getAfterMappingHandler().handle(mapper, context, ol);
    }
    return f.future();
  }

  /**
   * @return the beforeInsertHandler
   */
  protected BeforeInsertHandler getBeforeInsertHandler() {
    return beforeInsertHandler;
  }

  /**
   * @return the afterInsertHandler
   */
  protected AfterInsertHandler getAfterInsertHandler() {
    return afterInsertHandler;
  }

  /**
   * @return the beforeUpdateHandler
   */
  protected BeforeUpdateHandler getBeforeUpdateHandler() {
    return beforeUpdateHandler;
  }

  /**
   * @return the afterUpdateHandler
   */
  protected AfterUpdateHandler getAfterUpdateHandler() {
    return afterUpdateHandler;
  }

  /**
   * @return the afterLoadHandler
   */
  protected AfterLoadHandler getAfterLoadHandler() {
    return afterLoadHandler;
  }

  /**
   * @return the beforeLoadHandler
   */
  protected BeforeLoadHandler getBeforeLoadHandler() {
    return beforeLoadHandler;
  }

  /**
   * @return the afterDeleteHandler
   */
  protected AfterDeleteHandler getAfterDeleteHandler() {
    return afterDeleteHandler;
  }

  /**
   * @return the beforeDeleteHandler
   */
  protected BeforeDeleteHandler getBeforeDeleteHandler() {
    return beforeDeleteHandler;
  }

  /**
   * @return the afterMappingHandler
   */
  protected AfterMappingHandler getAfterMappingHandler() {
    return afterMappingHandler;
  }

}
