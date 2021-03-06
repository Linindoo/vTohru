/*-
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
package cn.vtohru.orm.mapping.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import cn.vtohru.orm.annotation.Index;
import cn.vtohru.orm.annotation.IndexField;
import cn.vtohru.orm.dataaccess.query.IIndexedField;
import cn.vtohru.orm.mapping.IIndexDefinition;
import cn.vtohru.orm.mapping.IIndexFieldDefinition;
import cn.vtohru.orm.mapping.IMapper;
import cn.vtohru.orm.mapping.IndexOption;
import cn.vtohru.orm.mapping.IndexOption.IndexFeature;
import cn.vtohru.orm.util.StringUtil;
import io.micronaut.core.util.StringUtils;

/**
 * Implementation of {@link IIndexDefinition}
 * 
 * @author sschmitt
 *
 */
public class IndexDefinition implements IIndexDefinition {

  private String name;
  private final List<IIndexFieldDefinition> fields;
  private List<IndexOption> indexOptions;
  private String identifier;

  /**
   * Create a definition from an indexed field of a mapper
   * 
   * @param field
   *          the indexed field
   * @param mapper
   *          the mapper to fetch the column name of the field from
   */
  public IndexDefinition(final IIndexedField field, final IMapper<?> mapper) {
    name = createName(field);
    fields = new ArrayList<>();
    IndexFieldDefinition fieldDef = new IndexFieldDefinition();
    fieldDef.setName(field.getColumnName(mapper));
    fieldDef.setType(field.getType());
    fields.add(fieldDef);
  }

  private String createName(final IIndexedField field) {
    String fieldName = field.getFieldName();
    if (fieldName.length() > 80) {

      // index names can not be longer than 127 bytes, including the database and collection name
      return "IdxF_" + StringUtil.sha256Hex(fieldName);
    } else
      return "IdxF_" + fieldName;
  }

  /**
   * Create a definition from the annotation of an entity
   * 
   * @param index
   *          the annotation
   */
  public IndexDefinition(final Index index) {
    name = index.name();
    fields = new ArrayList<>();
    for (IndexField field : index.fields()) {
      IndexFieldDefinition def = new IndexFieldDefinition();
      def.setName(field.fieldName());
      def.setType(field.type());
      fields.add(def);
    }
    if (index.options() != null) {

      if (index.options().unique()) {
        getIndexOptions().add(new IndexOption(IndexFeature.UNIQUE, index.options().unique()));
      }
      if (index.options().sparse()) {
        getIndexOptions().add(new IndexOption(IndexFeature.SPARSE, index.options().sparse()));
      }
      if (StringUtils.isNotEmpty(index.options().partialFilterExpression()))
        getIndexOptions()
            .add(new IndexOption(IndexFeature.PARTIAL_FILTER_EXPRESSION, index.options().partialFilterExpression()));
    }
  }

  @Override
  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  @Override
  public List<IIndexFieldDefinition> getFields() {
    return fields;
  }

  @Override
  public List<IndexOption> getIndexOptions() {
    if (indexOptions == null)
      indexOptions = new ArrayList<>();
    return indexOptions;
  }

  @Override
  public String getIdentifier() {
    if (identifier == null) {
      identifier = createIdentifier();
    }
    return identifier;
  }

  /**
   * Create a unique identifier consisting of all field names sorted, combined, and transformed to lowercase
   * 
   * @return a unique identifier for the fields of the definition
   */
  private String createIdentifier() {
    return fields.stream().map(field -> field.getName() + ":" + field.getType()).sorted()
        .collect(Collectors.joining(".:")).toLowerCase(Locale.US);
  }

  @Override
  public String toString() {
    return "IndexDefinition [name=" + name + ", fields=" + fields + ", indexOptions=" + indexOptions + ", identifier="
        + getIdentifier() + "]";
  }
}
