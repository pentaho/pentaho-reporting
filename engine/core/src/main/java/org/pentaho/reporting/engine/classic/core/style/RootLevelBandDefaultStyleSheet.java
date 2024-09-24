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

public class RootLevelBandDefaultStyleSheet extends BandDefaultStyleSheet {
  /**
   * A shared default style-sheet.
   */
  private static RootLevelBandDefaultStyleSheet defaultStyle;

  /**
   * Creates a new default style sheet.
   */
  protected RootLevelBandDefaultStyleSheet() {
    setLocked( false );
    setStyleProperty( ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT, Boolean.FALSE );
    setStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE, Boolean.TRUE );
    setLocked( true );
  }

  /**
   * Returns the default band style sheet.
   *
   * @return the style-sheet.
   */
  public static synchronized RootLevelBandDefaultStyleSheet getRootLevelBandDefaultStyle() {
    if ( defaultStyle == null ) {
      defaultStyle = new RootLevelBandDefaultStyleSheet();
    }
    return defaultStyle;
  }
}
