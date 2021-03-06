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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Coordinates for a GeoJSON Polygon.
 * 
 * @author Michael Remme
 * 
 */
public class PolygonCoordinates {
  private final List<Position> exterior;
  private final List<List<Position>> holes;

  /**
   * Construct an instance.
   *
   * @param exterior the exterior ring of the polygon
   * @param holes    optional interior rings of the polygon
   */
  public PolygonCoordinates(final List<Position> exterior, final List<Position>... holes) {
    this.exterior = Collections.unmodifiableList(exterior);

    List<List<Position>> holesList = new ArrayList<>(holes.length);
    for (List<Position> hole : holes) {
      holesList.add(Collections.unmodifiableList(hole));
    }

    this.holes = Collections.unmodifiableList(holesList);
  }

  /**
   * Gets the exterior of the polygon.
   *
   * @return the exterior of the polygon
   */
  public List<Position> getExterior() {
    return exterior;
  }

  /**
   * Gets the holes in the polygon.
   *
   * @return the holes in the polygon, which will not be null but may be empty
   */
  public List<List<Position>> getHoles() {
    return holes;
  }

  @Override
  public boolean equals(final Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    PolygonCoordinates that = (PolygonCoordinates) o;

    if (!exterior.equals(that.exterior)) {
      return false;
    }
    if (!holes.equals(that.holes)) {
      return false;
    }

    return true;
  }

  @Override
  public int hashCode() {
    int result = exterior.hashCode();
    result = 31 * result + holes.hashCode();
    return result;
  }

  @Override
  public String toString() {
    return "PolygonCoordinates{" + "exterior=" + exterior + (holes.isEmpty() ? "" : ", holes=" + holes) + '}';
  }

}
