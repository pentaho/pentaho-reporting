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
