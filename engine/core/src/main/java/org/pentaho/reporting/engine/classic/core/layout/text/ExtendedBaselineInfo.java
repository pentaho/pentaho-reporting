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

package org.pentaho.reporting.engine.classic.core.layout.text;

/**
 * Creation-Date: 04.04.2007, 14:47:05
 *
 * @author Thomas Morgner
 */
public interface ExtendedBaselineInfo {
  public static final int BASELINE_COUNT = 10;

  public static final int BEFORE_EDGE = 0;
  public static final int TEXT_BEFORE_EDGE = 1;
  public static final int HANGING = 2;
  public static final int CENTRAL = 3;
  public static final int MIDDLE = 4;
  public static final int MATHEMATICAL = 5;
  public static final int ALPHABETHIC = 6;
  public static final int IDEOGRAPHIC = 7;
  public static final int TEXT_AFTER_EDGE = 8;
  public static final int AFTER_EDGE = 9;

  public int getDominantBaseline();

  public long[] getBaselines();

  public long getBaseline( int baseline );

  public long getUnderlinePosition();

  public long getStrikethroughPosition();
}
