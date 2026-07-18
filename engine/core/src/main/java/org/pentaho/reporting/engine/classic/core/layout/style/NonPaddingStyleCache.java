/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



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
