/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.IOException;
import java.io.Writer;
import java.text.NumberFormat;

public interface StyleBuilder {
  public static class StyleCarrier {
    private CSSKeys key;
    private String value;
    private String unit;

    public StyleCarrier( final CSSKeys key, final String value, final String unit ) {
      if ( key == null ) {
        throw new NullPointerException();
      }
      this.key = key;
      this.value = value;
      this.unit = unit;
    }

    public String getUnit() {
      return unit;
    }

    public CSSKeys getKey() {
      return key;
    }

    public String getValue() {
      return value;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final StyleCarrier that = (StyleCarrier) o;
      if ( !key.equals( that.key ) ) {
        return false;
      }
      if ( ObjectUtilities.equal( value, that.value ) == false ) {
        return false;
      }
      if ( ObjectUtilities.equal( unit, that.unit ) == false ) {
        return false;
      }
      return true;
    }

    public int hashCode() {
      int result;
      result = key.hashCode();
      result = 31 * result + ( value != null ? value.hashCode() : 0 );
      result = 31 * result + ( unit != null ? unit.hashCode() : 0 );
      return result;
    }

    public String toString() {
      return "StyleCarrier{" + "key='" + key + '\'' + ", value='" + value + '\'' + ", unit='" + unit + '\'' + '}';
    }
  }

  public enum CSSKeys {
    COLOR( "color", true ),

    PADDING( "padding" ), PADDING_TOP( "padding-top" ), PADDING_BOTTOM( "padding-bottom" ), PADDING_LEFT(
        "padding-left" ), PADDING_RIGHT( "padding-right" ),

    FONT_SIZE( "font-size", true ), FONT_FAMILY( "font-family", true ), FONT_WEIGHT( "font-weight", true ), FONT_STYLE(
        "font-style", true ), TEXT_DECORATION( "text-decoration", true ), TEXT_ALIGN( "text-align", true ),

    WORD_SPACING( "word-spacing", true ), LETTER_SPACING( "letter-spacing", true ), WHITE_SPACE( "white-space", true ),

    BORDER_COLLAPSE( "border-collapse" ), EMPTY_CELLS( "empty-cells" ), TABLE_LAYOUT( "table-layout" ), BORDER(
        "border" ), BORDER_TOP( "border-top" ), BORDER_LEFT( "border-left" ), BORDER_BOTTOM( "border-bottom" ), BORDER_RIGHT(
        "border-right" ), MOZ_BORDER_RADIUS_TOP_LEFT( "-moz-border-radius-topleft" ), MOZ_BORDER_RADIUS_TOP_RIGHT(
        "-moz-border-radius-topright" ), MOZ_BORDER_RADIUS_BOTTOM_LEFT( "-moz-border-radius-bottomleft" ), MOZ_BORDER_RADIUS_BOTTOM_RIGHT(
        "-moz-border-radius-bottomright" ), BORDER_TOP_LEFT_RADIUS( "border-top-left-radius" ), BORDER_TOP_RIGHT_RADIUS(
        "border-top-right-radius" ), BORDER_BOTTOM_LEFT_RADIUS( "border-bottom-left-radius" ), BORDER_BOTTOM_RIGHT_RADIUS(
        "border-bottom-right-radius" ),

    POSITION( "position" ), TOP( "top" ),

    BACKGROUND_COLOR( "background-color", true ), OVERFLOW( "overflow" ), WIDTH( "width" ), HEIGHT( "height" ),

    CONTENT( "content" ),
    TRANSFORM_ORIGIN( "transform-origin" ), TRANSFORM( "transform" ), DIRECTION( "direction" );

    private String cssName;
    private boolean inherit;

    private CSSKeys( final String cssName, final boolean inherit ) {
      this.cssName = cssName;
      this.inherit = inherit;
    }

    private CSSKeys( final String cssName ) {
      this.cssName = cssName;
    }

    public String getCssName() {
      return cssName;
    }

    public boolean isInherit() {
      return inherit;
    }
  }

  void clear();

  void append( CSSKeys key, String value );

  void appendRaw( CSSKeys key, String value );

  void append( CSSKeys key, String value, String unit );

  void append( CSSKeys key, String value, boolean replace );

  void append( CSSKeys key, String value, String unit, boolean replace );

  String toString();

  void print( Writer writer, boolean compact ) throws IOException;

  String toString( boolean compact );

  String printEdgeAsCSS( BorderEdge edge );

  String printCornerAsCSS( BorderCorner edge );

  NumberFormat getPointConverter();

  StyleCarrier[] toArray();

  public boolean isEmpty();

}
