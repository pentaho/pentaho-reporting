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

package org.pentaho.reporting.engine.classic.core.layout.style;

import org.pentaho.reporting.engine.classic.core.style.StyleSheet;

public class NonPaddingStyleCache implements StyleCache {
  private StyleCache styleCache;
  private NonPaddingWrapperStyleSheet nonPaddingWrapperStyleSheet;

  public NonPaddingStyleCache( final StyleCache styleCache ) {
    this.nonPaddingWrapperStyleSheet = new NonPaddingWrapperStyleSheet();
    this.styleCache = styleCache;
  }

  public SimpleStyleSheet getStyleSheet( final StyleSheet styleSheet ) {
    nonPaddingWrapperStyleSheet.setParent( styleSheet );
    try {
      return styleCache.getStyleSheet( nonPaddingWrapperStyleSheet );
    } finally {
      nonPaddingWrapperStyleSheet.setParent( null );
    }
  }

  public String printPerformanceStats() {
    return styleCache.printPerformanceStats();
  }
}
