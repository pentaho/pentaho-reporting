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

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.util.HtmlColors;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.util.HtmlEncoderUtil;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.util.LFUMap;
import org.pentaho.reporting.libraries.base.util.StringBufferWriter;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.io.IOException;
import java.io.Writer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;

public final class DefaultStyleBuilder implements StyleBuilder {

  public static final String INDENT = "    ";

  private LFUMap<BorderEdge, String> cachedBorderStyle;
  private LFUMap<BorderCorner, String> cachedCornerStyle;
  private String lineSeparator;
  private StringBuffer buffer;
  private StyleCarrier[] usedStyles;

  private NumberFormat pointConverter;
  private StyleBuilderFactory factory;

  public DefaultStyleBuilder( final StyleBuilderFactory factory ) {
    this.factory = factory;
    this.lineSeparator = StringUtils.getLineSeparator();
    this.cachedBorderStyle = new LFUMap<BorderEdge, String>( 30 );
    this.cachedCornerStyle = new LFUMap<BorderCorner, String>( 30 );
    this.buffer = new StringBuffer( 100 );
    this.usedStyles = new StyleCarrier[CSSKeys.values().length];
    if ( "true".equals( ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.modules.output.table.html.SafariLengthHack" ) ) ) {
      pointConverter = new DecimalFormat( "0", new DecimalFormatSymbols( Locale.US ) );
    } else {
      pointConverter = new DecimalFormat( "0.####", new DecimalFormatSymbols( Locale.US ) );
    }
  }

  public void clear() {
    Arrays.fill( usedStyles, null );
  }

  public void append( final CSSKeys key, final String value ) {
    final StyleCarrier newCarrier = new StyleCarrier( key, HtmlEncoderUtil.encodeCSS( value, prepareBuffer() ), null );
    usedStyles[key.ordinal()] = newCarrier;
  }

  public void appendRaw( final CSSKeys key, final String value ) {
    final StyleCarrier newCarrier = new StyleCarrier( key, value, null );
    usedStyles[key.ordinal()] = newCarrier;
  }

  public void append( final CSSKeys key, final String value, final String unit ) {
    final StyleCarrier newCarrier = new StyleCarrier( key, HtmlEncoderUtil.encodeCSS( value, prepareBuffer() ), unit );
    usedStyles[key.ordinal()] = newCarrier;
  }

  /**
   * Appends the style to the list. If the replace value is <code>false</code> and the list already contains the key, it
   * will not be replaced.
   */
  public void append( final CSSKeys key, final String value, final boolean replace ) {
    if ( replace == false ) {
      final Object stylePos = usedStyles[key.ordinal()];
      if ( stylePos != null ) {
        return;
      }
    }

    final StyleCarrier newCarrier = new StyleCarrier( key, HtmlEncoderUtil.encodeCSS( value, prepareBuffer() ), null );
    usedStyles[key.ordinal()] = newCarrier;
  }

  /**
   * Appends the style to the list. If the replace value if <code>false</code> and the list already contains the key, it
   * will not be replaced.
   */
  public void append( final CSSKeys key, final String value, final String unit, final boolean replace ) {
    if ( replace == false ) {
      final Object stylePos = usedStyles[key.ordinal()];
      if ( stylePos != null ) {
        return;
      }
    }

    final StyleCarrier newCarrier = new StyleCarrier( key, HtmlEncoderUtil.encodeCSS( value, prepareBuffer() ), unit );
    usedStyles[key.ordinal()] = newCarrier;
  }

  private StringBuffer prepareBuffer() {
    buffer.delete( 0, buffer.length() );
    return buffer;
  }

  public String toString() {
    return toString( true );
  }

  public void print( final Writer writer, final boolean compact ) throws IOException {
    // we are usign a linked list now, so a iterator is more efficient than a for loop ...
    boolean first = true;
    for ( final StyleCarrier sc : usedStyles ) {
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

  public String toString( final boolean compact ) {
    buffer.delete( 0, buffer.length() );
    final StringBufferWriter writer = new StringBufferWriter( buffer );
    try {
      print( writer, compact );
    } catch ( IOException e ) {
      // will not happen ..
      throw new IllegalStateException( "How can a fully buffered writer cause a IO exception?" );
    }
    return buffer.toString();
  }

  public String printEdgeAsCSS( final BorderEdge edge ) {
    final BorderStyle borderStyle = edge.getBorderStyle();
    final long width = edge.getWidth();
    if ( BorderStyle.NONE.equals( borderStyle ) || width <= 0 ) {
      return "none";
    }

    final String cached = cachedBorderStyle.get( edge );
    if ( cached != null ) {
      return cached;
    }

    final String value =
        pointConverter.format( factory.fixLengthForSafari( StrictGeomUtility.toExternalValue( width ) ) ) + "pt "
            + mapStyleToHtmlAllowed( borderStyle.toString() ) + ' ' + HtmlColors.getColorString( edge.getColor() );
    cachedBorderStyle.put( edge, value );
    return value;
  }

  private String mapStyleToHtmlAllowed( String style ) {
    if ( BorderStyle.DOT_DASH.toString().equals( style ) ) {
      return BorderStyle.DASHED.toString();
    } else if ( BorderStyle.DOT_DOT_DASH.toString().equals( style ) ) {
      return BorderStyle.DOTTED.toString();
    } else {
      return style;
    }
  }

  public String printCornerAsCSS( final BorderCorner edge ) {
    final String cached = cachedCornerStyle.get( edge );
    if ( cached != null ) {
      return cached;
    }

    final String value =
        ( pointConverter.format( factory.fixLengthForSafari( StrictGeomUtility.toExternalValue( edge.getWidth() ) ) )
            + "pt "
            + pointConverter
                .format( factory.fixLengthForSafari( StrictGeomUtility.toExternalValue( edge.getHeight() ) ) ) + "pt " );
    cachedCornerStyle.put( edge, value );
    return value;
  }

  public NumberFormat getPointConverter() {
    return pointConverter;
  }

  /**
   * @return the style carriers as array.
   */
  public StyleCarrier[] toArray() {
    return usedStyles.clone();
  }

  public boolean isEmpty() {
    for ( final StyleCarrier sc : usedStyles ) {
      if ( sc != null ) {
        return false;
      }
    }
    return true;
  }
}
