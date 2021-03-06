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
package cn.vtohru.orm.dataaccess.query;

/**
 * Marker interface to mark a field condition that has a variable as value and must be passed through an
 * {@link IFieldValueResolver}.
 * The variable value must be set without any tags that were used to identify a value as variable. Only the true name of
 * the variable should be set as value, to ensure everything resolving the variable has the same value to work with.
 *
 * @author sschmitt
 *
 */
public interface IVariableFieldCondition extends IFieldCondition {

}
