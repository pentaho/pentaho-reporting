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
import org.pentaho.reporting.libraries.fonts.text.ClassificationProducer;

/**
 * Looks-up the character on the given font.
 *
 * @author Thomas Morgner
 */
public class VariableFontSizeProducer implements FontSizeProducer {
  private FontMetrics fontMetrics;
  private int maxHeight;
  private int baseLine;


  public VariableFontSizeProducer( final FontMetrics fontMetrics ) {
    if ( fontMetrics == null ) {
      throw new NullPointerException();
    }
    this.fontMetrics = fontMetrics;
    this.maxHeight = (int) ( 0x7FFFFFFF & fontMetrics.getMaxHeight() );
    this.baseLine = (int) ( 0x7FFFFFFF & ( fontMetrics.getMaxHeight() - fontMetrics.getMaxDescent() ) );
  }

  public GlyphMetrics getCharacterSize( final int codePoint,
                                        GlyphMetrics dimension ) {
    final int width;
    if ( codePoint == ClassificationProducer.START_OF_TEXT ||
      codePoint == ClassificationProducer.END_OF_TEXT ||
      codePoint == -1 ) {
      width = 0;
    } else {
      width = (int) ( 0x7FFFFFFF & fontMetrics.getCharWidth( codePoint ) );
    }

    if ( dimension == null ) {
      dimension = new GlyphMetrics();
    }

    dimension.setWidth( width );
    dimension.setHeight( maxHeight );
    dimension.setBaselinePosition( baseLine );
    return dimension;
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }
}
