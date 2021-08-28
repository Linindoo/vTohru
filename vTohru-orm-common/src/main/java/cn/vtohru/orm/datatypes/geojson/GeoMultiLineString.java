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
package cn.vtohru.orm.datatypes.geojson;


import java.util.Collections;
import java.util.List;

/**
 * A representation of a GeoJSON LineString.
 * 
 * @author Michael Remme
 * 
 */
public class GeoMultiLineString extends GeoJsonObject {
  private final List<List<Position>> coordinates;

  /**
   * Construct an instance with the given coordinates.
   *
   * @param coordinates
   *          the coordinates of each line
   */
  public GeoMultiLineString(final List<List<Position>> coordinates) {
    for (List<Position> line : coordinates) {
    }

    this.coordinates = Collections.unmodifiableList(coordinates);
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.datatypes.geojson.GeoJsonObject#getType()
   */
  @Override
  public GeoJsonType getType() {
    return GeoJsonType.MULTI_LINE_STRING;
  }

  /**
   * Gets the GeoJSON coordinates of this MultiLineString
   *
   * @return the coordinates
   */
  public List<List<Position>> getCoordinates() {
    return coordinates;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    if (!super.equals(o)) {
      return false;
    }

    GeoMultiLineString polygon = (GeoMultiLineString) o;

    if (!coordinates.equals(polygon.coordinates)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = super.hashCode();
    result = 31 * result + coordinates.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "MultiLineString{" + "coordinates=" + coordinates + '}';
  }
}
