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

package org.pentaho.reporting.engine.classic.core.style;

/**
 * A default band style sheet. This StyleSheet defines the default attribute values for all master and subreports. The
 * master- and sub-reports have a band layout.
 *
 * @author Thomas Morgner
 */
public final class ReportDefaultStyleSheet extends ElementDefaultStyleSheet {
  /**
   * A shared default style-sheet.
   */
  private static ReportDefaultStyleSheet defaultStyle;

  /**
   * Creates a new default style sheet.
   */
  protected ReportDefaultStyleSheet() {
    setLocked( false );
    setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 0 ) );
    setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 0 ) );
    setStyleProperty( ElementStyleKeys.MAX_HEIGHT, new Float( ReportSectionDefaultStyleSheet.PHYSICAL_LIMIT ) );
    setStyleProperty( ElementStyleKeys.MAX_WIDTH, new Float( ReportSectionDefaultStyleSheet.PHYSICAL_LIMIT ) );
    setStyleProperty( BandStyleKeys.PAGEBREAK_AFTER, Boolean.FALSE );
    setStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE, Boolean.FALSE );
    setStyleProperty( BandStyleKeys.STICKY, Boolean.FALSE );
    setStyleProperty( BandStyleKeys.LAYOUT, BandStyleKeys.LAYOUT_BLOCK );
    setLocked( true );
  }

  /**
   * Returns the default band style sheet.
   *
   * @return the style-sheet.
   */
  public static synchronized ReportDefaultStyleSheet getSectionDefault() {
    if ( defaultStyle == null ) {
      defaultStyle = new ReportDefaultStyleSheet();
    }
    return defaultStyle;
  }
}
