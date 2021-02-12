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
 * Copyright (c) 2001 - 2021 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.xls.helper;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.util.HSSFColor;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;

/**
 * POI Excel utility methods.
 *
 * @author Heiko Evermann
 */
public final class StaticExcelColorSupport implements ExcelColorProducer {
  private static final Log logger = LogFactory.getLog( StaticExcelColorSupport.class );

  // 47: 255, 204, 153

  /**
   * DefaultConstructor.
   */
  public StaticExcelColorSupport() {
  }

  /**
   * the pre-defined excel color triplets.
   */
  private static final Map triplets;
  private static final Map indexes;

  static {
    final HashMap indexMap = new HashMap();
    indexMap.putAll( HSSFColor.getIndexHash() );
    indexMap.put( HSSFColor.HSSFColorPredefined.TAN.getIndex(), HSSFColor.HSSFColorPredefined.TAN.getColor() );

    final HashMap tripletMap = new HashMap();
    tripletMap.putAll( HSSFColor.getTripletHash() );
    tripletMap.put( HSSFColor.HSSFColorPredefined.TAN.getHexString(), HSSFColor.HSSFColorPredefined.TAN.getColor() );

    indexes = Collections.unmodifiableMap( indexMap );
    triplets = Collections.unmodifiableMap( tripletMap );
  }

  /**
   * Find a suitable color for the cell.
   * <p/>
   * The algorithm searches all available triplets, weighted by tripletvalue and tripletdifference to the other
   * triplets. The color wins, which has the smallest triplet difference and where all triplets are nearest to the
   * requested color. Damn, why couldn't these guys from microsoft implement a real color system.
   *
   * @param awtColor
   *          the awt color that should be transformed into an Excel color.
   * @return the excel color index that is nearest to the supplied color.
   */
  public short getNearestColor( final Color awtColor ) {
    return getNearestColor( awtColor, triplets );
  }

  public static short getNearestColor( final Color awtColor, final Map triplets ) {
    if ( awtColor == null ) {
      throw new NullPointerException();
    }

    if ( triplets == null || triplets.isEmpty() ) {
      logger.warn( "Unable to get triplet hashtable" );
      return HSSFColor.HSSFColorPredefined.BLACK.getIndex();
    }

    short color = HSSFColor.HSSFColorPredefined.BLACK.getIndex();
    double minDiff = Double.MAX_VALUE;

    // get the color without the alpha chanel
    final float[] hsb = Color.RGBtoHSB( awtColor.getRed(), awtColor.getGreen(), awtColor.getBlue(), null );

    float[] excelHsb = null;
    final Iterator elements = triplets.values().iterator();
    while ( elements.hasNext() ) {
      final HSSFColor crtColor = (HSSFColor) elements.next();
      final short[] rgb = crtColor.getTriplet();
      excelHsb = Color.RGBtoHSB( rgb[0], rgb[1], rgb[2], excelHsb );

      final double weight =
          3.0 * ( Math.min( Math.abs( excelHsb[0] - hsb[0] ), Math.abs( excelHsb[0] - hsb[0] + 1 ) ) )
              + Math.abs( excelHsb[1] - hsb[1] ) + Math.abs( excelHsb[2] - hsb[2] );

      if ( weight < minDiff ) {
        minDiff = weight;
        if ( minDiff == 0 ) {
          // we found the color ...
          return crtColor.getIndex();
        }
        color = crtColor.getIndex();
      }
    }
    return color;
  }

  public HSSFColor getColor( final short index ) {
    final Integer s = IntegerCache.getInteger( index );
    final HSSFColor color = (HSSFColor) indexes.get( s );
    if ( color == null ) {
      throw new IllegalStateException( "No such color." );
    }
    return color;
  }
}
