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
 * Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.layout.richtext.html;

import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;

public class FilterStyleBuilder implements StyleBuilder {
  private StyleBuilder builder;
  private StyleCarrier[] parentStyle;

  public FilterStyleBuilder( final StyleBuilder builder, final StyleCarrier[] parentStyle ) {
    this.builder = builder;
    this.parentStyle = parentStyle;
  }

  public void clear() {
    builder.clear();
  }

  private boolean isFiltered( final CSSKeys key, final StyleCarrier value ) {
    if ( parentStyle == null ) {
      return false;
    }
    if ( key.isInherit() == false ) {
      return false;
    }
    if ( ObjectUtilities.equal( parentStyle[key.ordinal()], value ) ) {
      return true;
    }
    return false;
  }

  public void append( final CSSKeys key, final String value ) {
    if ( isFiltered( key, new StyleCarrier( key, HtmlEncoderUtil.encodeCSS( value ), null ) ) ) {
      return;
    }
    builder.append( key, value );
  }

  public void appendRaw( final CSSKeys key, final String value ) {
    if ( isFiltered( key, new StyleCarrier( key, value, null ) ) ) {
      return;
    }
    builder.appendRaw( key, value );
  }

  public void append( final CSSKeys key, final String value, final String unit ) {
    if ( isFiltered( key, new StyleCarrier( key, HtmlEncoderUtil.encodeCSS( value ), unit ) ) ) {
      return;
    }
    builder.append( key, value, unit );
  }

  public void append( final CSSKeys key, final String value, final boolean replace ) {
    if ( isFiltered( key, new StyleCarrier( key, HtmlEncoderUtil.encodeCSS( value ), null ) ) ) {
      return;
    }
    builder.append( key, value, replace );
  }

  public void append( final CSSKeys key, final String value, final String unit, final boolean replace ) {
    if ( isFiltered( key, new StyleCarrier( key, HtmlEncoderUtil.encodeCSS( value ), unit ) ) ) {
      return;
    }
    builder.append( key, value, unit, replace );
  }

  public String toString() {
    return builder.toString();
  }

  public String toString( final boolean compact ) {
    return builder.toString( compact );
  }

  public void print( final Writer writer, final boolean compact ) throws IOException {
    builder.print( writer, compact );
  }

  public String printEdgeAsCSS( final BorderEdge edge ) {
    return builder.printEdgeAsCSS( edge );
  }

  public String printCornerAsCSS( final BorderCorner edge ) {
    return builder.printCornerAsCSS( edge );
  }

  public NumberFormat getPointConverter() {
    return builder.getPointConverter();
  }

  public StyleCarrier[] toArray() {
    return builder.toArray();
  }

  public boolean isEmpty() {
    return builder.isEmpty();
  }
}
