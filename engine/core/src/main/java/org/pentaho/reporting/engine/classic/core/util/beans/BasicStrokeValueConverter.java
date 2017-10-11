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

package org.pentaho.reporting.engine.classic.core.util.beans;

import java.awt.BasicStroke;
import java.util.Locale;

import org.pentaho.reporting.libraries.base.util.StringUtils;

public class BasicStrokeValueConverter implements ValueConverter {
  public BasicStrokeValueConverter() {
  }

  public String toAttributeValue( final Object o ) throws BeanException {
    if ( o instanceof BasicStroke == false ) {
      throw new BeanException();
    }
    final BasicStroke s = (BasicStroke) o;
    final float lineWidth = s.getLineWidth();
    final int lineJoin = s.getLineJoin();
    final float dashPhase = s.getDashPhase();
    final int endCap = s.getEndCap();
    final float mitterLimit = s.getMiterLimit();
    final float[] dashArray = s.getDashArray();

    final StringBuilder b = new StringBuilder();
    if ( dashArray != null ) {
      for ( int i = 0; i < dashArray.length; i++ ) {
        if ( i != 0 ) {
          b.append( "," );
        }
        b.append( dashArray[i] );
      }
    }

    return String.format( Locale.US, "BasicStroke:%f:%d:%f:%d:%f:%s", lineWidth, lineJoin, dashPhase, endCap,
        mitterLimit, b.toString() );
  }

  public Object toPropertyValue( final String s ) throws BeanException {
    final String[] parsedResult = StringUtils.split( s, ":" );
    if ( parsedResult.length < 6 ) {
      throw new BeanException( "ParsedResult length: " + parsedResult.length );
    }

    if ( "BasicStroke".equals( parsedResult[0] ) == false ) {
      throw new BeanException();
    }

    try {
      final float lineWidth = Float.parseFloat( parsedResult[1] );
      final int lineJoin = Integer.parseInt( parsedResult[2] );
      final float dashPhase = Float.parseFloat( parsedResult[3] );
      final int endCap = Integer.parseInt( parsedResult[4] );
      final float mitterLimit = Float.parseFloat( parsedResult[5] );

      final float[] dashArray;
      if ( parsedResult.length == 7 ) {
        final String[] dashArrayText = StringUtils.split( parsedResult[6], "," );
        dashArray = new float[dashArrayText.length];
        for ( int i = 0; i < dashArrayText.length; i++ ) {
          dashArray[i] = Float.parseFloat( dashArrayText[i] );
        }
      } else {
        dashArray = null;
      }

      return new BasicStroke( lineWidth, endCap, lineJoin, mitterLimit, dashArray, dashPhase );
    } catch ( Exception e ) {
      throw new BeanException( "Failed to parse basic-stroke: " + s, e );
    }
  }
}
