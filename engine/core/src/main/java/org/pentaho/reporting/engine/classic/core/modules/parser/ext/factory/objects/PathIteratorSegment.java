/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.objects;

import java.io.Serializable;

/**
 * This class represents a single segment in a path iterator.
 *
 * @author Thomas Morgner
 */
public class PathIteratorSegment implements Serializable {
  /**
   * The segment type, one of the PathIterator constants.
   */
  private int segmentType;
  /**
   * The x coordinate of the first parameter point.
   */
  private float x1;
  /**
   * The y coordinate of the first parameter point.
   */
  private float y1;
  /**
   * The x coordinate of the second parameter point.
   */
  private float x2;
  /**
   * The y coordinate of the second parameter point.
   */
  private float y2;
  /**
   * The x coordinate of the third parameter point.
   */
  private float x3;
  /**
   * The y coordinate of the third parameter point.
   */
  private float y3;

  /**
   * Default constructor.
   */
  public PathIteratorSegment() {
  }

  /**
   * Returns the segment type for this PathIterator segment.
   *
   * @return the type of the segment, one of the predefined constants of the class PathIterator.
   */
  public int getSegmentType() {
    return segmentType;
  }

  /**
   * Defines the segment type for this PathIterator segment.
   *
   * @param segmentType
   *          the type of the segment, one of the predefined constants of the class PathIterator.
   */
  public void setSegmentType( final int segmentType ) {
    this.segmentType = segmentType;
  }

  /**
   * Returns the x coordinate of the first parameter point.
   *
   * @return x coordinate of the first parameter point.
   */
  public float getX1() {
    return x1;
  }

  /**
   * Defines the x coordinate of the first parameter point.
   *
   * @param x1
   *          the x coordinate of the first parameter point.
   */
  public void setX1( final float x1 ) {
    this.x1 = x1;
  }

  /**
   * Returns the y coordinate of the first parameter point.
   *
   * @return y coordinate of the first parameter point.
   */
  public float getY1() {
    return y1;
  }

  /**
   * Defines the y coordinate of the first parameter point.
   *
   * @param y1
   *          the y coordinate of the first parameter point.
   */
  public void setY1( final float y1 ) {
    this.y1 = y1;
  }

  /**
   * Returns the x coordinate of the second parameter point.
   *
   * @return x coordinate of the second parameter point.
   */
  public float getX2() {
    return x2;
  }

  /**
   * Defines the x coordinate of the second parameter point.
   *
   * @param x2
   *          the x coordinate of the second parameter point.
   */
  public void setX2( final float x2 ) {
    this.x2 = x2;
  }

  /**
   * Returns the y coordinate of the second parameter point.
   *
   * @return y coordinate of the second parameter point.
   */
  public float getY2() {
    return y2;
  }

  /**
   * Defines the y coordinate of the second parameter point.
   *
   * @param y2
   *          the y coordinate of the second parameter point.
   */
  public void setY2( final float y2 ) {
    this.y2 = y2;
  }

  /**
   * Returns the x coordinate of the third parameter point.
   *
   * @return x coordinate of the third parameter point.
   */
  public float getX3() {
    return x3;
  }

  /**
   * Defines the x coordinate of the third parameter point.
   *
   * @param x3
   *          the x coordinate of the third parameter point.
   */
  public void setX3( final float x3 ) {
    this.x3 = x3;
  }

  /**
   * Returns the y coordinate of the third parameter point.
   *
   * @return y coordinate of the third parameter point.
   */
  public float getY3() {
    return y3;
  }

  /**
   * Defines the y coordinate of the third parameter point.
   *
   * @param y3
   *          the y coordinate of the third parameter point.
   */
  public void setY3( final float y3 ) {
    this.y3 = y3;
  }
}
