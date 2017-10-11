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
 * Copyright (c) 2001 - 2017 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleStyleRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.styles.BundleStyleSetWriteHandler;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.BorderStyle;
import org.pentaho.reporting.engine.classic.core.style.BoxSizing;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TableLayout;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.StrokeUtility;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ColorValueConverter;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.formatting.FastDecimalFormat;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriterSupport;

import java.awt.Color;
import java.awt.Stroke;
import java.io.IOException;
import java.util.Locale;

/**
 * @noinspection HardCodedStringLiteral
 */
public class StyleWriterUtility {
  private static final Log logger = LogFactory.getLog( StyleWriterUtility.class );

  private static FastDecimalFormat getPercentageLengthFormat() {
    return new FastDecimalFormat( "0.###'%'", Locale.US );
  }

  private static FastDecimalFormat getAbsoluteLengthFormat() {
    return new FastDecimalFormat( "0.###", Locale.US );
  }

  private StyleWriterUtility() {
  }

  public static void writeStyleRule( final String namespace, final String tagName, final XmlWriter writer,
      final ElementStyleSheet style ) throws IOException {
    if ( namespace == null ) {
      throw new NullPointerException();
    }
    if ( tagName == null ) {
      throw new NullPointerException();
    }
    if ( writer == null ) {
      throw new NullPointerException();
    }
    if ( style == null ) {
      throw new NullPointerException();
    }

    if ( style.getDefinedPropertyNamesArray().length == 0 ) {
      return;
    }

    final AttributeList attList = new AttributeList();
    writer.writeTag( namespace, tagName, attList, XmlWriterSupport.OPEN );

    final BundleStyleSetWriteHandler[] writeHandlers = BundleStyleRegistry.getInstance().getWriteHandlers();
    for ( int i = 0; i < writeHandlers.length; i++ ) {
      final BundleStyleSetWriteHandler writeHandler = writeHandlers[i];
      writeHandler.writeStyle( writer, style );
    }

    writer.writeCloseTag();
  }

  public static void writeBorderStyles( final XmlWriter writer, final ElementStyleSheet style ) throws IOException {
    if ( writer == null ) {
      throw new NullPointerException();
    }
    if ( style == null ) {
      throw new NullPointerException();
    }

    final AttributeList bandStyleAtts = new AttributeList();

    if ( style.isLocalKey( ElementStyleKeys.BACKGROUND_COLOR ) ) {
      final Color value = (Color) style.getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR );
      bandStyleAtts
          .setAttribute( BundleNamespaces.STYLE, "background-color", ColorValueConverter.colorToString( value ) );
    }

    final FastDecimalFormat absoluteLengthFormat = getAbsoluteLengthFormat();
    if ( style.isLocalKey( ElementStyleKeys.PADDING_TOP ) && style.isLocalKey( ElementStyleKeys.PADDING_LEFT )
        && style.isLocalKey( ElementStyleKeys.PADDING_BOTTOM ) && style.isLocalKey( ElementStyleKeys.PADDING_RIGHT ) ) {
      final double paddingTop = style.getDoubleStyleProperty( ElementStyleKeys.PADDING_TOP, 0 );
      final double paddingLeft = style.getDoubleStyleProperty( ElementStyleKeys.PADDING_LEFT, 0 );
      final double paddingBottom = style.getDoubleStyleProperty( ElementStyleKeys.PADDING_BOTTOM, 0 );
      final double paddingRight = style.getDoubleStyleProperty( ElementStyleKeys.PADDING_RIGHT, 0 );
      if ( paddingTop == paddingLeft && paddingTop == paddingRight && paddingTop == paddingBottom ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "padding", absoluteLengthFormat.format( paddingTop ) );
      } else {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "padding-top", absoluteLengthFormat.format( paddingTop ) );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "padding-left", absoluteLengthFormat.format( paddingLeft ) );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "padding-bottom", absoluteLengthFormat
            .format( paddingBottom ) );
        bandStyleAtts
            .setAttribute( BundleNamespaces.STYLE, "padding-right", absoluteLengthFormat.format( paddingRight ) );
      }
    } else {
      if ( style.isLocalKey( ElementStyleKeys.PADDING_TOP ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.PADDING_TOP, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "padding-top", absoluteLengthFormat.format( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.PADDING_LEFT ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.PADDING_LEFT, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "padding-left", absoluteLengthFormat.format( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.PADDING_BOTTOM ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.PADDING_BOTTOM, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "padding-bottom", absoluteLengthFormat.format( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.PADDING_RIGHT ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.PADDING_RIGHT, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "padding-right", absoluteLengthFormat.format( value ) );
      }
    }

    if ( style.isLocalKey( ElementStyleKeys.BORDER_TOP_WIDTH ) && style.isLocalKey( ElementStyleKeys.BORDER_LEFT_WIDTH )
        && style.isLocalKey( ElementStyleKeys.BORDER_BOTTOM_WIDTH )
        && style.isLocalKey( ElementStyleKeys.BORDER_RIGHT_WIDTH ) ) {
      final double top = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, 0 );
      final double left = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, 0 );
      final double bottom = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, 0 );
      final double right = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, 0 );
      if ( top == left && top == right && top == bottom ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-width", absoluteLengthFormat.format( top ) );
      } else {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-top-width", absoluteLengthFormat.format( top ) );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-left-width", absoluteLengthFormat.format( left ) );
        bandStyleAtts
            .setAttribute( BundleNamespaces.STYLE, "border-bottom-width", absoluteLengthFormat.format( bottom ) );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-right-width", absoluteLengthFormat.format( right ) );
      }
    } else {
      if ( style.isLocalKey( ElementStyleKeys.BORDER_TOP_WIDTH ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-top-width", absoluteLengthFormat.format( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_LEFT_WIDTH ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-left-width", absoluteLengthFormat.format( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_BOTTOM_WIDTH ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, 0 );
        bandStyleAtts
            .setAttribute( BundleNamespaces.STYLE, "border-bottom-width", absoluteLengthFormat.format( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_RIGHT_WIDTH ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-right-width", absoluteLengthFormat.format( value ) );
      }
    }
    if ( style.isLocalKey( ElementStyleKeys.BORDER_BREAK_WIDTH ) ) {
      final double value = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_BREAK_WIDTH, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-break-width", absoluteLengthFormat.format( value ) );
    }

    if ( style.isLocalKey( ElementStyleKeys.BORDER_TOP_COLOR ) && style.isLocalKey( ElementStyleKeys.BORDER_LEFT_COLOR )
        && style.isLocalKey( ElementStyleKeys.BORDER_BOTTOM_COLOR )
        && style.isLocalKey( ElementStyleKeys.BORDER_RIGHT_COLOR ) ) {
      final Color top = (Color) style.getStyleProperty( ElementStyleKeys.BORDER_TOP_COLOR );
      final Color left = (Color) style.getStyleProperty( ElementStyleKeys.BORDER_LEFT_COLOR );
      final Color bottom = (Color) style.getStyleProperty( ElementStyleKeys.BORDER_BOTTOM_COLOR );
      final Color right = (Color) style.getStyleProperty( ElementStyleKeys.BORDER_RIGHT_COLOR );
      if ( ObjectUtilities.equal( top, left ) && ObjectUtilities.equal( top, right )
          && ObjectUtilities.equal( top, bottom ) ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-color", ColorValueConverter.colorToString( top ) );
      } else {
        bandStyleAtts
            .setAttribute( BundleNamespaces.STYLE, "border-top-color", ColorValueConverter.colorToString( top ) );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-left-color", ColorValueConverter
            .colorToString( left ) );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-bottom-color", ColorValueConverter
            .colorToString( bottom ) );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-right-color", ColorValueConverter
            .colorToString( right ) );
      }
    } else {
      if ( style.isLocalKey( ElementStyleKeys.BORDER_TOP_COLOR ) ) {
        final Color value = (Color) style.getStyleProperty( ElementStyleKeys.BORDER_TOP_COLOR );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-top-color", ColorValueConverter
            .colorToString( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_LEFT_COLOR ) ) {
        final Color value = (Color) style.getStyleProperty( ElementStyleKeys.BORDER_LEFT_COLOR );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-left-color", ColorValueConverter
            .colorToString( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_BOTTOM_COLOR ) ) {
        final Color value = (Color) style.getStyleProperty( ElementStyleKeys.BORDER_BOTTOM_COLOR );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-bottom-color", ColorValueConverter
            .colorToString( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_RIGHT_COLOR ) ) {
        final Color value = (Color) style.getStyleProperty( ElementStyleKeys.BORDER_RIGHT_COLOR );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-right-color", ColorValueConverter
            .colorToString( value ) );
      }
    }
    if ( style.isLocalKey( ElementStyleKeys.BORDER_BREAK_COLOR ) ) {
      final Color value = (Color) style.getStyleProperty( ElementStyleKeys.BORDER_BREAK_COLOR );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-break-color", ColorValueConverter
          .colorToString( value ) );
    }

    if ( style.isLocalKey( ElementStyleKeys.BORDER_TOP_STYLE ) && style.isLocalKey( ElementStyleKeys.BORDER_LEFT_STYLE )
        && style.isLocalKey( ElementStyleKeys.BORDER_BOTTOM_STYLE )
        && style.isLocalKey( ElementStyleKeys.BORDER_RIGHT_STYLE ) ) {
      final Object top = style.getStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE );
      final Object left = style.getStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE );
      final Object bottom = style.getStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE );
      final Object right = style.getStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE );
      if ( ObjectUtilities.equal( top, left ) && ObjectUtilities.equal( top, right )
          && ObjectUtilities.equal( top, bottom ) ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-style", top.toString() );
      } else {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-top-style", top.toString() );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-left-style", left.toString() );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-bottom-style", bottom.toString() );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-right-style", right.toString() );
      }
    } else {
      if ( style.isLocalKey( ElementStyleKeys.BORDER_TOP_STYLE ) ) {
        final Object value = style.getStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-top-style", value.toString() );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_LEFT_STYLE ) ) {
        final Object value = style.getStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-left-style", value.toString() );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_BOTTOM_STYLE ) ) {
        final Object value = style.getStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-bottom-style", value.toString() );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_RIGHT_STYLE ) ) {
        final Object value = style.getStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-right-style", value.toString() );
      }
    }
    if ( style.isLocalKey( ElementStyleKeys.BORDER_BREAK_STYLE ) ) {
      final Object value = style.getStyleProperty( ElementStyleKeys.BORDER_BREAK_STYLE );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-break-style", value.toString() );
    }

    if ( style.isLocalKey( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH )
        && style.isLocalKey( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH )
        && style.isLocalKey( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH )
        && style.isLocalKey( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH ) ) {
      final double bottomLeft = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH, 0 );
      final double bottomRight = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH, 0 );
      final double topLeft = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH, 0 );
      final double topRight = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH, 0 );
      if ( bottomLeft == bottomRight && bottomLeft == topRight && bottomLeft == topLeft ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-radius-width", absoluteLengthFormat
            .format( bottomLeft ) );
      } else {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-top-left-radius-width", absoluteLengthFormat
            .format( topLeft ) );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-top-right-radius-width", absoluteLengthFormat
            .format( topRight ) );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-bottom-left-radius-width", absoluteLengthFormat
            .format( bottomLeft ) );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-bottom-right-radius-width", absoluteLengthFormat
            .format( bottomRight ) );
      }
    } else {
      if ( style.isLocalKey( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-top-left-radius-width", absoluteLengthFormat
            .format( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-top-right-radius-width", absoluteLengthFormat
            .format( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-bottom-left-radius-width", absoluteLengthFormat
            .format( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-bottom-right-radius-width", absoluteLengthFormat
            .format( value ) );
      }
    }

    if ( style.isLocalKey( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT )
        && style.isLocalKey( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT )
        && style.isLocalKey( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT )
        && style.isLocalKey( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT ) ) {
      final double bottomLeft = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT, 0 );
      final double bottomRight = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT, 0 );
      final double topLeft = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT, 0 );
      final double topRight = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT, 0 );
      if ( bottomLeft == bottomRight && bottomLeft == topRight && bottomLeft == topLeft ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-radius-height", absoluteLengthFormat
            .format( bottomLeft ) );
      } else {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-top-left-radius-height", absoluteLengthFormat
            .format( topLeft ) );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-top-right-radius-height", absoluteLengthFormat
            .format( topRight ) );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-bottom-left-radius-height", absoluteLengthFormat
            .format( bottomLeft ) );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-bottom-right-radius-height", absoluteLengthFormat
            .format( bottomRight ) );
      }
    } else {
      if ( style.isLocalKey( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-top-left-radius-height", absoluteLengthFormat
            .format( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-top-right-radius-height", absoluteLengthFormat
            .format( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-bottom-left-radius-height", absoluteLengthFormat
            .format( value ) );
      }
      if ( style.isLocalKey( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT ) ) {
        final double value = style.getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT, 0 );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "border-bottom-right-radius-height", absoluteLengthFormat
            .format( value ) );
      }
    }

    if ( bandStyleAtts.isEmpty() == false ) {
      writer.writeTag( BundleNamespaces.STYLE, "border-styles", bandStyleAtts, XmlWriterSupport.CLOSE );
    }
  }

  public static void writeSpatialStyles( final XmlWriter writer, final ElementStyleSheet style ) throws IOException {
    if ( writer == null ) {
      throw new NullPointerException();
    }
    if ( style == null ) {
      throw new NullPointerException();
    }

    final AttributeList bandStyleAtts = new AttributeList();
    if ( style.isLocalKey( ElementStyleKeys.POS_X ) ) {
      final double value = style.getDoubleStyleProperty( ElementStyleKeys.POS_X, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "x", formatLength( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.POS_Y ) ) {
      final double value = style.getDoubleStyleProperty( ElementStyleKeys.POS_Y, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "y", formatLength( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.MIN_WIDTH ) ) {
      final double value = style.getDoubleStyleProperty( ElementStyleKeys.MIN_WIDTH, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "min-width", formatLength( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.MIN_HEIGHT ) ) {
      final double value = style.getDoubleStyleProperty( ElementStyleKeys.MIN_HEIGHT, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "min-height", formatLength( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.WIDTH ) ) {
      final double value = style.getDoubleStyleProperty( ElementStyleKeys.WIDTH, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "width", formatLength( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.HEIGHT ) ) {
      final double value = style.getDoubleStyleProperty( ElementStyleKeys.HEIGHT, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "height", formatLength( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.MAX_WIDTH ) ) {
      final double value = style.getDoubleStyleProperty( ElementStyleKeys.MAX_WIDTH, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "max-width", formatLength( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.MAX_HEIGHT ) ) {
      final double value = style.getDoubleStyleProperty( ElementStyleKeys.MAX_HEIGHT, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "max-height", formatLength( value ) );
    }
    if ( bandStyleAtts.isEmpty() == false ) {
      writer.writeTag( BundleNamespaces.STYLE, "spatial-styles", bandStyleAtts, XmlWriterSupport.CLOSE );
    }
  }

  public static void writeTextStyles( final XmlWriter writer, final ElementStyleSheet style ) throws IOException {
    if ( writer == null ) {
      throw new NullPointerException();
    }
    if ( style == null ) {
      throw new NullPointerException();
    }

    final AttributeList bandStyleAtts = new AttributeList();
    if ( style.isLocalKey( TextStyleKeys.FONT ) ) {
      final String value = (String) style.getStyleProperty( TextStyleKeys.FONT );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "font-face", value );
    }
    if ( style.isLocalKey( TextStyleKeys.BOLD ) ) {
      final boolean value = style.getBooleanStyleProperty( TextStyleKeys.BOLD );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "bold", String.valueOf( value ) );
    }
    if ( style.isLocalKey( TextStyleKeys.ITALIC ) ) {
      final boolean value = style.getBooleanStyleProperty( TextStyleKeys.ITALIC );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "italic", String.valueOf( value ) );
    }
    if ( style.isLocalKey( TextStyleKeys.EMBEDDED_FONT ) ) {
      final boolean value = style.getBooleanStyleProperty( TextStyleKeys.EMBEDDED_FONT );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "embedded", String.valueOf( value ) );
    }
    if ( style.isLocalKey( TextStyleKeys.UNDERLINED ) ) {
      final boolean value = style.getBooleanStyleProperty( TextStyleKeys.UNDERLINED );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "underline", String.valueOf( value ) );
    }
    if ( style.isLocalKey( TextStyleKeys.STRIKETHROUGH ) ) {
      final boolean value = style.getBooleanStyleProperty( TextStyleKeys.STRIKETHROUGH );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "strikethrough", String.valueOf( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.EXCEL_WRAP_TEXT ) ) {
      final boolean value = style.getBooleanStyleProperty( ElementStyleKeys.EXCEL_WRAP_TEXT );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "excel-text-wrapping", String.valueOf( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.EXCEL_INDENTION ) ) {
      final int value = style.getIntStyleProperty( ElementStyleKeys.EXCEL_INDENTION, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "excel-text-indention", String.valueOf( value ) );
    }
    if ( style.isLocalKey( TextStyleKeys.TRIM_TEXT_CONTENT ) ) {
      final boolean value = style.getBooleanStyleProperty( TextStyleKeys.TRIM_TEXT_CONTENT );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "trim-text-content", String.valueOf( value ) );
    }
    if ( style.isLocalKey( TextStyleKeys.FONTENCODING ) ) {
      final String value = (String) style.getStyleProperty( TextStyleKeys.FONTENCODING );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "encoding", value );
    }
    if ( style.isLocalKey( TextStyleKeys.RESERVED_LITERAL ) ) {
      final String value = (String) style.getStyleProperty( TextStyleKeys.RESERVED_LITERAL );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "ellipsis", value );
    }
    if ( style.isLocalKey( TextStyleKeys.FONTSIZE ) ) {
      final int value = style.getIntStyleProperty( TextStyleKeys.FONTSIZE, 0 );
      if ( value > 0 ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "font-size", String.valueOf( value ) );
      }
    }
    if ( style.isLocalKey( TextStyleKeys.LINEHEIGHT ) ) {
      final double value = style.getDoubleStyleProperty( TextStyleKeys.LINEHEIGHT, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "line-height", formatLength( value ) );
    }
    if ( style.isLocalKey( TextStyleKeys.WORD_SPACING ) ) {
      final int value = style.getIntStyleProperty( TextStyleKeys.WORD_SPACING, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "word-spacing", String.valueOf( value ) );
    }
    if ( style.isLocalKey( TextStyleKeys.X_MIN_LETTER_SPACING ) ) {
      final int value = style.getIntStyleProperty( TextStyleKeys.X_MIN_LETTER_SPACING, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "min-letter-spacing", String.valueOf( value ) );
    }
    if ( style.isLocalKey( TextStyleKeys.X_OPTIMUM_LETTER_SPACING ) ) {
      final int value = style.getIntStyleProperty( TextStyleKeys.X_OPTIMUM_LETTER_SPACING, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "optimum-letter-spacing", String.valueOf( value ) );
    }
    if ( style.isLocalKey( TextStyleKeys.X_MAX_LETTER_SPACING ) ) {
      final int value = style.getIntStyleProperty( TextStyleKeys.X_MAX_LETTER_SPACING, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "max-letter-spacing", String.valueOf( value ) );
    }
    if ( style.isLocalKey( TextStyleKeys.FONT_SMOOTH ) ) {
      final Object value = style.getStyleProperty( TextStyleKeys.FONT_SMOOTH );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "font-smooth", value.toString() );
    }
    if ( style.isLocalKey( TextStyleKeys.TEXT_WRAP ) ) {
      final Object value = style.getStyleProperty( TextStyleKeys.TEXT_WRAP );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "text-wrap", value.toString() );
    }
    if ( style.isLocalKey( TextStyleKeys.WORDBREAK ) ) {
      final Object value = style.getStyleProperty( TextStyleKeys.WORDBREAK );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "word-break", value.toString() );
    }
    if ( style.isLocalKey( TextStyleKeys.DIRECTION ) ) {
      final Object value = style.getStyleProperty( TextStyleKeys.DIRECTION );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "direction", value.toString() );
    }
    if ( style.isLocalKey( TextStyleKeys.WHITE_SPACE_COLLAPSE ) ) {
      final Object value = style.getStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "whitespace-collapse", value.toString() );
    }
    if ( style.isLocalKey( TextStyleKeys.VERTICAL_TEXT_ALIGNMENT ) ) {
      final Object value = style.getStyleProperty( TextStyleKeys.VERTICAL_TEXT_ALIGNMENT );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "whitespace-collapse", value.toString() );
    }
    if ( style.isLocalKey( TextStyleKeys.TEXT_INDENT ) ) {
      final double value = style.getDoubleStyleProperty( TextStyleKeys.TEXT_INDENT, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "text-indent", formatFloat( value ) );
    }
    if ( style.isLocalKey( TextStyleKeys.FIRST_LINE_INDENT ) ) {
      final double value = style.getDoubleStyleProperty( TextStyleKeys.FIRST_LINE_INDENT, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "first-line-indent", formatFloat( value ) );
    }
    if ( bandStyleAtts.isEmpty() == false ) {
      writer.writeTag( BundleNamespaces.STYLE, "text-styles", bandStyleAtts, XmlWriterSupport.CLOSE );
    }
  }

  public static void writeContentStyles( final XmlWriter writer, final ElementStyleSheet style ) throws IOException {
    if ( writer == null ) {
      throw new NullPointerException();
    }
    if ( style == null ) {
      throw new NullPointerException();
    }

    final AttributeList bandStyleAtts = new AttributeList();
    if ( style.isLocalKey( ElementStyleKeys.DRAW_SHAPE ) ) {
      final boolean value = style.getBooleanStyleProperty( ElementStyleKeys.DRAW_SHAPE );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "draw-shape", String.valueOf( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.FILL_SHAPE ) ) {
      final boolean value = style.getBooleanStyleProperty( ElementStyleKeys.FILL_SHAPE );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "fill-shape", String.valueOf( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.SCALE ) ) {
      final boolean value = style.getBooleanStyleProperty( ElementStyleKeys.SCALE );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "scale", String.valueOf( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.KEEP_ASPECT_RATIO ) ) {
      final boolean value = style.getBooleanStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "keep-aspect-ratio", String.valueOf( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.DYNAMIC_HEIGHT ) ) {
      final boolean value = style.getBooleanStyleProperty( ElementStyleKeys.DYNAMIC_HEIGHT );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "dynamic-height", String.valueOf( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.PAINT ) ) {
      final Color value = (Color) style.getStyleProperty( ElementStyleKeys.PAINT );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "color", ColorValueConverter.colorToString( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.FILL_COLOR ) ) {
      final Color value = (Color) style.getStyleProperty( ElementStyleKeys.FILL_COLOR );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "fill-color", ColorValueConverter.colorToString( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.EXCEL_DATA_FORMAT_STRING ) ) {
      final String value = (String) style.getStyleProperty( ElementStyleKeys.EXCEL_DATA_FORMAT_STRING );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "excel-cell-format", value );
    }
    if ( style.isLocalKey( ElementStyleKeys.ANTI_ALIASING ) ) {
      final boolean value = style.getBooleanStyleProperty( ElementStyleKeys.ANTI_ALIASING );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "anti-aliasing", String.valueOf( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.STROKE ) ) {
      final Stroke s = (Stroke) style.getStyleProperty( ElementStyleKeys.STROKE );
      final float strokeWidth = StrokeUtility.getStrokeWidth( s );
      final BorderStyle strokeType = StrokeUtility.translateStrokeStyle( s );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "stroke-weight", getAbsoluteLengthFormat().format(
          strokeWidth ) );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "stroke-style", strokeType.toString() );
    }
    if ( bandStyleAtts.isEmpty() == false ) {
      writer.writeTag( BundleNamespaces.STYLE, "content-styles", bandStyleAtts, XmlWriterSupport.CLOSE );
    }
  }

  public static void writeCommonStyles( final XmlWriter writer, final ElementStyleSheet style ) throws IOException {
    if ( writer == null ) {
      throw new NullPointerException();
    }
    if ( style == null ) {
      throw new NullPointerException();
    }

    final AttributeList bandStyleAtts = new AttributeList();
    if ( style.isLocalKey( ElementStyleKeys.ANCHOR_NAME ) ) {
      final String value = (String) style.getStyleProperty( ElementStyleKeys.ANCHOR_NAME );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "anchor-name", value );
    }
    if ( style.isLocalKey( ElementStyleKeys.HREF_TARGET ) ) {
      final String value = (String) style.getStyleProperty( ElementStyleKeys.HREF_TARGET );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "href-target", value );
    }
    if ( style.isLocalKey( ElementStyleKeys.HREF_TITLE ) ) {
      final String value = (String) style.getStyleProperty( ElementStyleKeys.HREF_TITLE );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "href-title", value );
    }
    if ( style.isLocalKey( ElementStyleKeys.HREF_WINDOW ) ) {
      final String value = (String) style.getStyleProperty( ElementStyleKeys.HREF_WINDOW );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "href-window", value );
    }
    if ( style.isLocalKey( ElementStyleKeys.BOX_SIZING ) ) {
      final BoxSizing value = (BoxSizing) style.getStyleProperty( ElementStyleKeys.BOX_SIZING );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "box-sizing", value.toString() );
    }
    if ( style.isLocalKey( ElementStyleKeys.VISIBLE ) ) {
      final boolean value = style.getBooleanStyleProperty( ElementStyleKeys.VISIBLE );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "visible", String.valueOf( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.INVISIBLE_CONSUMES_SPACE ) ) {
      final boolean value = style.getBooleanStyleProperty( ElementStyleKeys.INVISIBLE_CONSUMES_SPACE );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "invisible-consumes-space", String.valueOf( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.WIDOWS ) ) {
      final int value = style.getIntStyleProperty( ElementStyleKeys.WIDOWS, -1 );
      if ( value >= 0 ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "widows", String.valueOf( value ) );
      }
    }
    if ( style.isLocalKey( ElementStyleKeys.ORPHANS ) ) {
      final int value = style.getIntStyleProperty( ElementStyleKeys.ORPHANS, -1 );
      if ( value >= 0 ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "orphans", String.valueOf( value ) );
      }
    }
    if ( style.isLocalKey( ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT ) ) {
      final boolean value = style.getBooleanStyleProperty( ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, ElementStyleKeys.WIDOW_ORPHAN_OPT_OUT.getName(), String
          .valueOf( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.OVERFLOW_X ) ) {
      final boolean value = style.getBooleanStyleProperty( ElementStyleKeys.OVERFLOW_X );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "overflow-x", String.valueOf( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.OVERFLOW_Y ) ) {
      final boolean value = style.getBooleanStyleProperty( ElementStyleKeys.OVERFLOW_Y );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "overflow-y", String.valueOf( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE ) ) {
      final boolean value = style.getBooleanStyleProperty( ElementStyleKeys.AVOID_PAGEBREAK_INSIDE );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "avoid-page-break", String.valueOf( value ) );
    }
    if ( style.isLocalKey( ElementStyleKeys.ALIGNMENT ) ) {
      final ElementAlignment value = (ElementAlignment) style.getStyleProperty( ElementStyleKeys.ALIGNMENT );
      if ( ElementAlignment.CENTER.equals( value ) ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "alignment", "center" );
      } else if ( ElementAlignment.RIGHT.equals( value ) ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "alignment", "right" );
      } else if ( ElementAlignment.JUSTIFY.equals( value ) ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "alignment", "justify" );
      } else if ( ElementAlignment.LEFT.equals( value ) ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "alignment", "left" );
      }
    }
    if ( style.isLocalKey( ElementStyleKeys.VALIGNMENT ) ) {
      final ElementAlignment value = (ElementAlignment) style.getStyleProperty( ElementStyleKeys.VALIGNMENT );
      if ( ElementAlignment.MIDDLE.equals( value ) ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "vertical-alignment", "middle" );
      } else if ( ElementAlignment.BOTTOM.equals( value ) ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "vertical-alignment", "bottom" );
      } else if ( ElementAlignment.TOP.equals( value ) ) {
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "vertical-alignment", "top" );
      }
    }
    if ( bandStyleAtts.isEmpty() == false ) {
      writer.writeTag( BundleNamespaces.STYLE, "common-styles", bandStyleAtts, XmlWriterSupport.CLOSE );
    }
  }

  public static void writePageBandStyles( final XmlWriter writer, final ElementStyleSheet style ) throws IOException {
    if ( writer == null ) {
      throw new NullPointerException();
    }
    if ( style == null ) {
      throw new NullPointerException();
    }

    final AttributeList bandStyleAtts = new AttributeList();
    if ( style.isLocalKey( BandStyleKeys.REPEAT_HEADER ) ) {
      final boolean value = style.getBooleanStyleProperty( BandStyleKeys.REPEAT_HEADER );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "repeat", String.valueOf( value ) );
    }
    if ( style.isLocalKey( BandStyleKeys.DISPLAY_ON_FIRSTPAGE ) ) {
      final boolean value = style.getBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_FIRSTPAGE );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "display-on-first-page", String.valueOf( value ) );
    }
    if ( style.isLocalKey( BandStyleKeys.DISPLAY_ON_LASTPAGE ) ) {
      final boolean value = style.getBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_LASTPAGE );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "display-on-last-page", String.valueOf( value ) );
    }
    if ( style.isLocalKey( BandStyleKeys.STICKY ) ) {
      final boolean value = style.getBooleanStyleProperty( BandStyleKeys.STICKY );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "sticky", String.valueOf( value ) );
    }
    if ( style.isLocalKey( BandStyleKeys.FIXED_POSITION ) ) {
      final double value = style.getDoubleStyleProperty( BandStyleKeys.FIXED_POSITION, 0 );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "fixed-position", formatLength( value ) );
    }
    if ( bandStyleAtts.isEmpty() == false ) {
      writer.writeTag( BundleNamespaces.STYLE, "page-band-styles", bandStyleAtts, XmlWriterSupport.CLOSE );
    }
  }

  private static String formatFloat( final double value ) {
    return getAbsoluteLengthFormat().format( value );
  }

  private static String formatLength( final double value ) {
    if ( value >= 0 ) {
      return getAbsoluteLengthFormat().format( value );
    } else {
      return getPercentageLengthFormat().format( -value );
    }
  }

  public static void writeBandStyles( final XmlWriter writer, final ElementStyleSheet style ) throws IOException {
    if ( writer == null ) {
      throw new NullPointerException();
    }
    if ( style == null ) {
      throw new NullPointerException();
    }

    final AttributeList bandStyleAtts = new AttributeList();
    if ( style.isLocalKey( BandStyleKeys.COMPUTED_SHEETNAME ) ) {
      final String value = (String) style.getStyleProperty( BandStyleKeys.COMPUTED_SHEETNAME );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "computed-sheetname", value );
    }
    if ( style.isLocalKey( BandStyleKeys.BOOKMARK ) ) {
      final String value = (String) style.getStyleProperty( BandStyleKeys.BOOKMARK );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "bookmark", value );
    }
    if ( style.isLocalKey( BandStyleKeys.PAGEBREAK_BEFORE ) ) {
      final boolean value = style.getBooleanStyleProperty( BandStyleKeys.PAGEBREAK_BEFORE );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "pagebreak-before", String.valueOf( value ) );
    }
    if ( style.isLocalKey( BandStyleKeys.PAGEBREAK_AFTER ) ) {
      final boolean value = style.getBooleanStyleProperty( BandStyleKeys.PAGEBREAK_AFTER );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "pagebreak-after", String.valueOf( value ) );
    }
    if ( style.isLocalKey( BandStyleKeys.LAYOUT ) ) {
      final String value = (String) style.getStyleProperty( BandStyleKeys.LAYOUT );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "layout", String.valueOf( value ) );
    }
    if ( style.isLocalKey( BandStyleKeys.TABLE_LAYOUT ) ) {
      try {
        final TableLayout value = (TableLayout) style.getStyleProperty( BandStyleKeys.TABLE_LAYOUT );
        final String valueAsString = ConverterRegistry.toAttributeValue( value );
        bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "table-layout", valueAsString );
      } catch ( BeanException e ) {
        throw new IOException( "Style 'table-layout' could not be written." );
      }
    }
    if ( bandStyleAtts.isEmpty() == false ) {
      writer.writeTag( BundleNamespaces.STYLE, "band-styles", bandStyleAtts, XmlWriterSupport.CLOSE );
    }
  }

  public static void writeRotationStyles( final XmlWriter writer, final ElementStyleSheet style ) throws IOException {
    if ( writer == null ) {
      throw new NullPointerException();
    }
    if ( style == null ) {
      throw new NullPointerException();
    }
    final AttributeList bandStyleAtts = new AttributeList();
    if ( style.isLocalKey( TextStyleKeys.TEXT_ROTATION ) ) {
      final Object value = style.getStyleProperty( TextStyleKeys.TEXT_ROTATION );
      bandStyleAtts.setAttribute( BundleNamespaces.STYLE, "rotation", value.toString() );
    }
    if ( bandStyleAtts.isEmpty() == false ) {
      writer.writeTag( BundleNamespaces.STYLE, "rotation-styles", bandStyleAtts, XmlWriterSupport.CLOSE );
    }
  }
}
