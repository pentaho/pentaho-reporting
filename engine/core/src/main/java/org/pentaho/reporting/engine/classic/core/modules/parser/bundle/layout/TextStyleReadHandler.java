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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.FontSmooth;
import org.pentaho.reporting.engine.classic.core.style.TextDirection;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextWrap;
import org.pentaho.reporting.engine.classic.core.style.VerticalTextAlign;
import org.pentaho.reporting.engine.classic.core.style.WhitespaceCollapse;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class TextStyleReadHandler extends AbstractXmlReadHandler implements StyleReadHandler {
  private static final Log logger = LogFactory.getLog( TextStyleReadHandler.class );
  private ElementStyleSheet styleSheet;

  public TextStyleReadHandler() {
  }

  public ElementStyleSheet getStyleSheet() {
    return styleSheet;
  }

  public void setStyleSheet( final ElementStyleSheet styleSheet ) {
    this.styleSheet = styleSheet;
  }

  /**
   * Starts parsing.
   * <p/>
   * font-smooth="always" text-wrap="wrap" vertical-text-alignment="top" whitespace-collapse="collapse"
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    final String wordSpacing = attrs.getValue( getUri(), "word-spacing" );
    if ( wordSpacing != null ) {
      styleSheet
          .setStyleProperty( TextStyleKeys.WORD_SPACING, ReportParserUtil.parseFloat( wordSpacing, getLocator() ) );
    }
    final String minLetterSpacing = attrs.getValue( getUri(), "min-letter-spacing" );
    if ( minLetterSpacing != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.X_MIN_LETTER_SPACING, ReportParserUtil.parseFloat( minLetterSpacing,
          getLocator() ) );
    }
    final String maxLetterSpacing = attrs.getValue( getUri(), "max-letter-spacing" );
    if ( maxLetterSpacing != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.X_MAX_LETTER_SPACING, ReportParserUtil.parseFloat( maxLetterSpacing,
          getLocator() ) );
    }
    final String optimumLetterSpacing = attrs.getValue( getUri(), "optimum-letter-spacing" );
    if ( optimumLetterSpacing != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.X_OPTIMUM_LETTER_SPACING, ReportParserUtil.parseFloat(
          optimumLetterSpacing, getLocator() ) );
    }
    final String reservedLiteral = attrs.getValue( getUri(), "ellipsis" );
    if ( "UTF-8".equals( reservedLiteral ) ) {
      logger.info( "Auto-corrected invalid ellipsis text [PRD-3315]" );
      styleSheet.setStyleProperty( TextStyleKeys.RESERVED_LITERAL, null );
    } else if ( reservedLiteral != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.RESERVED_LITERAL, reservedLiteral );
    }
    final String fontName = attrs.getValue( getUri(), "font-face" );
    if ( fontName != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.FONT, fontName );
    }
    final String fontEncoding = attrs.getValue( getUri(), "encoding" );
    if ( fontEncoding != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.FONTENCODING, fontEncoding );
    }
    final String fontSize = attrs.getValue( getUri(), "font-size" );
    if ( fontSize != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.FONTSIZE, ReportParserUtil.parseInteger( fontSize, getLocator() ) );
    }
    final String lineHeight = attrs.getValue( getUri(), "line-height" );
    if ( lineHeight != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.LINEHEIGHT, ReportParserUtil.parseFloat( lineHeight, getLocator() ) );
    }
    final String fontBold = attrs.getValue( getUri(), "bold" );
    if ( fontBold != null ) {
      styleSheet.setBooleanStyleProperty( TextStyleKeys.BOLD, "true".equals( fontBold ) );
    }
    final String fontEmbedded = attrs.getValue( getUri(), "embedded" );
    if ( fontEmbedded != null ) {
      styleSheet.setBooleanStyleProperty( TextStyleKeys.EMBEDDED_FONT, "true".equals( fontEmbedded ) );
    }
    final String fontItalics = attrs.getValue( getUri(), "italic" );
    if ( fontItalics != null ) {
      styleSheet.setBooleanStyleProperty( TextStyleKeys.ITALIC, "true".equals( fontItalics ) );
    }
    final String fontUnderline = attrs.getValue( getUri(), "underline" );
    if ( fontUnderline != null ) {
      styleSheet.setBooleanStyleProperty( TextStyleKeys.UNDERLINED, "true".equals( fontUnderline ) );
    }
    final String fontStrikethrough = attrs.getValue( getUri(), "strikethrough" );
    if ( fontStrikethrough != null ) {
      styleSheet.setBooleanStyleProperty( TextStyleKeys.STRIKETHROUGH, "true".equals( fontStrikethrough ) );
    }
    final String trimTextContent = attrs.getValue( getUri(), "trim-text-content" );
    if ( trimTextContent != null ) {
      styleSheet.setBooleanStyleProperty( TextStyleKeys.TRIM_TEXT_CONTENT, "true".equals( trimTextContent ) );
    }
    final String excelTextWrap = attrs.getValue( getUri(), "excel-text-wrapping" );
    if ( excelTextWrap != null ) {
      styleSheet.setBooleanStyleProperty( ElementStyleKeys.EXCEL_WRAP_TEXT, "true".equals( excelTextWrap ) );
    }
    final String excelIndention = attrs.getValue( getUri(), "excel-text-indention" );
    if ( excelIndention != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.EXCEL_INDENTION, ReportParserUtil.parseInteger( excelIndention,
          getLocator() ).shortValue() );
    }
    final String wsCollapse = attrs.getValue( getUri(), "whitespace-collapse" );
    if ( wsCollapse != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.WHITE_SPACE_COLLAPSE, parseWhitespaceCollapse( wsCollapse ) );
    }
    final String textWrap = attrs.getValue( getUri(), "text-wrap" );
    if ( textWrap != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.TEXT_WRAP, parseTextWrap( textWrap ) );
    }
    final String wordBreak = attrs.getValue( getUri(), "word-break" );
    if ( wordBreak != null ) {
      styleSheet.setBooleanStyleProperty( TextStyleKeys.WORDBREAK, "true".equals( wordBreak ) );
    }
    final String textDirection = attrs.getValue( getUri(), "direction" );
    if ( textDirection != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.DIRECTION, parseTextDirection( textDirection ) );
    }

    final String fontSmooth = attrs.getValue( getUri(), "font-smooth" );
    if ( fontSmooth != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.FONT_SMOOTH, parseFontSmooth( fontSmooth ) );
    }

    final String verticalTextAlignment = attrs.getValue( getUri(), "vertical-text-alignment" );
    if ( verticalTextAlignment != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.VERTICAL_TEXT_ALIGNMENT,
          parseVerticalTextAlign( verticalTextAlignment ) );
    }

    final String firstLineIndent = attrs.getValue( getUri(), "first-line-indent" );
    if ( firstLineIndent != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.FIRST_LINE_INDENT, ReportParserUtil.parseFloat( firstLineIndent,
          getLocator() ) );
    }

    final String textIndent = attrs.getValue( getUri(), "text-indent" );
    if ( textIndent != null ) {
      styleSheet.setStyleProperty( TextStyleKeys.TEXT_INDENT, ReportParserUtil.parseFloat( textIndent, getLocator() ) );
    }
  }

  private WhitespaceCollapse parseWhitespaceCollapse( final String attr ) {
    if ( WhitespaceCollapse.DISCARD.toString().equalsIgnoreCase( attr ) ) {
      return WhitespaceCollapse.DISCARD;
    }
    if ( WhitespaceCollapse.COLLAPSE.toString().equalsIgnoreCase( attr ) ) {
      return WhitespaceCollapse.COLLAPSE;
    }
    if ( WhitespaceCollapse.PRESERVE.toString().equalsIgnoreCase( attr ) ) {
      return WhitespaceCollapse.PRESERVE;
    }
    if ( WhitespaceCollapse.PRESERVE_BREAKS.toString().equalsIgnoreCase( attr ) ) {
      return WhitespaceCollapse.PRESERVE_BREAKS;
    }
    return null;
  }

  private VerticalTextAlign parseVerticalTextAlign( final String attr ) {
    if ( VerticalTextAlign.USE_SCRIPT.toString().equalsIgnoreCase( attr ) ) {
      return VerticalTextAlign.USE_SCRIPT;
    }
    if ( VerticalTextAlign.TEXT_BOTTOM.toString().equalsIgnoreCase( attr ) ) {
      return VerticalTextAlign.TEXT_BOTTOM;
    }
    if ( VerticalTextAlign.BOTTOM.toString().equalsIgnoreCase( attr ) ) {
      return VerticalTextAlign.BOTTOM;
    }
    if ( VerticalTextAlign.TEXT_TOP.toString().equalsIgnoreCase( attr ) ) {
      return VerticalTextAlign.TEXT_TOP;
    }
    if ( VerticalTextAlign.TOP.toString().equalsIgnoreCase( attr ) ) {
      return VerticalTextAlign.TOP;
    }
    if ( VerticalTextAlign.CENTRAL.toString().equalsIgnoreCase( attr ) ) {
      return VerticalTextAlign.CENTRAL;
    }
    if ( VerticalTextAlign.MIDDLE.toString().equalsIgnoreCase( attr ) ) {
      return VerticalTextAlign.MIDDLE;
    }
    if ( VerticalTextAlign.SUB.toString().equalsIgnoreCase( attr ) ) {
      return VerticalTextAlign.SUB;
    }
    if ( VerticalTextAlign.SUPER.toString().equalsIgnoreCase( attr ) ) {
      return VerticalTextAlign.SUPER;
    }
    if ( VerticalTextAlign.BASELINE.toString().equalsIgnoreCase( attr ) ) {
      return VerticalTextAlign.BASELINE;
    }
    return null;
  }

  private FontSmooth parseFontSmooth( final String attr ) {
    if ( FontSmooth.ALWAYS.toString().equalsIgnoreCase( attr ) ) {
      return FontSmooth.ALWAYS;
    }
    if ( FontSmooth.NEVER.toString().equalsIgnoreCase( attr ) ) {
      return FontSmooth.NEVER;
    }
    return FontSmooth.AUTO;
  }

  private TextWrap parseTextWrap( final String attr ) {
    if ( TextWrap.WRAP.toString().equalsIgnoreCase( attr ) ) {
      return TextWrap.WRAP;
    }
    if ( TextWrap.NONE.toString().equalsIgnoreCase( attr ) ) {
      return TextWrap.NONE;
    }
    return TextWrap.WRAP;
  }

  private TextDirection parseTextDirection( final String o ) {
    if ( TextDirection.LTR.toString().equalsIgnoreCase( o ) ) {
      return TextDirection.LTR;
    }
    if ( TextDirection.RTL.toString().equalsIgnoreCase( o ) ) {
      return TextDirection.RTL;
    }
    return TextDirection.LTR;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return styleSheet;
  }
}
