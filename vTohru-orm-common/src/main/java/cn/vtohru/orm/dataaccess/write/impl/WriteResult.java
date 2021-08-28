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
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import cn.vtohru.orm.dataaccess.write.IWriteEntry;
import cn.vtohru.orm.dataaccess.write.IWriteResult;
import cn.vtohru.orm.dataaccess.write.WriteAction;
import cn.vtohru.orm.exception.InsertException;
import cn.vtohru.orm.mapping.IStoreObject;

/**
 * Default implementation of {@link IWriteResult}
 * 
 * @author Michael Remme
 * 
 */

public class WriteResult implements IWriteResult {

  private List<IWriteEntry> resultList = new ArrayList<>();
  private List insertedIds = new ArrayList();

  public WriteResult() {
  }

  public WriteResult(final List<IWriteEntry> resultList) {
    this.resultList = resultList;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.dataaccess.write.IWriteResult#getResult()
   */
  @Override
  public Iterator<IWriteEntry> iterator() {
    return resultList.iterator();
  }

  @Override
  public int size() {
    return resultList.size();
  }

  @Override
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (IWriteEntry entry : resultList) {
      builder.append(entry.toString()).append("\n");
    }
    return builder.toString();
  }

  @Override
  public boolean isEmpty() {
    return resultList.isEmpty();
  }

  @Override
  public boolean contains(final Object o) {
    return resultList.contains(o);
  }

  @Override
  public Object[] toArray() {
    return resultList.toArray();
  }

  @Override
  public <T> T[] toArray(final T[] a) {
    return resultList.toArray(a);
  }

  @Override
  public boolean add(final IWriteEntry e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(final Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(final Collection<?> c) {
    return resultList.containsAll(c);
  }

  @Override
  public boolean addAll(final Collection<? extends IWriteEntry> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(final Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void addEntry(IStoreObject<?, ?> sto, Object id, WriteAction action) {
    addEntry(new WriteEntry(sto, id, action));
  }

  protected void addEntry(IWriteEntry entry) {
    resultList.add(entry);
    if (entry.getAction().equals(WriteAction.INSERT)) {
      if (insertedIds.contains(entry.getId())) {
        throw new InsertException(String.format("Trial to insert duplicate ID. Existing IDs: %s | new Id: %s ",
                insertedIds, entry.getId()));
      } else {
        insertedIds.add(entry.getId());
      }
    }
  }
}
