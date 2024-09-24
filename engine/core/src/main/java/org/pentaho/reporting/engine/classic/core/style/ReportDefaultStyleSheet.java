/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

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
