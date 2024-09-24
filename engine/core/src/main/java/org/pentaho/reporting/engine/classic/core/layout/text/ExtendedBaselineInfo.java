/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
