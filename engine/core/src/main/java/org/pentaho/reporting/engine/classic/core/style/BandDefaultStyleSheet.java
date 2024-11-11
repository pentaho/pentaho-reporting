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
 * A default band style sheet. This StyleSheet defines the default attribute values for all Bands.
 *
 * @author Thomas Morgner
 */
public class BandDefaultStyleSheet extends ElementDefaultStyleSheet {
  /**
   * A shared default style-sheet.
   */
  private static BandDefaultStyleSheet defaultStyle;

  /**
   * Creates a new default style sheet.
   */
  protected BandDefaultStyleSheet() {
    setLocked( false );
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
  public static synchronized BandDefaultStyleSheet getBandDefaultStyle() {
    if ( defaultStyle == null ) {
      defaultStyle = new BandDefaultStyleSheet();
    }
    return defaultStyle;
  }
}
