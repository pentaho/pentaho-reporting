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

package org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.internal;

import com.lowagie.text.pdf.PdfAnnotation;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfRectangle;
import com.lowagie.text.pdf.PdfWriter;

public class PolygonAnnotation extends PdfAnnotation {
  private static final PdfName POLYGON = new PdfName( "Polygon" ); // NON-NLS
  private static final PdfName VERTICES = new PdfName( "Vertices" ); // NON-NLS

  public PolygonAnnotation( final PdfWriter writer, final float[] coords ) {
    super( writer, null );
    put( PdfName.SUBTYPE, POLYGON );
    put( PdfName.RECT, createRec( coords ) );
    put( VERTICES, new PdfArray( coords ) );
  }

  private static PdfRectangle createRec( final float[] coords ) {
    float minX = Integer.MAX_VALUE;
    float maxX = Integer.MIN_VALUE;
    float minY = Integer.MAX_VALUE;
    float maxY = Integer.MIN_VALUE;

    for ( int i = 0; i < coords.length; i += 2 ) {
      float x = coords[i];
      float y = coords[i + 1];
      if ( x < minX ) {
        minX = x;
      }
      if ( y < minY ) {
        minY = y;
      }

      if ( x > maxX ) {
        maxX = x;
      }
      if ( y > maxY ) {
        maxY = y;
      }
    }
    return new PdfRectangle( minX, minY, maxX, maxY );
  }
}
