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
package cn.vtohru.orm.dataaccess.write;

import java.util.Collection;

import cn.vtohru.orm.dataaccess.IAccessResult;
import cn.vtohru.orm.mapping.IStoreObject;

/**
 * This object is created by a save action and contains the information about the action itself and the objects saved
 * 
 * 
 * @author Michael Remme
 *
 */
public interface IWriteResult extends Collection<IWriteEntry>, IAccessResult {
    void addEntry(IStoreObject<?, ?> sto, Object id, WriteAction action);
}
