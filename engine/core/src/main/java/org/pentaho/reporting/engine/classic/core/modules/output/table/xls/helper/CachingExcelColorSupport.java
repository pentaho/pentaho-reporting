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

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.awt.Color;

import org.pentaho.reporting.libraries.base.util.LFUMap;

public class CachingExcelColorSupport implements ExcelColorProducer {
  private ExcelColorProducer base;
  private LFUMap<Integer, Short> colorCache;

  public CachingExcelColorSupport( final ExcelColorProducer base ) {
    if ( base == null ) {
      throw new NullPointerException();
    }
    this.base = base;
    this.colorCache = new LFUMap<Integer, Short>( 5000 );
  }

  public short getNearestColor( final Color awtColor ) {
    Short value = colorCache.get( awtColor.getRGB() );
    if ( value != null ) {
      return value;
    }

    short nearestColor = base.getNearestColor( awtColor );
    colorCache.put( awtColor.getRGB(), nearestColor );
    return nearestColor;
  }
}
