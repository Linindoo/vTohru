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
package cn.vtohru.orm.dataaccess.delete.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import cn.vtohru.orm.dataaccess.delete.IDelete;
import cn.vtohru.orm.dataaccess.delete.IDeleteResult;
import cn.vtohru.orm.observer.IObserver;
import cn.vtohru.orm.observer.IObserverContext;
import cn.vtohru.orm.observer.IObserverEvent;
import cn.vtohru.orm.observer.ObserverEventType;
import cn.vtohru.orm.observer.impl.handler.AbstractEventHandler;
import io.vertx.core.Future;

/**
 * Handles the event {@link ObserverEventType#AFTER_DELETE }
 * 
 * @author Michael Remme
 * 
 */
public class AfterDeleteHandler extends AbstractEventHandler<IDelete<?>, IDeleteResult> {

  /*
   * (non-Javadoc)
   * 
   * @see
   * cn.vtohru.orm.observer.impl.AbstractEventHandler#createEntityFutureList(cn.vtohru.orm
   * .observer.IObserver, cn.vtohru.orm.dataaccess.IDataAccessObject,
   * cn.vtohru.orm.observer.IObserverContext)
   */
  @SuppressWarnings("rawtypes")
  @Override
  protected List<Future> createEntityFutureList(IObserver observer, IDelete<?> deleteObject, IDeleteResult result,
      IObserverContext context) {
    List<Future> fl = new ArrayList<>();
    Iterator<?> selection = ((Delete<?>) deleteObject).getSelection();
    while (selection.hasNext()) {
      IObserverEvent event = IObserverEvent.createEvent(ObserverEventType.AFTER_DELETE, selection.next(), null,
          deleteObject, deleteObject.getDataStore());
      if (observer.canHandleEvent(event, context)) {
        Future tf = observer.handleEvent(event, context);
        if (tf != null) {
          fl.add(tf);
        }
      }
    }
    return fl;
  }

}
