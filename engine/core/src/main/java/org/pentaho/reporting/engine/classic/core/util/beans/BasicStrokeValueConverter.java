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
