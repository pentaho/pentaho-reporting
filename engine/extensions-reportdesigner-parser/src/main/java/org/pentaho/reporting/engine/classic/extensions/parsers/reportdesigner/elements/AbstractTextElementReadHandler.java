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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.ElementAlignment;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.IntegerCache;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.ColorConverter;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.converter.FontConverter;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.report.FormulaReadHandler;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.awt.*;
import java.util.Properties;

public abstract class AbstractTextElementReadHandler extends AbstractReportElementReadHandler {
  private Element element;
  private FormulaReadHandler formulaReadHandler;

  public AbstractTextElementReadHandler() {
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) ) {
      if ( "formula".equals( tagName ) ) {
        formulaReadHandler = new FormulaReadHandler();
        return formulaReadHandler;
      }
    }
    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    if ( formulaReadHandler != null ) {
      final Expression expression = formulaReadHandler.getFormula();
      if ( expression != null ) {
        element.setAttributeExpression
          ( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, expression );
      }
    }

    final Properties result = getResult();
    final String fieldName = result.getProperty( "fieldName" );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD, fieldName );

    final String nullString = result.getProperty( "nullString" );
    element.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.NULL_VALUE, nullString );

    final String fontString = result.getProperty( "font" );
    if ( fontString != null ) {
      final Font font = (Font) new FontConverter().convertFromString( fontString, getLocator() );
      element.getStyle().setStyleProperty( TextStyleKeys.BOLD, ( font.isBold() ) ? Boolean.TRUE : Boolean.FALSE );
      element.getStyle().setStyleProperty( TextStyleKeys.ITALIC, ( font.isItalic() ) ? Boolean.TRUE : Boolean.FALSE );
      element.getStyle().setStyleProperty( TextStyleKeys.FONT, font.getName() );
      element.getStyle().setStyleProperty( TextStyleKeys.FONTSIZE, IntegerCache.getInteger( font.getSize() ) );
    }
    final String foreGround = result.getProperty( "foreground" );
    if ( foreGround != null ) {
      element.getStyle().setStyleProperty( ElementStyleKeys.PAINT, ColorConverter.getObject( foreGround ) );
    }
    final String strikethough = result.getProperty( "strikethough" );
    if ( strikethough != null ) {
      if ( "true".equals( strikethough ) ) {
        element.getStyle().setStyleProperty( TextStyleKeys.STRIKETHROUGH, Boolean.TRUE );
      } else {
        element.getStyle().setStyleProperty( TextStyleKeys.STRIKETHROUGH, Boolean.FALSE );
      }
    }
    final String underline = result.getProperty( "underline" );
    if ( underline != null ) {
      if ( "true".equals( underline ) ) {
        element.getStyle().setStyleProperty( TextStyleKeys.UNDERLINED, Boolean.TRUE );
      } else {
        element.getStyle().setStyleProperty( TextStyleKeys.UNDERLINED, Boolean.FALSE );
      }
    }

    final String embedFont = result.getProperty( "embedFont" );
    if ( embedFont != null ) {
      if ( "true".equals( embedFont ) ) {
        element.getStyle().setStyleProperty( TextStyleKeys.EMBEDDED_FONT, Boolean.TRUE );
      } else {
        element.getStyle().setStyleProperty( TextStyleKeys.EMBEDDED_FONT, Boolean.FALSE );
      }
    }

    final String lineHeight = result.getProperty( "lineHeight" );
    if ( lineHeight != null ) {
      final float f = ParserUtil.parseFloat( lineHeight, "Failed to parse float-value", getLocator() );
      element.getStyle().setStyleProperty( TextStyleKeys.LINEHEIGHT, new Float( f ) );
    }

    final String verticalAlignment = result.getProperty( "verticalAlignment" );
    if ( verticalAlignment != null ) {
      if ( "TOP".equals( verticalAlignment ) ) {
        element.getStyle().setStyleProperty( ElementStyleKeys.VALIGNMENT, ElementAlignment.TOP );
      } else if ( "MIDDLE".equals( verticalAlignment ) ) {
        element.getStyle().setStyleProperty( ElementStyleKeys.VALIGNMENT, ElementAlignment.MIDDLE );
      } else if ( "BOTTOM".equals( verticalAlignment ) ) {
        element.getStyle().setStyleProperty( ElementStyleKeys.VALIGNMENT, ElementAlignment.BOTTOM );
      }
    }

    final String horizontalAlignment = result.getProperty( "horizontalAlignment" );
    if ( horizontalAlignment != null ) {
      if ( "LEFT".equals( horizontalAlignment ) ) {
        element.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.LEFT );
      } else if ( "CENTER".equals( horizontalAlignment ) ) {
        element.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.CENTER );
      } else if ( "RIGHT".equals( horizontalAlignment ) ) {
        element.getStyle().setStyleProperty( ElementStyleKeys.ALIGNMENT, ElementAlignment.RIGHT );
      }
    }

    final String reservedLiteral = result.getProperty( "reservedLiteral" );
    if ( reservedLiteral != null ) {
      element.getStyle().setStyleProperty( TextStyleKeys.RESERVED_LITERAL, reservedLiteral );
    }

    final String trimTextContent = result.getProperty( "timeTextContent" );
    if ( trimTextContent != null ) {
      element.getStyle().setStyleProperty( TextStyleKeys.TRIM_TEXT_CONTENT, trimTextContent );
    }

    final String wrapTextInExcel = result.getProperty( "wrapTextInExcel" );
    if ( wrapTextInExcel != null ) {
      if ( "true".equals( wrapTextInExcel ) ) {
        element.getStyle().setStyleProperty( ElementStyleKeys.EXCEL_WRAP_TEXT, Boolean.TRUE );
      } else {
        element.getStyle().setStyleProperty( ElementStyleKeys.EXCEL_WRAP_TEXT, Boolean.FALSE );
      }
    }

    final String encoding = result.getProperty( "encoding" );
    if ( encoding != null ) {
      element.getStyle().setStyleProperty( TextStyleKeys.FONTENCODING, encoding );
    }

  }

  protected void setElement( Element element ) {
    this.element = element;
  }

  protected Element getElement() {
    return element;
  }
}
