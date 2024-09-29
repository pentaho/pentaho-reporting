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


package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.util.Arrays;

public class StoredStyle {
  private DefaultStyleBuilder.StyleCarrier[] styles;
  private int hashCode;

  public StoredStyle( final StyleBuilder styleBuilder ) {
    this.styles = styleBuilder.toArray();
    int hashCode = 1;
    for ( final DefaultStyleBuilder.StyleCarrier sc : styles ) {
      if ( sc != null ) {
        final DefaultStyleBuilder.CSSKeys s = sc.getKey();
        hashCode = s.hashCode() + hashCode * 23;
      } else {
        hashCode = hashCode * 23;
      }
    }
    this.hashCode = hashCode;
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final StoredStyle that = (StoredStyle) o;

    if ( !Arrays.equals( styles, that.styles ) ) {
      return false;
    }

    return true;
  }

  public int hashCode() {
    return hashCode;
  }

  public void print( final Writer writer, final boolean compact ) throws IOException {
    final String lineSeparator = StringUtils.getLineSeparator();
    boolean first = true;
    for ( final DefaultStyleBuilder.StyleCarrier sc : styles ) {
      if ( sc == null ) {
        continue;
      }
      if ( first == false ) {
        writer.write( "; " );
      }

      if ( compact == false ) {
        if ( first == false ) {
          writer.write( lineSeparator );
        }
        writer.write( DefaultStyleBuilder.INDENT );
      }

      writer.write( sc.getKey().getCssName() );
      writer.write( ": " );
      writer.write( sc.getValue() );
      final String unit = sc.getUnit();
      if ( unit != null ) {
        writer.write( unit );
      }
      first = false;
    }
  }

  public void print( final StringBuffer buffer, final boolean compact ) {
    final String lineSeparator = StringUtils.getLineSeparator();
    boolean first = true;
    for ( final DefaultStyleBuilder.StyleCarrier sc : styles ) {
      if ( sc == null ) {
        continue;
      }

      if ( first == false ) {
        buffer.append( "; " );
      }

      if ( compact == false ) {
        if ( first == false ) {
          buffer.append( lineSeparator );
        }
        buffer.append( DefaultStyleBuilder.INDENT );
      }

      buffer.append( sc.getKey() );
      buffer.append( ": " );
      buffer.append( sc.getValue() );
      final String unit = sc.getUnit();
      if ( unit != null ) {
        buffer.append( unit );
      }
      first = false;
    }
  }

}
