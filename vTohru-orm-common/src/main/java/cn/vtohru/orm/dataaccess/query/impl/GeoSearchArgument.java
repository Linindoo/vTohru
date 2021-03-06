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
package cn.vtohru.orm.dataaccess.query.impl;

import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;
import com.fasterxml.jackson.databind.DatabindContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.annotation.JsonTypeIdResolver;
import com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase;

import cn.vtohru.orm.datatypes.geojson.GeoJsonObject;
import cn.vtohru.orm.datatypes.geojson.GeoJsonType;
import cn.vtohru.orm.datatypes.geojson.GeoLineString;
import cn.vtohru.orm.datatypes.geojson.GeoMultiLineString;
import cn.vtohru.orm.datatypes.geojson.GeoMultiPoint;
import cn.vtohru.orm.datatypes.geojson.GeoMultiPolygon;
import cn.vtohru.orm.datatypes.geojson.GeoPoint;
import cn.vtohru.orm.datatypes.geojson.GeoPolygon;

/**
 * GeoSearchArgument contains possible parameters of a GeoSearch
 * 
 * @author Michael Remme
 * 
 */
@JsonInclude(Include.NON_NULL)
public class GeoSearchArgument {

  public static final String COORDINATES = "coordinates";

  @JsonProperty("$geometry")
  @JsonTypeInfo(include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "type", use = JsonTypeInfo.Id.CUSTOM)
  @JsonTypeIdResolver(GeoSearchArgumentTypeResolver.class)
  private GeoJsonObject geoJson;
  @JsonProperty("$maxDistance")
  private Integer maxDistance;

  protected GeoSearchArgument() {
    // default constructor for jackson
  }

  public GeoSearchArgument(final GeoJsonObject geoJson) {
    this.geoJson = geoJson;
  }

  public GeoSearchArgument(final GeoJsonObject geoJson, final int maxDistance) {
    this.geoJson = geoJson;
    this.maxDistance = maxDistance;
  }

  /**
   * @return the geoJson
   */
  public GeoJsonObject getGeoJson() {
    return geoJson;
  }

  /**
   * @return the maximum distance for the search, may be null
   */
  public Integer getMaxDistance() {
    return maxDistance;
  }

  /**
   * Custom resolver to hande serialization and deserialization of GeoJsonObjects by their respective GeoJsonTypes
   */
  public static class GeoSearchArgumentTypeResolver extends TypeIdResolverBase {

    /*
     * (non-Javadoc)
     * 
     * @see com.fasterxml.jackson.databind.jsontype.impl.TypeIdResolverBase#typeFromId(com.fasterxml.jackson.databind.
     * DatabindContext, java.lang.String)
     */
    @Override
    public JavaType typeFromId(final DatabindContext context, final String id) throws IOException {
      GeoJsonType type = null;
      for (GeoJsonType t : GeoJsonType.values()) {
        if (t.getTypeName().equals(id)) {
          type = t;
          break;
        }
      }
      if (type == null)
        throw new IllegalStateException("Could not find GeoJsonType for typeName: " + id);

      switch (type) {
      case LINE_STRING:
        return context.constructType(GeoLineString.class);
      case MULTI_LINE_STRING:
        return context.constructType(GeoMultiLineString.class);
      case MULTI_POINT:
        return context.constructType(GeoMultiPoint.class);
      case MULTI_POLYGON:
        return context.constructType(GeoMultiPolygon.class);
      case POINT:
        return context.constructType(GeoPoint.class);
      case POLYGON:
        return context.constructType(GeoPolygon.class);
      case GEOMETRY_COLLECTION:
        // no implementation yet
      default:
        throw new IllegalStateException("No implementation class defind for GeoJsonType " + type);
      }
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fasterxml.jackson.databind.jsontype.TypeIdResolver#idFromValue(java.lang.Object)
     */
    @Override
    public String idFromValue(final Object value) {
      if (!(value instanceof GeoJsonObject))
        throw new IllegalStateException("Can only resolve objects of type GeoJsonObject, but got " + value.getClass());
      return ((GeoJsonObject) value).getType().getTypeName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fasterxml.jackson.databind.jsontype.TypeIdResolver#idFromValueAndType(java.lang.Object, java.lang.Class)
     */
    @Override
    public String idFromValueAndType(final Object value, final Class<?> suggestedType) {
      return idFromValue(value);
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.fasterxml.jackson.databind.jsontype.TypeIdResolver#getMechanism()
     */
    @Override
    public Id getMechanism() {
      return Id.CUSTOM;
    }

  }
}
