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

import java.util.List;


/**
 * A representation of a GeoJSON Polygon.
 * 
 * @author Michael Remme
 * 
 */
public class GeoPolygon extends GeoJsonObject {
  private final PolygonCoordinates coordinates;

  /**
   * Construct an instance with the given coordinates.
   *
   * @param exterior
   *          the exterior ring of the polygon
   * @param holes
   *          optional interior rings of the polygon
   */
  public GeoPolygon(final List<Position> exterior, final List<Position>... holes) {
    this(new PolygonCoordinates(exterior, holes));
  }

  /**
   * Construct an instance with the given coordinates.
   *
   * @param coordinates
   *          the coordinates
   */
  public GeoPolygon(final PolygonCoordinates coordinates) {
    this.coordinates =  coordinates;
  }

  /*
   * (non-Javadoc)
   * 
   * @see cn.vtohru.orm.datatypes.geojson.GeoJsonObject#getType()
   */
  @Override
  public GeoJsonType getType() {
    return GeoJsonType.POLYGON;
  }

  /**
   * Gets the GeoJSON coordinates of the polygon
   *
   * @return the coordinates, which must have at least one element
   */
  public PolygonCoordinates getCoordinates() {
    return coordinates;
  }

  /**
   * Gets the exterior coordinates.
   *
   * @return the exterior coordinates
   */
  public List<Position> getExterior() {
    return coordinates.getExterior();
  }

  /**
   * Get the holes in this polygon.
   *
   * @return the possibly-empty list of holes
   */
  public List<List<Position>> getHoles() {
    return coordinates.getHoles();
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

    GeoPolygon polygon = (GeoPolygon) o;

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
    return "Polygon{" + "exterior=" + coordinates.getExterior()
        + (coordinates.getHoles().isEmpty() ? "" : ", holes=" + coordinates.getHoles()) + '}';
  }
}
