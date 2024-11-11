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


package org.pentaho.reporting.libraries.css.keys.content;

import org.pentaho.reporting.libraries.css.values.CSSStringType;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;

/**
 * This class holds a sample of well-known quoting characters. These values are non-normative and there are no
 * CSS-constants defined for them.
 *
 * @author Thomas Morgner
 */
public class QuotingValues {

  public static final CSSStringValue QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\"" );
  public static final CSSStringValue APOSTROPHE =
    new CSSStringValue( CSSStringType.STRING, "\u0027" );
  public static final CSSStringValue SINGLE_LEFT_POINTING_ANGLE_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u2039" );
  public static final CSSStringValue SINGLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u203A" );
  public static final CSSStringValue DOUBLE_LEFT_POINTING_ANGLE_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u00AB" );
  public static final CSSStringValue DOUBLE_RIGHT_POINTING_ANGLE_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u00BB" );
  public static final CSSStringValue SINGLE_LEFT_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u2018" );
  public static final CSSStringValue SINGLE_RIGHT_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u2019" );
  public static final CSSStringValue DOUBLE_LEFT_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u201C" );
  public static final CSSStringValue DOUBLE_RIGHT_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u201D" );
  public static final CSSStringValue DOUBLE_LOW9_QUOTATION_MARK =
    new CSSStringValue( CSSStringType.STRING, "\u201E" );

  private QuotingValues() {
  }
}
