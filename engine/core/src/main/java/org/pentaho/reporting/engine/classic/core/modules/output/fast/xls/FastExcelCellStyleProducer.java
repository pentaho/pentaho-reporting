/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.fast.xls;

import org.apache.poi.ss.usermodel.CellStyle;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.CellStyleProducer;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper.ExcelFontFactory;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextRotation;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.LFUMap;

public class FastExcelCellStyleProducer implements CellStyleProducer {
  private static class CacheKey {
    private final InstanceID id;
    private final CellBackground background;
    private final InstanceID styleSheetId;
    private final TextRotation textRotation;

    private CacheKey( final InstanceID id, final CellBackground background, final InstanceID styleSheetId,
                      final TextRotation textRotation ) {
      this.id = id;
      this.background = background;
      this.styleSheetId = styleSheetId;
      this.textRotation = textRotation;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final CacheKey cacheKey = (CacheKey) o;

      if ( background != null ? !background.equals( cacheKey.background ) : cacheKey.background != null ) {
        return false;
      }
      if ( id != null ? !id.equals( cacheKey.id ) : cacheKey.id != null ) {
        return false;
      }
      if ( styleSheetId != null ? !styleSheetId.equals( cacheKey.styleSheetId ) : cacheKey.styleSheetId != null ) {
        return false;
      }
      if ( textRotation != null ? !textRotation.equals( cacheKey.textRotation ) : cacheKey.textRotation != null ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = id != null ? id.hashCode() : 0;
      result = 31 * result + ( background != null ? background.hashCode() : 0 );
      result = 31 * result + ( styleSheetId != null ? styleSheetId.hashCode() : 0 );
      result = 31 * result + ( textRotation != null ? textRotation.hashCode() : 0 );
      return result;
    }
  }

  private final CellStyleProducer backend;
  private final LFUMap<CellBackground, CellStyle> backgroundCache;
  private final LFUMap<CacheKey, CellStyle> contentCache;

  public FastExcelCellStyleProducer( final CellStyleProducer backend ) {
    if ( backend == null ) {
      throw new NullPointerException();
    }
    this.contentCache = new LFUMap<CacheKey, CellStyle>( 5000 );
    this.backgroundCache = new LFUMap<CellBackground, CellStyle>( 5000 );
    this.backend = backend;
  }

  public CellStyle createCellStyle( final InstanceID id, final StyleSheet element, final CellBackground bg ) {
    if ( id == null ) {
      CellStyle cellStyle = backgroundCache.get( bg );
      if ( cellStyle != null ) {
        return cellStyle;
      }
    } else {
      CellStyle cellStyle = contentCache.get( new CacheKey( id, bg, element.getId(),
        (TextRotation) element.getStyleProperty( TextStyleKeys.TEXT_ROTATION, null ) ) );
      if ( cellStyle != null ) {
        return cellStyle;
      }
    }

    CellStyle cellStyle = backend.createCellStyle( id, element, bg );
    if ( cellStyle == null ) {
      return null;
    }
    if ( id == null ) {
      backgroundCache.put( bg, cellStyle );
    } else {
      contentCache.put( new CacheKey( id, bg, element.getId(), (TextRotation) element.getStyleProperty( TextStyleKeys.TEXT_ROTATION, null ) ), cellStyle );
    }
    return cellStyle;
  }

  public ExcelFontFactory getFontFactory() {
    return backend.getFontFactory();
  }
}
