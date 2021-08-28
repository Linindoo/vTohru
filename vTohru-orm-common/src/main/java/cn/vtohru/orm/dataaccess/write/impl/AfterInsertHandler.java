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
package cn.vtohru.orm.dataaccess.write.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.vtohru.orm.dataaccess.write.IWrite;
import cn.vtohru.orm.dataaccess.write.IWriteResult;
import cn.vtohru.orm.observer.IObserver;
import cn.vtohru.orm.observer.IObserverContext;
import cn.vtohru.orm.observer.IObserverEvent;
import cn.vtohru.orm.observer.ObserverEventType;
import cn.vtohru.orm.observer.impl.handler.AbstractEventHandler;
import io.vertx.core.Future;

/**
 * Handles the event {@link ObserverEventType#AFTER_INSERT }
 * 
 * @author Michael Remme
 * 
 */
public class AfterInsertHandler extends AbstractEventHandler<IWrite<?>, IWriteResult> {

  /**
   * @param observer
   * @param writeObject
   * @param context
   * @return
   */
  @SuppressWarnings("rawtypes")
  @Override
  protected List<Future> createEntityFutureList(IObserver observer, IWrite<?> writeObject, IWriteResult result,
      IObserverContext context) {
    List<Future> fl = new ArrayList<>();
    Iterator<?> selection = ((AbstractWrite) writeObject).getSelection();
    while (selection.hasNext()) {
      IObserverEvent event = IObserverEvent.createEvent(getEventType(), selection.next(), result, writeObject,
          writeObject.getDataStore());
      if (observer.canHandleEvent(event, context)) {
        Future tf = observer.handleEvent(event, context);
        if (tf != null) {
          fl.add(tf);
        }
      }
    }
    return fl;
  }

  protected ObserverEventType getEventType() {
    return ObserverEventType.AFTER_INSERT;
  }

}
