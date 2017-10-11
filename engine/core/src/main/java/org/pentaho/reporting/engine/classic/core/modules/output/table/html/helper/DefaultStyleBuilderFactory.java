/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.table.html.helper;

import java.awt.Color;
import java.text.NumberFormat;

import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.layout.model.Border;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderCorner;
import org.pentaho.reporting.engine.classic.core.layout.model.BorderEdge;
import org.pentaho.reporting.engine.classic.core.layout.model.context.BoxDefinition;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.CellBackground;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.util.HtmlColors;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.util.HtmlEncoderUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;
import org.pentaho.reporting.libraries.base.config.Configuration;

@SuppressWarnings( "HardCodedStringLiteral" )
public class DefaultStyleBuilderFactory implements StyleBuilderFactory {
  private boolean safariLengthFix;
  private boolean useWhitespacePreWrap;
  private boolean enableRoundBorderCorner;

  public DefaultStyleBuilderFactory() {
  }

  public boolean isSafariLengthFix() {
    return safariLengthFix;
  }

  public boolean isUseWhitespacePreWrap() {
    return useWhitespacePreWrap;
  }

  public boolean isEnableRoundBorderCorner() {
    return enableRoundBorderCorner;
  }

  public StyleBuilder produceTextStyle( StyleBuilder styleBuilder, final StyleSheet styleSheet,
      final BoxDefinition boxDefinition, final boolean includeBorder,
      final StyleBuilder.StyleCarrier[] parentElementStyle ) {
    if ( styleSheet == null ) {
      throw new NullPointerException();
    }
    if ( styleBuilder == null ) {
      styleBuilder = new DefaultStyleBuilder( this );
    } else {
      styleBuilder.clear();
    }

    final FilterStyleBuilder filterStyleBuilder = new FilterStyleBuilder( styleBuilder, parentElementStyle );
    final NumberFormat pointConverter = filterStyleBuilder.getPointConverter();
    final Color textColor = (Color) styleSheet.getStyleProperty( ElementStyleKeys.PAINT );
    final Color backgroundColor = (Color) styleSheet.getStyleProperty( ElementStyleKeys.BACKGROUND_COLOR );
    if ( backgroundColor != null ) {
      filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.BACKGROUND_COLOR, HtmlColors
          .getColorString( backgroundColor ) );
    }

    if ( includeBorder ) {
      processBorder( styleSheet, boxDefinition, filterStyleBuilder, pointConverter );
    }
    if ( textColor != null ) {
      filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.COLOR, HtmlColors.getColorString( textColor ) );
    }

    processFontStyle( styleSheet, filterStyleBuilder, pointConverter );
    processTextDecoration( styleSheet, filterStyleBuilder );

    final ElementAlignment align = (ElementAlignment) styleSheet.getStyleProperty( ElementStyleKeys.ALIGNMENT );
    filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.TEXT_ALIGN, translateHorizontalAlignment( align ) );

    final double wordSpacing = styleSheet.getDoubleStyleProperty( TextStyleKeys.WORD_SPACING, 0 );
    filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.WORD_SPACING, pointConverter
        .format( fixLengthForSafari( wordSpacing ) ), "pt" );

    final double minLetterSpacing = styleSheet.getDoubleStyleProperty( TextStyleKeys.X_MIN_LETTER_SPACING, 0 );
    final double maxLetterSpacing = styleSheet.getDoubleStyleProperty( TextStyleKeys.X_MAX_LETTER_SPACING, 0 );
    filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.LETTER_SPACING, pointConverter
        .format( fixLengthForSafari( Math.min( minLetterSpacing, maxLetterSpacing ) ) ), "pt" );

    processWhiteSpaceCollapse( styleSheet, filterStyleBuilder );

    return styleBuilder;
  }

  private void processWhiteSpaceCollapse( final StyleSheet styleSheet, final FilterStyleBuilder filterStyleBuilder ) {
    final WhitespaceCollapse wsCollapse =
        (WhitespaceCollapse) styleSheet.getStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE );
    if ( WhitespaceCollapse.PRESERVE.equals( wsCollapse ) ) {
      if ( useWhitespacePreWrap ) {
        // this style does not work for IE6 and IE7, but heck, in that case they just behave as if normal mode is
        // selected. In that case multiple spaces are collapsed into a single space.
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.WHITE_SPACE, "pre-wrap" );
      } else {
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.WHITE_SPACE, "pre" );
      }
    } else if ( WhitespaceCollapse.PRESERVE_BREAKS.equals( wsCollapse ) ) {
      filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.WHITE_SPACE, "nowrap" );
    } else {
      // discard is handled on the layouter level already;
      // collapse is the normal way of handling whitespaces in the engine.
      filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.WHITE_SPACE, "normal" );
    }
  }

  private void processFontStyle( final StyleSheet styleSheet, final FilterStyleBuilder filterStyleBuilder,
      final NumberFormat pointConverter ) {
    filterStyleBuilder.appendRaw( DefaultStyleBuilder.CSSKeys.FONT_FAMILY, translateFontFamily( styleSheet ) );
    filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.FONT_SIZE, pointConverter
        .format( fixLengthForSafari( styleSheet.getDoubleStyleProperty( TextStyleKeys.FONTSIZE, 0 ) ) ), "pt" );
    if ( styleSheet.getBooleanStyleProperty( TextStyleKeys.BOLD ) ) {
      filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.FONT_WEIGHT, "bold" );
    } else {
      filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.FONT_WEIGHT, "normal" );
    }

    if ( styleSheet.getBooleanStyleProperty( TextStyleKeys.ITALIC ) ) {
      filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.FONT_STYLE, "italic" );
    } else {
      filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.FONT_STYLE, "normal" );
    }
  }

  private void processTextDecoration( final StyleSheet styleSheet, final FilterStyleBuilder filterStyleBuilder ) {
    final boolean underlined = styleSheet.getBooleanStyleProperty( TextStyleKeys.UNDERLINED );
    final boolean strikeThrough = styleSheet.getBooleanStyleProperty( TextStyleKeys.STRIKETHROUGH );
    if ( underlined && strikeThrough ) {
      filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.TEXT_DECORATION, "underline line-through" );
    } else if ( strikeThrough ) {
      filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.TEXT_DECORATION, "line-through" );
    }
    if ( underlined ) {
      filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.TEXT_DECORATION, "underline" );
    } else {
      filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.TEXT_DECORATION, "none" );
    }
  }

  private void processBorder( final StyleSheet styleSheet, final BoxDefinition boxDefinition,
      final FilterStyleBuilder filterStyleBuilder, final NumberFormat pointConverter ) {
    final Border border = boxDefinition.getBorder();
    final BorderEdge top = border.getTop();
    final BorderEdge left = border.getLeft();
    final BorderEdge bottom = border.getBottom();
    final BorderEdge right = border.getRight();
    if ( top.equals( left ) && top.equals( right ) && top.equals( bottom ) ) {
      if ( BorderEdge.EMPTY.equals( top ) == false ) {
        filterStyleBuilder.appendRaw( DefaultStyleBuilder.CSSKeys.BORDER, filterStyleBuilder.printEdgeAsCSS( top ) );
      }
    } else {
      if ( top != null && BorderEdge.EMPTY.equals( top ) == false ) {
        filterStyleBuilder.appendRaw( DefaultStyleBuilder.CSSKeys.BORDER_TOP, filterStyleBuilder.printEdgeAsCSS( top ) );
      }
      if ( left != null && BorderEdge.EMPTY.equals( left ) == false ) {
        filterStyleBuilder
            .appendRaw( DefaultStyleBuilder.CSSKeys.BORDER_LEFT, filterStyleBuilder.printEdgeAsCSS( left ) );
      }
      if ( bottom != null && BorderEdge.EMPTY.equals( bottom ) == false ) {
        filterStyleBuilder.appendRaw( DefaultStyleBuilder.CSSKeys.BORDER_BOTTOM, filterStyleBuilder
            .printEdgeAsCSS( bottom ) );
      }
      if ( right != null && BorderEdge.EMPTY.equals( right ) == false ) {
        filterStyleBuilder.appendRaw( DefaultStyleBuilder.CSSKeys.BORDER_RIGHT, filterStyleBuilder
            .printEdgeAsCSS( right ) );
      }
    }
    if ( enableRoundBorderCorner ) {
      final double blW =
          Math.max( 0, styleSheet.getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH, 0 ) );
      final double blH =
          Math.max( 0, styleSheet.getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT, 0 ) );
      if ( blW > 0 && blH > 0 ) {
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_BOTTOM_LEFT, pointConverter
            .format( fixLengthForSafari( blW ) )
            + "pt " + pointConverter.format( fixLengthForSafari( blH ) ) + "pt" );
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.BORDER_BOTTOM_LEFT_RADIUS, pointConverter
            .format( fixLengthForSafari( blW ) )
            + "pt " + pointConverter.format( fixLengthForSafari( blH ) ) + "pt" );
      }

      final double brW =
          Math.max( 0, styleSheet.getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH, 0 ) );
      final double brH =
          Math.max( 0, styleSheet.getDoubleStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT, 0 ) );
      if ( brW > 0 && brH > 0 ) {
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_BOTTOM_RIGHT, pointConverter
            .format( fixLengthForSafari( brW ) )
            + "pt " + pointConverter.format( fixLengthForSafari( brH ) ) + "pt" );
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.BORDER_BOTTOM_RIGHT_RADIUS, pointConverter
            .format( fixLengthForSafari( brW ) )
            + "pt " + pointConverter.format( fixLengthForSafari( brH ) ) + "pt" );
      }

      final double tlW =
          Math.max( 0, styleSheet.getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH, 0 ) );
      final double tlH =
          Math.max( 0, styleSheet.getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT, 0 ) );
      if ( tlW > 0 && tlH > 0 ) {
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_TOP_LEFT, pointConverter
            .format( fixLengthForSafari( tlW ) )
            + "pt " + pointConverter.format( fixLengthForSafari( tlH ) ) + "pt" );
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.BORDER_TOP_LEFT_RADIUS, pointConverter
            .format( fixLengthForSafari( tlW ) )
            + "pt " + pointConverter.format( fixLengthForSafari( tlH ) ) + "pt" );
      }

      final double trW =
          Math.max( 0, styleSheet.getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH, 0 ) );
      final double trH =
          Math.max( 0, styleSheet.getDoubleStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT, 0 ) );
      if ( trW > 0 && trH > 0 ) {
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_TOP_RIGHT, pointConverter
            .format( fixLengthForSafari( trW ) )
            + "pt " + pointConverter.format( fixLengthForSafari( trH ) ) + "pt" );
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.BORDER_TOP_RIGHT_RADIUS, pointConverter
            .format( fixLengthForSafari( trW ) )
            + "pt " + pointConverter.format( fixLengthForSafari( trH ) ) + "pt" );
      }
    }

    final long paddingTop = boxDefinition.getPaddingTop();
    final long paddingLeft = boxDefinition.getPaddingLeft();
    final long paddingBottom = boxDefinition.getPaddingBottom();
    final long paddingRight = boxDefinition.getPaddingRight();
    if ( paddingTop == paddingLeft && paddingTop == paddingRight && paddingTop == paddingBottom ) {
      if ( paddingTop > 0 ) {
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.PADDING, pointConverter
            .format( fixLengthForSafari( StrictGeomUtility.toExternalValue( paddingTop ) ) ), "pt" );
      }
    } else {
      if ( paddingTop > 0 ) {
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.PADDING_TOP, pointConverter
            .format( fixLengthForSafari( StrictGeomUtility.toExternalValue( paddingTop ) ) ), "pt" );
      }
      if ( paddingLeft > 0 ) {
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.PADDING_LEFT, pointConverter
            .format( fixLengthForSafari( StrictGeomUtility.toExternalValue( paddingLeft ) ) ), "pt" );
      }
      if ( paddingBottom > 0 ) {
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.PADDING_BOTTOM, pointConverter
            .format( fixLengthForSafari( StrictGeomUtility.toExternalValue( paddingBottom ) ) ), "pt" );
      }
      if ( paddingRight > 0 ) {
        filterStyleBuilder.append( DefaultStyleBuilder.CSSKeys.PADDING_RIGHT, pointConverter
            .format( fixLengthForSafari( StrictGeomUtility.toExternalValue( paddingRight ) ) ), "pt" );
      }
    }
  }

  private static String translateFontFamily( final StyleSheet box ) {
    final String family = (String) box.getStyleProperty( TextStyleKeys.FONT );
    if ( "Serif".equalsIgnoreCase( family ) ) {
      return "serif";
    } else if ( "Sans-serif".equalsIgnoreCase( family ) || "SanSerif".equalsIgnoreCase( family )
        || "SansSerif".equalsIgnoreCase( family ) || "Dialog".equalsIgnoreCase( family )
        || "DialogInput".equalsIgnoreCase( family ) ) {
      return "sans-serif";
    } else if ( "Monospaced".equalsIgnoreCase( family ) ) {
      return "monospace";
    } else {
      return '\"' + HtmlEncoderUtil.encodeCSS( family ) + '\"';
    }
  }

  public double fixLengthForSafari( final double border ) {
    if ( safariLengthFix == false ) {
      return border;
    }
    if ( border == 0 ) {
      return 0;
    }
    return Math.max( 1, Math.round( border ) );
  }

  /**
   * Translates the JFreeReport horizontal element alignment into a HTML alignment constant.
   *
   * @param ea
   *          the element alignment
   * @return the translated alignment name.
   */
  public static String translateHorizontalAlignment( final ElementAlignment ea ) {
    if ( ElementAlignment.JUSTIFY.equals( ea ) ) {
      return "justify";
    }
    if ( ElementAlignment.RIGHT.equals( ea ) ) {
      return "right";
    }
    if ( ElementAlignment.CENTER.equals( ea ) ) {
      return "center";
    }
    return "left";
  }

  public void configure( final Configuration configuration ) {
    safariLengthFix =
        ( "true"
            .equals( configuration
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.SafariLengthHack" ) ) );
    useWhitespacePreWrap =
        ( "true"
            .equals( configuration
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.UseWhitespacePreWrap" ) ) );
    enableRoundBorderCorner =
        ( "true"
            .equals( configuration
                .getConfigProperty( "org.pentaho.reporting.engine.classic.core.modules.output.table.html.EnableRoundBorderCorner" ) ) );
  }

  public StyleBuilder createCellStyle( StyleBuilder styleBuilder, final CellBackground background,
      final StyleBuilder.CSSKeys[] extraStyleKeys, final String[] extraStyleValues ) {
    return createCellStyle( styleBuilder, null, null, background, extraStyleKeys, extraStyleValues );
  }

  public StyleBuilder createCellStyle( StyleBuilder styleBuilder, final StyleSheet styleSheet,
      final BoxDefinition boxDefinition, final CellBackground background, final StyleBuilder.CSSKeys[] extraStyleKeys,
      final String[] extraStyleValues ) {
    if ( styleBuilder == null ) {
      throw new NullPointerException();
    }
    if ( styleSheet == null ) {
      styleBuilder.clear();
    } else {
      styleBuilder = produceTextStyle( styleBuilder, styleSheet, boxDefinition, true, null );
    }

    // Add the extra styles
    if ( extraStyleKeys != null && extraStyleValues != null && extraStyleKeys.length == extraStyleValues.length ) {
      for ( int i = 0; i < extraStyleKeys.length; ++i ) {
        styleBuilder.append( extraStyleKeys[i], extraStyleValues[i], false );
      }
    }

    if ( background != null ) {
      final Color colorValue = ( background.getBackgroundColor() );
      if ( colorValue != null ) {
        styleBuilder.append( DefaultStyleBuilder.CSSKeys.BACKGROUND_COLOR, HtmlColors.getColorString( colorValue ) );
      }

      final BorderEdge topEdge = background.getTop();
      final BorderEdge leftEdge = background.getLeft();
      final BorderEdge bottomEdge = background.getBottom();
      final BorderEdge rightEdge = background.getRight();
      if ( topEdge.equals( leftEdge ) && topEdge.equals( rightEdge ) && topEdge.equals( bottomEdge ) ) {
        if ( BorderEdge.EMPTY.equals( topEdge ) == false ) {
          styleBuilder.appendRaw( DefaultStyleBuilder.CSSKeys.BORDER, styleBuilder.printEdgeAsCSS( topEdge ) );
        }
      } else {
        if ( BorderEdge.EMPTY.equals( topEdge ) == false ) {
          styleBuilder.appendRaw( DefaultStyleBuilder.CSSKeys.BORDER_TOP, styleBuilder.printEdgeAsCSS( topEdge ) );
        }
        if ( BorderEdge.EMPTY.equals( leftEdge ) == false ) {
          styleBuilder.appendRaw( DefaultStyleBuilder.CSSKeys.BORDER_LEFT, styleBuilder.printEdgeAsCSS( leftEdge ) );
        }
        if ( BorderEdge.EMPTY.equals( bottomEdge ) == false ) {
          styleBuilder.appendRaw( DefaultStyleBuilder.CSSKeys.BORDER_BOTTOM, styleBuilder.printEdgeAsCSS( bottomEdge ) );
        }
        if ( BorderEdge.EMPTY.equals( rightEdge ) == false ) {
          styleBuilder.appendRaw( DefaultStyleBuilder.CSSKeys.BORDER_RIGHT, styleBuilder.printEdgeAsCSS( rightEdge ) );
        }
      }

      if ( isEnableRoundBorderCorner() ) {
        final BorderCorner topLeft = background.getTopLeft();
        if ( BorderCorner.EMPTY.equals( topLeft ) == false ) {
          styleBuilder.append( DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_TOP_LEFT, styleBuilder
              .printCornerAsCSS( topLeft ) );
          styleBuilder.append( DefaultStyleBuilder.CSSKeys.BORDER_TOP_LEFT_RADIUS, styleBuilder
              .printCornerAsCSS( topLeft ) );
        }

        final BorderCorner topRight = background.getTopRight();
        if ( BorderCorner.EMPTY.equals( topRight ) == false ) {
          styleBuilder.append( DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_TOP_RIGHT, styleBuilder
              .printCornerAsCSS( topRight ) );
          styleBuilder.append( DefaultStyleBuilder.CSSKeys.BORDER_TOP_RIGHT_RADIUS, styleBuilder
              .printCornerAsCSS( topRight ) );
        }

        final BorderCorner bottomLeft = background.getBottomLeft();
        if ( BorderCorner.EMPTY.equals( bottomLeft ) == false ) {
          styleBuilder.append( DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_BOTTOM_LEFT, styleBuilder
              .printCornerAsCSS( bottomLeft ) );
          styleBuilder.append( DefaultStyleBuilder.CSSKeys.BORDER_BOTTOM_LEFT_RADIUS, styleBuilder
              .printCornerAsCSS( bottomLeft ) );
        }

        final BorderCorner bottomRight = background.getBottomRight();
        if ( BorderCorner.EMPTY.equals( bottomRight ) == false ) {
          styleBuilder.append( DefaultStyleBuilder.CSSKeys.MOZ_BORDER_RADIUS_BOTTOM_RIGHT, styleBuilder
              .printCornerAsCSS( bottomRight ) );
          styleBuilder.append( DefaultStyleBuilder.CSSKeys.BORDER_BOTTOM_RIGHT_RADIUS, styleBuilder
              .printCornerAsCSS( bottomRight ) );
        }
      }
    }
    return styleBuilder;
  }

}
