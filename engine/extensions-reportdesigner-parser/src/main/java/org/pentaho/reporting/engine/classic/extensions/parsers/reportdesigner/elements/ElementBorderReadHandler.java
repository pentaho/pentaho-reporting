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

import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ElementBorderReadHandler extends AbstractXmlReadHandler {
  private BorderEdgeReadHandler borderTop;
  private BorderEdgeReadHandler borderLeft;
  private BorderEdgeReadHandler borderBottom;
  private BorderEdgeReadHandler borderRight;
  private BorderEdgeReadHandler borderBreak;

  private BorderCornerReadHandler borderTopLeft;
  private BorderCornerReadHandler borderTopRight;
  private BorderCornerReadHandler borderBottomLeft;
  private BorderCornerReadHandler borderBottomRight;
  private ElementStyleSheet elementStyleSheet;

  private boolean sameBorder;

  public ElementBorderReadHandler( final ElementStyleSheet elementStyleSheet ) {
    this.elementStyleSheet = elementStyleSheet;
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    super.startParsing( attrs );
    sameBorder = "true".equals( attrs.getValue( getUri(), "sameBorderForAllSides" ) );
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
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "borderTop".equals( tagName ) ) {
      borderTop = new BorderEdgeReadHandler();
      return borderTop;
    }
    if ( "borderLeft".equals( tagName ) ) {
      borderLeft = new BorderEdgeReadHandler();
      return borderLeft;
    }
    if ( "borderBottom".equals( tagName ) ) {
      borderBottom = new BorderEdgeReadHandler();
      return borderBottom;
    }
    if ( "borderRight".equals( tagName ) ) {
      borderRight = new BorderEdgeReadHandler();
      return borderRight;
    }
    if ( "borderBreak".equals( tagName ) ) {
      borderBreak = new BorderEdgeReadHandler();
      return borderBreak;
    }

    if ( "topLeftEdge".equals( tagName ) ) {
      borderTopLeft = new BorderCornerReadHandler();
      return borderTopLeft;
    }
    if ( "topRightEdge".equals( tagName ) ) {
      borderTopRight = new BorderCornerReadHandler();
      return borderTopRight;
    }
    if ( "bottomLeftEdge".equals( tagName ) ) {
      borderBottomLeft = new BorderCornerReadHandler();
      return borderBottomLeft;
    }
    if ( "bottomRightEdge".equals( tagName ) ) {
      borderBottomRight = new BorderCornerReadHandler();
      return borderBottomRight;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    apply( elementStyleSheet );
  }

  private void apply( ElementStyleSheet s ) {
    if ( sameBorder ) {
      borderLeft = borderTop;
      borderRight = borderTop;
      borderBottom = borderTop;

      borderTopRight = borderTopLeft;
      borderBottomLeft = borderTopLeft;
      borderBottomRight = borderTopLeft;
    }

    if ( borderTop != null ) {
      s.setStyleProperty( ElementStyleKeys.BORDER_TOP_STYLE, borderTop.getBorderType() );
      s.setStyleProperty( ElementStyleKeys.BORDER_TOP_WIDTH, new Float( borderTop.getWidth() ) );
      s.setStyleProperty( ElementStyleKeys.BORDER_TOP_COLOR, borderTop.getColor() );
    }
    if ( borderLeft != null ) {
      s.setStyleProperty( ElementStyleKeys.BORDER_LEFT_STYLE, borderLeft.getBorderType() );
      s.setStyleProperty( ElementStyleKeys.BORDER_LEFT_WIDTH, new Float( borderLeft.getWidth() ) );
      s.setStyleProperty( ElementStyleKeys.BORDER_LEFT_COLOR, borderLeft.getColor() );
    }
    if ( borderBottom != null ) {
      s.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_STYLE, borderBottom.getBorderType() );
      s.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_WIDTH, new Float( borderBottom.getWidth() ) );
      s.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_COLOR, borderBottom.getColor() );
    }
    if ( borderRight != null ) {
      s.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_STYLE, borderRight.getBorderType() );
      s.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_WIDTH, new Float( borderRight.getWidth() ) );
      s.setStyleProperty( ElementStyleKeys.BORDER_RIGHT_COLOR, borderRight.getColor() );
    }
    if ( borderBreak != null ) {
      s.setStyleProperty( ElementStyleKeys.BORDER_BREAK_STYLE, borderBreak.getBorderType() );
      s.setStyleProperty( ElementStyleKeys.BORDER_BREAK_WIDTH, new Float( borderBreak.getWidth() ) );
      s.setStyleProperty( ElementStyleKeys.BORDER_BREAK_COLOR, borderBreak.getColor() );
    }


    if ( borderBottomLeft != null ) {
      s.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_HEIGHT, borderBottomLeft.getHeight() );
      s.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_LEFT_RADIUS_WIDTH, borderBottomLeft.getWidth() );
    }
    if ( borderBottomRight != null ) {
      s.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_HEIGHT, borderBottomRight.getHeight() );
      s.setStyleProperty( ElementStyleKeys.BORDER_BOTTOM_RIGHT_RADIUS_WIDTH, borderBottomRight.getWidth() );
    }
    if ( borderTopLeft != null ) {
      s.setStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_HEIGHT, borderTopLeft.getHeight() );
      s.setStyleProperty( ElementStyleKeys.BORDER_TOP_LEFT_RADIUS_WIDTH, borderTopLeft.getWidth() );
    }
    if ( borderTopRight != null ) {
      s.setStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_HEIGHT, borderTopRight.getHeight() );
      s.setStyleProperty( ElementStyleKeys.BORDER_TOP_RIGHT_RADIUS_WIDTH, borderTopRight.getWidth() );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return null;
  }
}
