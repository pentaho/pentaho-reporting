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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
