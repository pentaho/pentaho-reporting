/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.style;

/**
 * A default band style sheet. This StyleSheet defines the default attribute values for all report-sections.
 *
 * @author Thomas Morgner
 */
public final class ReportSectionDefaultStyleSheet extends ElementDefaultStyleSheet {
  public static final long PHYSICAL_LIMIT = (long) StrictMath.pow( 2, 52 );

  /**
   * A shared default style-sheet.
   */
  private static ReportSectionDefaultStyleSheet defaultStyle;

  /**
   * Creates a new default style sheet.
   */
  protected ReportSectionDefaultStyleSheet() {
    setLocked( false );
    setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 0 ) );
    setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 0 ) );
    setStyleProperty( ElementStyleKeys.MAX_HEIGHT, new Float( ReportSectionDefaultStyleSheet.PHYSICAL_LIMIT ) );
    setStyleProperty( ElementStyleKeys.MAX_WIDTH, new Float( ReportSectionDefaultStyleSheet.PHYSICAL_LIMIT ) );
    setStyleProperty( BandStyleKeys.PAGEBREAK_AFTER, Boolean.FALSE );
    setStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE, Boolean.FALSE );
    setStyleProperty( BandStyleKeys.DISPLAY_ON_FIRSTPAGE, Boolean.TRUE );
    setStyleProperty( BandStyleKeys.DISPLAY_ON_LASTPAGE, Boolean.TRUE );
    setStyleProperty( BandStyleKeys.STICKY, Boolean.FALSE );
    setLocked( true );
  }

  /**
   * Returns the default band style sheet.
   *
   * @return the style-sheet.
   */
  public static synchronized ReportSectionDefaultStyleSheet getSectionDefault() {
    if ( defaultStyle == null ) {
      defaultStyle = new ReportSectionDefaultStyleSheet();
    }
    return defaultStyle;
  }
}
