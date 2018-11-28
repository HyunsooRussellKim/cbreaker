package com.citus.controller;


import java.awt.geom.Arc2D;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.geotools.geometry.jts.JTSFactoryFinder;

import org.locationtech.jts.algorithm.Angle;
import org.locationtech.jts.algorithm.CGAlgorithms;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.CoordinateArrays;
import org.locationtech.jts.geom.CoordinateSequence;
import org.locationtech.jts.geom.CoordinateSequenceFactory;
import org.locationtech.jts.geom.CoordinateSequenceFilter;
import org.locationtech.jts.geom.Envelope;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryCollection;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineSegment;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.MultiLineString;
import org.locationtech.jts.geom.MultiPoint;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.locationtech.jts.geom.impl.CoordinateArraySequenceFactory;
import org.locationtech.jts.geom.impl.PackedCoordinateSequenceFactory;
import org.locationtech.jts.operation.linemerge.LineMerger;
import org.locationtech.jts.util.GeometricShapeFactory;


public final class JtsGeometryUtil {
	  public static final String FEATURE_PROPERTY = "feature";

	  private JtsGeometryUtil() {
	  }

	  @SuppressWarnings("unchecked")
	  public static <T extends Object> T getGeometryProperty(
	    final Geometry geometry, final String name) {
	    Map<String, Object> map = getGeometryProperties(geometry);
	    return (T)map.get(name);
	  }

	  @SuppressWarnings("unchecked")
	  public static Map<String, Object> getGeometryProperties(
	    final Geometry geometry) {
	    Object userData = geometry.getUserData();
	    if (userData instanceof Map) {
	      Map<String, Object> map = (Map<String, Object>)userData;
	      return map;
	    }
	    return Collections.emptyMap();
	  }

	  @SuppressWarnings("unchecked")
	  public static void setGeometryProperty(final Geometry geometry,
	    final String name, final Object value) {
	    Object userData = geometry.getUserData();
	    if (!(userData instanceof Map)) {
	      userData = new TreeMap<Object, Object>();
	      geometry.setUserData(userData);
	    }
	    Map<Object, Object> map = (Map<Object, Object>)userData;
	    map.put(name, value);

	  }

	  public static Polygon createPolygon(final MultiLineString multiLine) {
	    GeometryFactory factory = multiLine.getFactory();
	    Coordinate[] coordinates = getMergeLine(multiLine).getCoordinates();
	    LinearRing linearRing = factory.createLinearRing(coordinates);
	    Polygon polygon = factory.createPolygon(linearRing, null);
	    return polygon;

	  }

	  public static LineString getMergeLine(final MultiLineString multiLineString) {
	    Collection<LineString> lineStrings = getMergedLines(multiLineString);
	    int numLines = lineStrings.size();
	    if (numLines == 1) {
	      return (LineString)lineStrings.iterator().next();
	    } else {
	      return null;
	    }
	  }

	  @SuppressWarnings("unchecked")
	  public static Collection<LineString> getMergedLines(
	    final MultiLineString multiLineString) {
	    LineMerger merger = new LineMerger();
	    merger.add(multiLineString);
	    Collection<LineString> lineStrings = merger.getMergedLineStrings();
	    return lineStrings;
	  }

	  /**
	   * Merge two lines that share common coordinates at either the start or end.
	   * If the lines touch only at their start coordinates, the line2 will be
	   * reversed and joined before the start of line1. If the tow lines ouch only
	   * at their end coordinates, the line2 will be reversed and joined after the
	   * end of line1.
	   * 
	   * @param line1 The first line.
	   * @param line2 The second line.
	   * @return The new line string
	   */
	  public static LineString merge(final LineString line1, final LineString line2) {
	    CoordinateSequence coordinates = merge(line1.getCoordinateSequence(),
	      line2.getCoordinateSequence());
	    GeometryFactory factory = line1.getFactory();
	    LineString line = factory.createLineString(coordinates);
	    line.setUserData(line1.getUserData());
	    return line;
	  }

	  private static CoordinateSequence merge(final CoordinateSequence coordinates1,
	    final CoordinateSequence coordinates2) {
	    Coordinate[] coordinates = new Coordinate[coordinates1.size()
	      + coordinates2.size() - 1];
	    int numCoords = 0;
	    Coordinate coordinates1Start = coordinates1.getCoordinate(0);
	    Coordinate coordinates1End = coordinates1.getCoordinate(coordinates1.size() - 1);
	    Coordinate coordinates2Start = coordinates2.getCoordinate(0);
	    Coordinate coordinates2End = coordinates2.getCoordinate(coordinates2.size() - 1);
	    if (coordinates1Start.equals(coordinates2End)) {
	      numCoords = addCoordinates(coordinates2, coordinates, numCoords, null);
	      numCoords = addCoordinates(coordinates1, coordinates, numCoords,
	        coordinates[numCoords - 1]);
	    } else if (coordinates2Start.equals(coordinates1End)) {
	      numCoords = addCoordinates(coordinates1, coordinates, numCoords, null);
	      numCoords = addCoordinates(coordinates2, coordinates, numCoords,
	        coordinates[numCoords - 1]);
	    } else if (coordinates1Start.equals(coordinates2Start)) {
	      numCoords = addReversedCoordinates(coordinates2, coordinates, numCoords,
	        null);
	      numCoords = addCoordinates(coordinates1, coordinates, numCoords,
	        coordinates[numCoords - 1]);
	    } else if (coordinates1End.equals(coordinates2End)) {
	      numCoords = addCoordinates(coordinates1, coordinates, numCoords, null);
	      numCoords = addReversedCoordinates(coordinates2, coordinates, numCoords,
	        coordinates[numCoords - 1]);
	    } else {
	      throw new IllegalArgumentException("lines don't touch");

	    }

	    if (numCoords != coordinates.length) {
	      Coordinate[] newCoordinates = new Coordinate[numCoords];
	      System.arraycopy(coordinates, 0, newCoordinates, 0, numCoords);
	      coordinates = newCoordinates;
	    }

	    return CoordinateArraySequenceFactory.instance().create(coordinates);
	  }

	  public static int addCoordinates(final CoordinateSequence src, final Coordinate[] dest,
	    final int startIndex, final Coordinate lastCoordinate) {
	    Coordinate previousCoordinate = lastCoordinate;
	    int coordIndex = startIndex;
	    try {
	      for (int i = 0; i < src.size(); i++) {
	        Coordinate coordinate = src.getCoordinate(i);
	        if (!coordinate.equals(previousCoordinate)) {
	          dest[coordIndex++] = coordinate;
	        }
	        previousCoordinate = coordinate;
	      }
	    } catch (ArrayIndexOutOfBoundsException e) {
	      e.printStackTrace();
	    }
	    return coordIndex;
	  }

	  public static int addReversedCoordinates(final CoordinateSequence src,
	    final Coordinate[] dest, final int startIndex, final Coordinate lastCoordinate) {
	    Coordinate endCoordinate = lastCoordinate;
	    int coordIndex = startIndex;
	    try {
	      for (int i = src.size() - 1; i > -1; i--) {
	        Coordinate coordinate = src.getCoordinate(i);
	        if (!coordinate.equals(endCoordinate)) {
	          dest[coordIndex++] = coordinate;
	        }
	        endCoordinate = coordinate;
	      }
	    } catch (ArrayIndexOutOfBoundsException e) {
	      e.printStackTrace();
	    }
	    return coordIndex;
	  }

	  public static boolean equalsExact3D(final LineString line1, final  LineString line2) {
	    if (line1.getNumPoints() != line2.getNumPoints()) {
	      return false;
	    }
	    for (int i = 0; i < line1.getNumPoints(); i++) {
	      line1.getCoordinateN(i);
	      Coordinate coordinate1 = line1.getCoordinateN(i);
	      Coordinate coordinate2 = line2.getCoordinateN(i);
	      if (!coordinate1.equals3D(coordinate2)) {
	        return false;
	      }
	    }
	    return true;
	  }

	  public static boolean equalsExact3D(final Point point1, final Point point2) {
	    Coordinate coordinate1 = point1.getCoordinate();
	    Coordinate coordinate2 = point2.getCoordinate();

	    return (coordinate1.x == coordinate2.x) && (coordinate1.y == coordinate2.y)
	      && equalsZ(coordinate1.z, coordinate2.z);
	  }

	  public static boolean equalsZ(final double z1, final  double z2) {
	    if (z1 == z2) {
	      return true;
	    } else if (Double.isNaN(z1)) {
	      return (Double.isNaN(z2) || z2 == 0);
	    } else if (z1 == 0 && Double.isNaN(z2)) {
	      return true;
	    } else {
	      return false;
	    }

	  }

	  public static boolean equalsExact3D(final Geometry geometry1, final Geometry geometry2) {
	    if ((geometry1 instanceof LineString) && (geometry2 instanceof LineString)) {
	      LineString line1 = (LineString)geometry1;
	      LineString line2 = (LineString)geometry2;
	      return equalsExact3D(line1, line2);
	    } else if ((geometry1 instanceof Point) && (geometry2 instanceof Point)) {
	      Point point1 = (Point)geometry1;
	      Point point2 = (Point)geometry2;
	      return equalsExact3D(point1, point2);
	    } else if ((geometry1 instanceof MultiPoint)
	      && (geometry2 instanceof MultiPoint)) {
	      MultiPoint multiPoint1 = (MultiPoint)geometry1;
	      MultiPoint multiPoint2 = (MultiPoint)geometry2;
	      return equalsExact3D(multiPoint1, multiPoint2);
	    }
	    return false;
	  }

	  public static boolean equalsExact3D(final GeometryCollection collection1,
	    final GeometryCollection collection2) {
	    if (collection1.getNumGeometries() != collection2.getNumGeometries()) {
	      return false;
	    } else {
	      for (int i = 0; i < collection1.getNumGeometries(); i++) {
	        Geometry geometry1 = collection1.getGeometryN(i);
	        Geometry geometry2 = collection2.getGeometryN(i);
	        if (!equalsExact3D(geometry1, geometry2)) {
	          return false;
	        }
	      }
	    }
	    return true;
	  }

	  public static double distance(final Coordinate coordinate, final Geometry geometry) {
	    GeometryFactory factory = geometry.getFactory();
	    Point point = factory.createPoint(coordinate);
	    return point.distance(geometry);
	  }

	  public static void applyPrecisionModel(final Geometry geometry) {
	    PrecisionModel precisionModel = geometry.getPrecisionModel();
	    Coordinate[] coordinates = geometry.getCoordinates();
	    for (int i = 0; i < coordinates.length; i++) {
	      Coordinate coordinate = coordinates[i];
	      precisionModel.makePrecise(coordinate);
	    }

	  }

	  public static double getMiddleAngle(final double lastAngle,
	    final double angle, final int orientation) {
	    if (orientation == Angle.COUNTERCLOCKWISE) {
	      if (Double.isNaN(lastAngle)) {
	        return angle + Angle.PI_OVER_2;
	      } else if (Double.isNaN(angle)) {
	        return lastAngle + Angle.PI_OVER_2;
	      } else {
	        int turn = Angle.getTurn(lastAngle, angle);
	        double angleDiff = angle - lastAngle;
	        if (turn == Angle.CLOCKWISE) {
	          return lastAngle
	            - (Angle.PI_TIMES_2 - (Math.PI - Math.abs(angleDiff)) / 2);
	        } else {
	          return angle + (Math.PI - Math.abs(angleDiff)) / 2;
	        }

	      }
	    } else {
	      if (Double.isNaN(lastAngle)) {
	        return angle - Angle.PI_OVER_2;
	      } else if (Double.isNaN(angle)) {
	        return lastAngle - Angle.PI_OVER_2;
	      } else {
	        int turn = Angle.getTurn(lastAngle, angle);
	        double angleDiff = angle - lastAngle;
	        if (turn == Angle.CLOCKWISE) {
	          return angle - (Math.PI - Math.abs(angleDiff)) / 2;
	        } else {
	          return lastAngle
	            + (Angle.PI_TIMES_2 - (Math.PI - Math.abs(angleDiff)) / 2);
	        }
	      }
	    }

	  }

	  public static LineString subLineString(final LineString line, final int length) {
	    CoordinateSequence coords = line.getCoordinateSequence();
	    CoordinateSequence newCoords = subSequence(coords, 0, length);
	    GeometryFactory factory = line.getFactory();
	    return factory.createLineString(newCoords);
	  }

	  public static LineString subLineString(final LineString line,
	    final Coordinate fromCoordinate, final int fromIndex, final int toIndex,
	    final Coordinate toCoordinate) {
	    int numCoords = toIndex - fromIndex + 1;
	    int length = numCoords;
	    int offset = 0;
	    if (fromCoordinate != null) {
	      length++;
	      offset = 1;
	    }
	    if (toCoordinate != null) {
	      length++;
	    }
	    CoordinateSequence coords = line.getCoordinateSequence();
	    int dimension = coords.getDimension();
	    PackedCoordinateSequenceFactory doubleFactory = PackedCoordinateSequenceFactory.DOUBLE_FACTORY;
	    try {
	      CoordinateSequence newCoords = doubleFactory.create(length, dimension);
	      if (fromCoordinate != null) {
	        setCoordinate(newCoords, 0, fromCoordinate);
	      }
	      copyCoords(coords, fromIndex, newCoords, offset, numCoords);
	      if (toCoordinate != null) {
	        try {
	          setCoordinate(newCoords, length - 1, toCoordinate);
	        } catch (ArrayIndexOutOfBoundsException e) {
	          e.printStackTrace();
	        }
	      }
	      GeometryFactory factory = line.getFactory();
	      return factory.createLineString(newCoords);
	    } catch (NegativeArraySizeException e) {
	      e.printStackTrace();
	      throw e;
	    }
	  }

	  public static LineString subLineString(final LineString line,
	    final int length, final Coordinate coordinate) {
	    CoordinateSequence coords = line.getCoordinateSequence();
	    int dimension = coords.getDimension();
	    PackedCoordinateSequenceFactory doubleFactory = PackedCoordinateSequenceFactory.DOUBLE_FACTORY;
	    CoordinateSequence newCoords = doubleFactory.create(length + 1, dimension);
	    copyCoords(coords, 0, newCoords, 0, length);
	    setCoordinate(newCoords, length, coordinate);
	    GeometryFactory factory = line.getFactory();
	    return factory.createLineString(newCoords);
	  }

	  public static LineString createLineString(final GeometryFactory factory,
	    final Coordinate coordinate, final double angle,
	    final double lengthBackward, final double lengthForward) {
	    Coordinate c1 = new Coordinate(coordinate.x - lengthBackward
	      * Math.cos(angle), coordinate.y - lengthBackward * Math.sin(angle));
	    Coordinate c2 = new Coordinate(coordinate.x + lengthForward
	      * Math.cos(angle), coordinate.y + lengthForward * Math.sin(angle));
	    LineString line = factory.createLineString(new Coordinate[] {
	      c1, c2
	    });
	    return line;
	  }

	  public static LineString createParallelLineString(final LineString line,
	    final int orientation, final double distance) {
	    GeometryFactory factory = line.getFactory();
	    CoordinateSequence coordinates = line.getCoordinateSequence();
	    List<Coordinate> newCoordinates = new ArrayList<Coordinate>();
	    Coordinate coordinate = coordinates.getCoordinate(0);
	    LineSegment lastLineSegment = null;
	    int coordinateCount = coordinates.size();
	    for (int i = 0; i < coordinateCount; i++) {
	      Coordinate nextCoordinate = null;
	      LineSegment lineSegment = null;
	      if (i < coordinateCount - 1) {
	        nextCoordinate = coordinates.getCoordinate(i + 1);
	        lineSegment = new LineSegment(coordinate, nextCoordinate);
	        lineSegment = offset(lineSegment, distance, orientation);
	        // if (lastLineSegment == null) {
	        // lineSegment = addLength(lineSegment, 0, distance*2);
	        // } else if (i == coordinateCount - 2) {
	        // lineSegment = addLength(lineSegment, distance*2, 0);
	        // } else {
	        // lineSegment = addLength(lineSegment, distance*2, distance*2);
	        // }
	      }
	      if (lineSegment == null) {
	        newCoordinates.add(lastLineSegment.p1);
	      } else if (lastLineSegment == null) {
	        newCoordinates.add(lineSegment.p0);
	      } else {
	        Coordinate intersection = lastLineSegment.intersection(lineSegment);
	        if (intersection != null) {
	          newCoordinates.add(intersection);
	        } else {
	          // newCoordinates.add(lastLineSegment.p1);
	          newCoordinates.add(lineSegment.p0);
	        }
	      }

	      coordinate = nextCoordinate;
	      lastLineSegment = lineSegment;
	    }
	    CoordinateSequence newCoords = PackedCoordinateSequenceFactory.DOUBLE_FACTORY.create(newCoordinates.toArray(new Coordinate[0]));
	    return factory.createLineString(newCoords);
	  }

	  public static LineSegment offset(final LineSegment line,
	    final double distance, final int orientation) {
	    double angle = line.angle();
	    if (orientation == Angle.CLOCKWISE) {
	      angle -= Angle.PI_OVER_2;
	    } else {
	      angle += Angle.PI_OVER_2;
	    }
	    Coordinate c1 = offset(line.p0, angle, distance);
	    Coordinate c2 = offset(line.p1, angle, distance);
	    return new LineSegment(c1, c2);
	  }

	  public static Coordinate offset(final Coordinate coordinate,
	    final double angle, final double distance) {
	    double newX = coordinate.x + distance * Math.cos(angle);
	    double newY = coordinate.y + distance * Math.sin(angle);
	    Coordinate newCoordinate = new Coordinate(newX, newY);
	    return newCoordinate;

	  }

	  public static LineSegment addLength(final LineSegment line,
	    final double startDistance, final double endDistance) {
	    double angle = line.angle();
	    Coordinate c1 = offset(line.p0, angle, -startDistance);
	    Coordinate c2 = offset(line.p1, angle, endDistance);
	    return new LineSegment(c1, c2);

	  }

	  /**
	   * Add a evelation (z) value for a coordinate that is on a line segment.
	   * 
	   * @param coordinate The Coordinate.
	   * @param line The line segment the coordinate is on.
	   */
	  public static void addElevation(final Coordinate coordinate,
	    final LineSegment line) {
	    double z = getElevation(line, coordinate);
	    coordinate.z = z;
	  }

	  public static double getElevation(final LineSegment line,
	    final Coordinate coordinate) {
	    Coordinate c0 = line.p0;
	    Coordinate c1 = line.p1;
	    double fraction = coordinate.distance(c0) / line.getLength();
	    double z = c0.z + (c1.z - c0.z) * (fraction);
	    return z;
	  }

	  public static CoordinateSequence subSequence(final CoordinateSequence coords,
	    final int start, final int length) {
	    int dimension = coords.getDimension();
	    PackedCoordinateSequenceFactory factory = PackedCoordinateSequenceFactory.DOUBLE_FACTORY;
	    CoordinateSequence newCoords = factory.create(length, dimension);
	    copyCoords(coords, start, newCoords, 0, length);
	    return newCoords;
	  }

	  /*
	  public static void visitLineSegments(final CoordinateSequence coords,
	    final LineSegmentVistor visitor) {
	    Coordinate previousCoordinate = coords.getCoordinate(0);
	    for (int i = 1; i < coords.size(); i++) {
	      Coordinate coordinate = coords.getCoordinate(i);
	      LineSegment segment = new LineSegment(previousCoordinate, coordinate);
	      if (segment.getLength() > 0) {
	        if (!visitor.visit(segment)) {
	          return;
	        }
	      }
	      previousCoordinate = coordinate;
	    }
	  }
		*/

	  /*
	  public static void addElevation(final PrecisionModel precisionModel,
	    final Coordinate coordinate, final LineSegment3D line) {
	    addElevation(coordinate, line);
	    coordinate.z = precisionModel.makePrecise(coordinate.z);

	  }
	  */

	  public static void makePrecise(final PrecisionModel precision,
	    final Geometry geometry) {
	    geometry.apply(new CoordinateSequenceFilter() {
	      public void filter(final CoordinateSequence coordinates, final int index) {
	        for (int i = 0; i < coordinates.getDimension(); i++) {
	          double ordinate = coordinates.getOrdinate(index, i);
	          double preciseOrdinate = precision.makePrecise(ordinate);
	          coordinates.setOrdinate(index, i, preciseOrdinate);
	        }
	      }

	      public boolean isDone() {
	        return false;
	      }

	      public boolean isGeometryChanged() {
	        return true;
	      }

	    });
	  }

	  public static Envelope buffer(final Envelope envelope, final int i) {
	    return new Envelope(envelope.getMinX() - i, envelope.getMaxX() + i,
	      envelope.getMinY() - i, envelope.getMaxY() + i);
	  }

	  /**
	   * Computes whether a ring defined by an array of {@link Coordinate} is
	   * oriented counter-clockwise.
	   * <ul>
	   * <li>The list of points is assumed to have the first and last points equal.
	   * <li>This will handle coordinate lists which contain repeated points.
	   * </ul>
	   * This algorithm is <b>only</b> guaranteed to work with valid rings. If the
	   * ring is invalid (e.g. self-crosses or touches), the computed result <b>may</b>
	   * not be correct.
	   * 
	   * @param ring an array of coordinates forming a ring
	   * @return <code>true</code> if the ring is oriented counter-clockwise.
	   */
	  public static boolean isCCW(final CoordinateSequence ring) {
	    // # of points without closing endpoint
	    int nPts = ring.size() - 1;

	    // find highest point
	    Coordinate hiPt = ring.getCoordinate(0);
	    int hiIndex = 0;
	    for (int i = 1; i <= nPts; i++) {
	      Coordinate p = ring.getCoordinate(i);
	      if (p.y > hiPt.y) {
	        hiPt = p;
	        hiIndex = i;
	      }
	    }

	    // find distinct point before highest point
	    int iPrev = hiIndex;
	    do {
	      iPrev = iPrev - 1;
	      if (iPrev < 0) {
	        iPrev = nPts;
	      }
	    } while (ring.getCoordinate(iPrev).equals2D(hiPt) && iPrev != hiIndex);

	    // find distinct point after highest point
	    int iNext = hiIndex;
	    do {
	      iNext = (iNext + 1) % nPts;
	    } while (ring.getCoordinate(iNext).equals2D(hiPt) && iNext != hiIndex);

	    Coordinate prev = ring.getCoordinate(iPrev);
	    Coordinate next = ring.getCoordinate(iNext);

	    /**
	     * This check catches cases where the ring contains an A-B-A configuration
	     * of points. This can happen if the ring does not contain 3 distinct points
	     * (including the case where the input array has fewer than 4 elements), or
	     * it contains coincident line segments.
	     */
	    if (prev.equals2D(hiPt) || next.equals2D(hiPt) || prev.equals2D(next)) {
	      return false;
	    }

	    int disc = CGAlgorithms.computeOrientation(prev, hiPt, next);

	    /**
	     * If disc is exactly 0, lines are collinear. There are two possible cases:
	     * (1) the lines lie along the x axis in opposite directions (2) the lines
	     * lie on top of one another (1) is handled by checking if next is left of
	     * prev ==> CCW (2) will never happen if the ring is valid, so don't check
	     * for it (Might want to assert this)
	     */
	    boolean isCCW = false;
	    if (disc == 0) {
	      // poly is CCW if prev x is right of next x
	      isCCW = (prev.x > next.x);
	    } else {
	      // if area is positive, points are ordered CCW
	      isCCW = (disc > 0);
	    }
	    return isCCW;
	  }

	  /*
	  public static void addElevation(final Coordinate coordinate, final LineString line) {
	    CoordinateSequence coordinates = line.getCoordinateSequence();
	    Coordinate previousCoordinate = coordinates.getCoordinate(0);
	    for (int i = 1; i < coordinates.size(); i++) {
	      Coordinate currentCoordinate = coordinates.getCoordinate(i);
	      LineSegment3D segment = new LineSegment3D(previousCoordinate,
	        currentCoordinate);
	      if (segment.distance(coordinate) < 1) {
	        PrecisionModel precisionModel = line.getFactory().getPrecisionModel();
	        addElevation(precisionModel, coordinate, segment);
	        return;
	      }
	      previousCoordinate = currentCoordinate;
	    }

	  }
	  */

	  /*
	  public static Geometry difference2DZ(final LineString line, final Geometry geometry) {
	    Geometry difference = line.difference(geometry);
	    if (difference instanceof LineString) {
	      LineString lineDiff = (LineString)difference;
	      Coordinate c0 = lineDiff.getCoordinateN(0);
	      if (Double.isNaN(c0.z)) {
	        addElevation(c0, line);
	      }
	      Coordinate cN = lineDiff.getCoordinateN(lineDiff.getNumPoints() - 1);
	      if (Double.isNaN(cN.z)) {
	        addElevation(cN, line);
	      }

	    }
	    difference.setUserData(line.getUserData());
	    return difference;
	  }
	  */

	  /**
	   * Change to a floating precision model to calculate the intersection. This
	   * reduces the chance of lines being returned instead of points where there is
	   * a sharp angle
	   * 
	   * @param line1
	   * @param line2
	   * @return
	   */
	  /*
	  public static Geometry intersection2DZFloating(final LineString line1,
	    final LineString line2) {
	    GeometryFactory factory = new GeometryFactory();
	    CoordinateSequence coordinates1 = line1.getCoordinateSequence();
	    LineString line1Floating = factory.createLineString(coordinates1);
	    CoordinateSequence coordinates2 = line2.getCoordinateSequence();
	    LineString line2Floating = factory.createLineString(coordinates2);
	    return JtsGeometryUtil.intersection2DZ(line1Floating, line2Floating);
	  }
	  */

	  /*
	  public static Geometry intersection2DZ(final LineString line, final Geometry geometry) {
	    Geometry intersection = line.intersection(geometry);
	    if (intersection instanceof LineString) {
	      LineString lineDiff = (LineString)intersection;
	      addElevation(line, lineDiff);

	    } else {
	      if (intersection instanceof MultiLineString) {
	        for (int i = 0; i < intersection.getNumGeometries(); i++) {
	          LineString lineDiff = (LineString)intersection.getGeometryN(i);
	          addElevation(line, lineDiff);
	        }
	      }
	    }
	    intersection.setUserData(line.getUserData());
	    return intersection;
	  }
	  */

	  /*
	  private static void addElevation(final LineString original, final LineString update) {
	    Coordinate c0 = update.getCoordinateN(0);
	    if (Double.isNaN(c0.z)) {
	      addElevation(c0, original);
	    }
	    Coordinate cN = update.getCoordinateN(update.getNumPoints() - 1);
	    if (Double.isNaN(cN.z)) {
	      addElevation(cN, original);
	    }
	  }
	  */

	  /*
	  public static Polygon getMitredBuffer(final Polygon polygon,
	    final double distance) {
	    Geometry buffer = polygon;
	    LineString exteriorRing = polygon.getExteriorRing();
	    Geometry exteriorBuffer = getMitredBuffer(exteriorRing, distance);
	    buffer = buffer.union(exteriorBuffer);
	    for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
	      LineString ring = polygon.getInteriorRingN(i);
	      Geometry bufferedRing = getMitredBuffer(ring, distance);
	      buffer = buffer.union(bufferedRing);
	    }
	    return (Polygon)buffer;
	  }
	  */

	  /*
	  public static Polygon getMitredBuffer(final LineString lineString,
	    final double distance) {
	    LineStringMitredBuffer visitor = new LineStringMitredBuffer(distance);
	    visitLineSegments(lineString.getCoordinateSequence(), visitor);
	    return visitor.getBuffer();
	  }
	  */

	  public static Polygon getMitredBuffer(final LineSegment segment,
	    final double distance) {

	    LineSegment extendedSegment = addLength(segment, distance, distance);
	    LineSegment clockwiseSegment = offset(extendedSegment, distance,
	      Angle.CLOCKWISE);
	    LineSegment counterClockwiseSegment = offset(extendedSegment, distance,
	      Angle.COUNTERCLOCKWISE);

	    Coordinate[] coords = new Coordinate[] {
	      clockwiseSegment.p0, clockwiseSegment.p1, counterClockwiseSegment.p1,
	      counterClockwiseSegment.p0, clockwiseSegment.p0
	    };
	    GeometryFactory factory = new GeometryFactory();
	    LinearRing exteriorRing = factory.createLinearRing(coords);
	    return factory.createPolygon(exteriorRing, null);
	  }

	  /**
	   * @param line1 The line to match
	   * @param line2 The line to compare the start of with the other line
	   * @return
	   */
	  public static boolean touchesAtStart(final LineString line1,
	    final LineString line2) {
	    Coordinate l1c0 = line1.getCoordinateN(0);
	    Coordinate l1cN = line1.getCoordinateN(line1.getNumPoints() - 1);
	    Coordinate l2c0 = line2.getCoordinateN(0);
	    if (l2c0.equals2D(l1c0)) {
	      return true;
	    } else {
	      return l2c0.equals2D(l1cN);
	    }
	  }

	  /**
	   * @param line1 The line to match
	   * @param line2 The line to compare the start of with the other line
	   * @return
	   */
	  public static boolean touchesAtEnd(final LineString line1,
	    final LineString line2) {
	    Coordinate l1c0 = line1.getCoordinateN(0);
	    Coordinate l1cN = line1.getCoordinateN(line1.getNumPoints() - 1);
	    Coordinate l2cN = line2.getCoordinateN(line2.getNumPoints() - 1);
	    if (l2cN.equals2D(l1c0)) {
	      return true;
	    } else {
	      return l2cN.equals2D(l1cN);
	    }
	  }

	  public static boolean startAndEndEqual(final LineString geometry1,
	    final LineString geometry2) {
	    Coordinate g1c0 = geometry1.getCoordinateN(0);
	    Coordinate g1cN = geometry1.getCoordinateN(geometry1.getNumPoints() - 1);
	    Coordinate g2c0 = geometry2.getCoordinateN(0);
	    Coordinate g2cN = geometry2.getCoordinateN(geometry2.getNumPoints() - 1);
	    if (g1c0.equals2D(g2c0)) {
	      return g1cN.equals2D(g2cN);
	    } else if (g1c0.equals2D(g2cN)) {
	      return g1cN.equals2D(g2c0);
	    } else {
	      return false;
	    }
	  }

	  public static boolean isBothWithinDistance(final LineString line1,
	    final LineString line2, final double maxDistance) {
	    if (isWithinDistance(line1, line2, maxDistance)) {
	      return isWithinDistance(line2, line1, maxDistance);
	    } else {
	      return false;
	    }
	  }

	  public static boolean isWithinDistance(final LineString line1,
	    final LineString line2, final double maxDistance) {
	    CoordinateSequence coordinates1 = line1.getCoordinateSequence();
	    for (int i = 0; i < coordinates1.size(); i++) {
	      Coordinate coordinate = coordinates1.getCoordinate(i);
	      if (!isWithinDistance(coordinate, line2, maxDistance)) {
	        return false;
	      }
	    }
	    return true;
	  }

	  public static boolean isWithinDistance(final Coordinate coordinate,
	    final LineString line, final double maxDistance) {
	    GeometryFactory factory = line.getFactory();
	    Point point = factory.createPoint(coordinate);
	    double distance = line.distance(point);
	    return distance <= maxDistance;
	  }

	  /*
	  public static Polygon reversePolygon(final Polygon polygon) {
	    GeometryFactory factory = polygon.getFactory();
	    LineString exteriorRing = polygon.getExteriorRing();
	    CoordinateSequence oldCoordinates = exteriorRing.getCoordinateSequence();
	    ReverseCoordinateSequence newCoordinates = new ReverseCoordinateSequence(
	      oldCoordinates);
	    LinearRing shell = factory.createLinearRing(newCoordinates);
	    return factory.createPolygon(shell, null);
	  }
	  */

	  public static Geometry get2DGeometry(final Geometry geometry) {
	    GeometryFactory factory = geometry.getFactory();
	    if (geometry instanceof Point) {
	      Point point = (Point)geometry;
	      return factory.createPoint(get2DCoordinates(point.getCoordinateSequence()));
	    } else if (geometry instanceof LineString) {
	      LineString line = (LineString)geometry;
	      return factory.createLineString(get2DCoordinates(line.getCoordinateSequence()));
	    } else if (geometry instanceof Polygon) {

	      Polygon polygon = (Polygon)geometry;
	      LinearRing shell = (LinearRing)polygon.getExteriorRing();
	      LinearRing shell2d = get2DGeometry(shell);
	      LinearRing[] holes2d = new LinearRing[polygon.getNumInteriorRing()];
	      for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
	        LinearRing hole = (LinearRing)polygon.getInteriorRingN(i);
	        holes2d[i] = get2DGeometry(hole);
	      }

	      return factory.createPolygon(shell2d, holes2d);
	    }
	    return null;
	  }

	  public static LinearRing get2DGeometry(final LinearRing ring) {
	    CoordinateSequence coordinates = ring.getCoordinateSequence();
	    CoordinateSequence coordinates2d = get2DCoordinates(coordinates);
	    GeometryFactory factory = ring.getFactory();
	    return factory.createLinearRing(coordinates2d);
	  }

	  private static CoordinateSequence get2DCoordinates(
	    final CoordinateSequence coordinateSequence) {
	    CoordinateSequenceFactory coordFactory = PackedCoordinateSequenceFactory.DOUBLE_FACTORY;
	    int numCoords = coordinateSequence.size();
	    CoordinateSequence coordinates = coordFactory.create(numCoords, 2);
	    for (int i = 0; i < numCoords; i++) {
	      double x = coordinateSequence.getX(i);
	      double y = coordinateSequence.getY(i);
	      coordinates.setOrdinate(i, 0, x);
	      coordinates.setOrdinate(i, 1, y);
	    }
	    return coordinates;
	  }

	  /**
	   * Insert the coordinate at the specified index into the line, returning the
	   * new line.
	   * 
	   * @param line The line.
	   * @param index The index to insert the coordinate.
	   * @param coordinate The coordinate.
	   */
	  public static LineString insert(final LineString line, final int index,
	    final Coordinate coordinate) {
	    CoordinateSequence coords = line.getCoordinateSequence();
	    CoordinateSequence newCoords = PackedCoordinateSequenceFactory.DOUBLE_FACTORY.create(
	      coords.size() + 1, coords.getDimension());
	    int j = 0;
	    for (int i = 0; i < newCoords.size(); i++) {
	      if (i == index) {
	        newCoords.setOrdinate(i, 0, coordinate.x);
	        newCoords.setOrdinate(i, 1, coordinate.y);
	        if (newCoords.getDimension() > 2) {
	          newCoords.setOrdinate(i, 2, coordinate.z);
	        }
	      } else {
	        for (int o = 0; o < newCoords.getDimension(); o++) {
	          newCoords.setOrdinate(i, o, coords.getOrdinate(j, o));
	        }
	        j++;
	      }
	    }
	    GeometryFactory factory = line.getFactory();
	    LineString newLine = factory.createLineString(newCoords);
	    return newLine;
	  }

	  public static List<LineString> split(final LineString line, final int index,
	    final Coordinate coordinate) {
	    List<LineString> lines = new ArrayList<LineString>();
	    boolean containsCoordinate = coordinate.equals(line.getCoordinateN(index));
	    CoordinateSequence coords = line.getCoordinateSequence();
	    int dimension = coords.getDimension();
	    int coords1Size;
	    int coords2Size = coords.size() - index;
	    if (containsCoordinate) {
	      coords1Size = index + 1;
	      coords2Size = coords.size() - index;
	    } else {
	      coords1Size = index + 2;
	      coords2Size = coords.size() - index;
	    }
	    CoordinateSequence coords1 = PackedCoordinateSequenceFactory.DOUBLE_FACTORY.create(
	      coords1Size, dimension);
	    copyCoords(coords, 0, coords1, 0, index + 1);
	    if (!containsCoordinate) {
	      setCoordinate(coords1, coords1Size - 1, coordinate);
	    }

	    CoordinateSequence coords2 = PackedCoordinateSequenceFactory.DOUBLE_FACTORY.create(
	      coords2Size, dimension);
	    if (!containsCoordinate) {
	      setCoordinate(coords2, 0, coordinate);
	      copyCoords(coords, index + 1, coords2, 1, coords2.size() - 1);
	    } else {
	      copyCoords(coords, index, coords2, 0, coords2.size());
	    }

	    GeometryFactory geometryFactory = line.getFactory();

	    if (coords1Size > 1) {
	      LineString line1 = geometryFactory.createLineString(coords1);
	      if (line1.getLength() > 0) {
	        lines.add(line1);
	      }
	    }

	    if (coords2Size > 1) {
	      LineString line2 = geometryFactory.createLineString(coords2);
	      if (line2.getLength() > 0) {
	        lines.add(line2);
	      }
	    }
	    return lines;
	  }

	  public static void copyCoords(final CoordinateSequence src, final int srcPos,
	    final CoordinateSequence dest, final int destPos, final  int length) {
	    int dimension = Math.min(src.getDimension(), dest.getDimension());
	    for (int i = 0; i < length; i++) {
	      for (int j = 0; j < dimension; j++) {
	        double ordinate = src.getOrdinate(srcPos + i, j);
	        dest.setOrdinate(destPos + i, j, ordinate);
	      }
	    }
	  }

	  public static void setCoordinate(final CoordinateSequence coordinates,
	    final int i, final Coordinate coordinate) {
	    coordinates.setOrdinate(i, 0, coordinate.x);
	    coordinates.setOrdinate(i, 1, coordinate.y);
	    if (coordinates.getDimension() > 2) {
	      coordinates.setOrdinate(i, 2, coordinate.z);
	    }

	  }

	  public static boolean isAlmostParallel(final LineString line,
	    final LineString matchLine, final double maxDistance) {
	    CoordinateSequence coords = line.getCoordinateSequence();
	    CoordinateSequence matchCoords = line.getCoordinateSequence();
	    Coordinate previousCoordinate = coords.getCoordinate(0);
	    for (int i = 1; i < coords.size(); i++) {
	      Coordinate coordinate = coords.getCoordinate(i);
	      Coordinate previousMatchCoordinate = matchCoords.getCoordinate(0);
	      for (int j = 1; j < coords.size(); j++) {
	        Coordinate matchCoordinate = matchCoords.getCoordinate(i);
	        double distance = CGAlgorithms.distanceLineLine(previousCoordinate,
	          coordinate, previousMatchCoordinate, matchCoordinate);
	        if (distance <= maxDistance) {
	          double angle1 = Angle.normalizePositive(Angle.angle(
	            previousCoordinate, coordinate));
	          double angle2 = Angle.normalizePositive(Angle.angle(
	            previousMatchCoordinate, matchCoordinate));
	          double angleDiff = Math.abs(angle1 - angle2);
	          if (angleDiff <= Math.PI / 6) {
	            return true;
	          }
	        }
	        previousMatchCoordinate = matchCoordinate;
	      }
	      previousCoordinate = coordinate;
	    }
	    return false;
	  }

	  public static LineString getMatchingLines(final LineString line1,
	    final LineString line2, final double maxDistance) {
	    List<Coordinate> newCoords = new ArrayList<Coordinate>();
	    CoordinateSequence coords1 = line1.getCoordinateSequence();
	    CoordinateSequence coords2 = line1.getCoordinateSequence();
	    Coordinate previousCoordinate = coords1.getCoordinate(0);
	    boolean finish = false;
	    for (int i = 1; i < coords1.size() && !finish; i++) {
	      Coordinate coordinate = coords1.getCoordinate(i);
	      Coordinate previousCoordinate2 = coords2.getCoordinate(0);
	      for (int j = 1; j < coords1.size() && !finish; j++) {
	        Coordinate coordinate2 = coords2.getCoordinate(i);
	        double distance = CGAlgorithms.distanceLineLine(previousCoordinate,
	          coordinate, previousCoordinate2, coordinate2);
	        if (distance > maxDistance) {
	          finish = true;
	        }
	        previousCoordinate2 = coordinate2;
	      }
	      previousCoordinate = coordinate;
	    }
	    if (newCoords.size() > 1) {
	      return createLineString(line1.getFactory(), newCoords);
	    } else {
	      return null;
	    }
	  }

	  public static LineString createLineString(final GeometryFactory factory,
	    final List<Coordinate> coordinates) {
	    Coordinate[] coords = new Coordinate[coordinates.size()];
	    coordinates.toArray(coords);
	    return factory.createLineString(coords);

	  }

	  public static int[] findClosestSegmentAndCoordinate(final LineString line,
	    final Coordinate coordinate) {
	    int[] closest = new int[] {
	      -1, -1, 0
	    };
	    double closestDistance = Double.MAX_VALUE;
	    CoordinateSequence coordinates = line.getCoordinateSequence();
	    Coordinate previousCoord = coordinates.getCoordinate(0);
	    double previousCoordinateDistance = previousCoord.distance(coordinate);
	    if (previousCoordinateDistance == 0) {
	      closest[0] = 0;
	      closest[1] = 0;
	      closest[2] = 1;
	    } else {
	      for (int i = 1; i < coordinates.size(); i++) {
	        Coordinate currentCoordinate = coordinates.getCoordinate(i);
	        double currentCoordinateDistance = currentCoordinate.distance(coordinate);
	        if (currentCoordinateDistance == 0) {
	          closest[0] = i;
	          closest[1] = i;
	          closest[2] = 1;
	          return closest;
	        }
	        LineSegment lineSegment = new LineSegment(previousCoord,
	          currentCoordinate);
	        double distance = lineSegment.distance(coordinate);
	        if (distance == 0) {
	          closest[0] = i - 1;
	          if (previousCoordinateDistance < currentCoordinateDistance) {
	            closest[1] = i - 1;
	          } else {
	            closest[1] = i;
	          }
	          return closest;
	        } else if (distance < closestDistance) {
	          closestDistance = distance;
	          closest[0] = i - 1;
	          if (previousCoordinateDistance < currentCoordinateDistance) {
	            closest[1] = i - 1;
	          } else {
	            closest[1] = i;
	          }
	        }
	        previousCoord = currentCoordinate;
	      }
	    }
	    return closest;
	  }

	  public static List<LineString> splitLineString(final LineString line,
	    final Coordinate coordinate) {
	    int[] indexes = findClosestSegmentAndCoordinate(line, coordinate);
	    int segmentIndex = indexes[0];
	    if (segmentIndex != -1) {
	      int coordinateIndex = indexes[1];
	      boolean exactMatch = coordinateIndex == 1;
	      if (coordinateIndex == 0) {
	        if (exactMatch) {
	          return Collections.singletonList(line);
	        } else {
	          Coordinate c0 = line.getCoordinateN(0);
	          Coordinate c1;
	          int i = 1;
	          do {
	            c1 = line.getCoordinateN(i);
	            i++;
	          } while (c1.equals(c0));

	          if (Angle.isAcute(c1, c0, coordinate)) {
	            Coordinate projectedCoordinate = new LineSegment(c0, c1).project(coordinate);
	            return split(line, 1, projectedCoordinate);
	          } else {
	            return Collections.singletonList(line);
	          }
	        }
	      } else if (coordinateIndex == line.getNumPoints() - 1) {
	        if (exactMatch) {
	          return Collections.singletonList(line);
	        } else {
	          Coordinate cn = line.getCoordinateN(line.getNumPoints() - 1);
	          Coordinate cn1;
	          int i = line.getNumPoints() - 2;
	          do {
	            cn1 = line.getCoordinateN(i);
	            i++;
	          } while (cn1.equals(cn));
	          if (Angle.isAcute(cn1, cn, coordinate)) {
	            Coordinate projectedCoordinate = new LineSegment(cn, cn1).project(coordinate);
	            return split(line, line.getNumPoints() - 1, projectedCoordinate);
	          } else {
	            return Collections.singletonList(line);
	          }
	        }
	      } else {
	        Coordinate c = line.getCoordinateN(segmentIndex);
	        Coordinate c1;
	        int i = segmentIndex + 1;
	        do {
	          c1 = line.getCoordinateN(i);
	          i++;
	        } while (c.equals(c1));
	        Coordinate projectedCoordinate = new LineSegment(c, c1).project(coordinate);
	        return split(line, segmentIndex, projectedCoordinate);
	      }
	    } else {
	      return Collections.emptyList();
	    }
	  }
	  
		/**
		 * @param center
		 * @param start
		 * @param end
		 * @return
		 */
		public static Geometry createSemiCircle(Coordinate center, Coordinate start, Coordinate end) {
			double r = Math.sqrt((start.x - center.x) * (start.x - center.x) + (start.y - center.y) * (start.y - center.y));
			double x = center.x - r;
			double y = center.y - r;
			double width = 2 * r;
			double height = 2 * r;
			double startAngle = (180 / Math.PI * Math.atan2(start.y - center.y, start.x - center.x));
			double endAngle = (180 / Math.PI * Math.atan2(end.y - center.y, end.x - center.x));		
			
			Arc2D arc = new Arc2D.Double(x, y, width, height, startAngle, endAngle,  Arc2D.OPEN);
			//arc.setAngles(start.x, start.y, end.x, end.y);
			
			PathIterator iter = arc.getPathIterator(null);
			
			double[] iterBuf = new double[6];
			
			List<Coordinate> coords = new ArrayList<Coordinate>();
			while (!iter.isDone()) {
				iter.currentSegment(iterBuf);
				coords.add(new Coordinate(iterBuf[0], iterBuf[1]));
				iter.next();
			}
			
			return new GeometryFactory().createLineString(coords.toArray(new Coordinate[coords.size()]));
		}	
		
		/**
		 * @param center
		 * @param start
		 * @param end
		 * @param radius
		 * @param direction true:right(상월), false:left(하월)
		 * @return
		 */
		public static Geometry createArc(Coordinate center, Coordinate start, Coordinate end, double radius, boolean direction) {
			LineString arc;
			GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
			shapeFactory.setNumPoints(32);
			shapeFactory.setCentre(center);
			shapeFactory.setSize(radius * 2);
			double startAngle = Math.atan2(start.y - center.y, start.x - center.x);	// in radian
			double endAngle = Math.atan2(end.y - center.y, end.x - center.x);	// in radian
			if(direction) arc = (LineString)(shapeFactory.createArc(endAngle, Math.PI).reverse());		// 상월, Math.PI = 180 in degree
			else arc = shapeFactory.createArc(startAngle, Math.PI);		// 하월 Math.PI = 180 in degree
			return arc;
		}
		
		/**
		 * @param center
		 * @param RADIUS
		 * @return
		 */
		public static Geometry createCircle(Coordinate center, final double RADIUS) {
			  GeometricShapeFactory shapeFactory = new GeometricShapeFactory();
			  shapeFactory.setNumPoints(32);
			  shapeFactory.setCentre(center);
			  shapeFactory.setSize(RADIUS * 2);
			  return shapeFactory.createCircle();
		} 
		
		/**
		 * @param coordinates
		 * @return
		 */
		public static Geometry createLine(Coordinate[] coordinates) {
			return new GeometryFactory().createLineString(coordinates);
		}
		
		/**
		 * @param coordinate
		 * @return
		 */
		public static Geometry createPoint(Coordinate coordinate) {
			return new GeometryFactory().createPoint(coordinate);
		}	  
	  
		/**
		 * Convert a MultiLineString to a LineString
		 * @param mls
		 * @return
		 */
		public static LineString convertMultiLineString2LineString(MultiLineString mls) {
			LineString newLine = null;
			try {
				if(mls instanceof MultiLineString) {
					if(mls.getNumGeometries()>1) {
						for (int i=0;i<mls.getNumGeometries();i++) {
						    if(i==1) newLine = makeLineStringUsingReverse((LineString)mls.getGeometryN(0), (LineString)mls.getGeometryN(1));
						    else if(i>1) newLine = makeLineStringUsingReverse(newLine, (LineString)mls.getGeometryN(i));
						}
					} else {
						newLine = (LineString)mls.getGeometryN(0);
					}
				}
			} catch(Exception e) {
			} finally{
			}
			return newLine;
		}
		
		/**
		 * 두개의 LineString을 가까운 점끼리 연결하여 하나의 LineString Return
		 * @param fromLine
		 * @param toLine
		 * @return LineString
		 */
		public static LineString makeLineStringUsingReverse(LineString fromLine, LineString toLine) {
			LineString lineString;
			
			GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(null);
			
			LineString[] lineStrings = new LineString[2];
			ArrayList<Coordinate> coordList = new ArrayList<Coordinate>();
			
			int M = fromLine.getNumPoints();
			int N = toLine.getNumPoints();
			
			Point fromLineStart = factory.createPoint(fromLine.getCoordinateN(0));
			Point fromLineEnd = factory.createPoint(fromLine.getCoordinateN(M-1));
			Point toLineStart = factory.createPoint(toLine.getCoordinateN(0));
			Point toLineEnd = factory.createPoint(toLine.getCoordinateN(N-1));

			double start2start = fromLineStart.distance(toLineStart);
			double start2end = fromLineStart.distance(toLineEnd);
			double end2start = fromLineEnd.distance(toLineStart);
			double end2end = fromLineEnd.distance(toLineEnd);
			
			List<Double> d = Arrays.asList(start2start, start2end, end2start, end2end);
			double min = Collections.min(d);
			if(min==start2start) {//first line reverse
				lineStrings[0] = (LineString)fromLine.reverse();
				lineStrings[1] = toLine;
			}
			else if (min==start2end) {//first, second line reverse
				lineStrings[0] = (LineString)fromLine.reverse();
				lineStrings[1] = (LineString)toLine.reverse();
			}
			else if (min==end2start) {//no reverse
				lineStrings[0] = fromLine;
				lineStrings[1] = toLine;
			}
			else if (min==end2end) {//second line reverse
				lineStrings[0] = fromLine;
				lineStrings[1] = (LineString)toLine.reverse();
			}
			
			for (int i=0; i<lineStrings.length;i++) {
			    int L = lineStrings[i].getNumPoints();
			    for ( int j=0;j<L;j++ ) {
			    	Coordinate co = lineStrings[i].getCoordinateN(j);
			    	coordList.add(co);
			    }
			}
			Coordinate[] coords = CoordinateArrays.toCoordinateArray(coordList);
			lineString = factory.createLineString(coords);
			
			return lineString;
		}
		
		/**
		 * Convert LineString[] to a LineString
		 * @param mls
		 * @return
		 */
		public static LineString convertLineStrings2LineString(LineString[] lines) {
			LineString ls = null;
			try {
				ArrayList<Coordinate> coordList = new ArrayList<Coordinate>();
				GeometryFactory factory = JTSFactoryFinder.getGeometryFactory(null);
				int N = lines.length;
				for ( int i = 0; i < N; i++ ) {
				    int M = lines[i].getNumPoints();
				    for ( int j = 0; j < M; j++ ) {
				    	Coordinate co = lines[i].getCoordinateN(j);
				    	coordList.add(co);
				    }
				}
				Coordinate[] coords = CoordinateArrays.toCoordinateArray(coordList);
				ls = factory.createLineString(coords);
			} catch(Exception e) {
			} finally{
			}
			return ls;
		}
	}

