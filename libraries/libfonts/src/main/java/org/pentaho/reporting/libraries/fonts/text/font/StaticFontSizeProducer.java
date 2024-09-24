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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.fonts.text.font;

import org.pentaho.reporting.libraries.fonts.registry.FontMetrics;
import org.pentaho.reporting.libraries.fonts.tools.FontStrictGeomUtility;

/**
 * Creates a monospaced font from any given font by always returning the maximum character width and height for that
 * font. Grapheme clusters have no effect on that font size producer.
 *
 * @author Thomas Morgner
 */
public class StaticFontSizeProducer implements FontSizeProducer {
  private int maxWidth;
  private int maxHeight;
  private int baseLine;
  // private FontMetrics fontMetrics;

  public StaticFontSizeProducer( final FontMetrics fontMetrics ) {
    if ( fontMetrics == null ) {
      throw new NullPointerException();
    }
    //this.fontMetrics = fontMetrics;
    this.maxHeight = (int) ( 0x7FFFFFFF &
      FontStrictGeomUtility.toInternalValue( fontMetrics.getMaxHeight() ) );
    this.maxWidth = (int) ( 0x7FFFFFFF &
      FontStrictGeomUtility.toInternalValue( fontMetrics.getMaxCharAdvance() ) );
    this.baseLine = (int) ( 0x7FFFFFFF & FontStrictGeomUtility.toInternalValue
      ( fontMetrics.getMaxHeight() - fontMetrics.getMaxDescent() ) );
  }

  public StaticFontSizeProducer( final int maxWidth,
                                 final int maxHeight,
                                 final int baseLine ) {
    this.maxWidth = maxWidth;
    this.maxHeight = maxHeight;
    this.baseLine = baseLine;
  }

  public GlyphMetrics getCharacterSize( final int codePoint,
                                        final GlyphMetrics dimension ) {
    if ( dimension == null ) {
      final GlyphMetrics retval = new GlyphMetrics();
      retval.setWidth( maxWidth );
      retval.setHeight( maxHeight );
      retval.setBaselinePosition( baseLine );
      return retval;
    }

    dimension.setWidth( maxWidth );
    dimension.setHeight( maxHeight );
    dimension.setBaselinePosition( baseLine );
    return dimension;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
