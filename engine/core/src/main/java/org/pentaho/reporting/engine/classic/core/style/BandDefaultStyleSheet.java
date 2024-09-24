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
