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

package org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.elementfactory.BandElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.TextElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportElementReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportElementReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.StyleExpressionHandler;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class BandReadHandler extends AbstractTextElementReadHandler {
  /**
   * Literal text for an XML report element.
   */
  public static final String ROUND_RECTANGLE_TAG = "round-rectangle";
  /**
   * Literal text for an XML report element.
   */
  public static final String LABEL_TAG = "label";

  /**
   * Literal text for an XML report element.
   */
  public static final String STRING_FIELD_TAG = "string-field";

  /**
   * Literal text for an XML report element.
   */
  public static final String NUMBER_FIELD_TAG = "number-field";

  /**
   * Literal text for an XML report element.
   */
  public static final String DATE_FIELD_TAG = "date-field";

  /**
   * Literal text for an XML report element.
   */
  public static final String IMAGEREF_TAG = "imageref";

  /**
   * Literal text for an XML report element.
   */
  public static final String IMAGEFIELD_TAG = "image-field";

  /**
   * Literal text for an XML report element.
   */
  public static final String IMAGEURLFIELD_TAG = "imageurl-field";

  /**
   * Literal text for an XML report element.
   */
  public static final String RECTANGLE_TAG = "rectangle";

  /**
   * Literal text for an XML report element.
   */
  public static final String RESOURCELABEL_TAG = "resource-label";

  /**
   * Literal text for an XML report element.
   */
  public static final String RESOURCEFIELD_TAG = "resource-field";

  /**
   * Literal text for an XML report element.
   */
  public static final String RESOURCEMESSAGE_TAG = "resource-message";

  /**
   * Literal text for an XML report element.
   */
  public static final String COMPONENTFIELD_TAG = "component-field";

  /**
   * Literal text for an XML report element.
   */
  public static final String LINE_TAG = "line";

  /**
   * Literal text for an XML report element.
   */
  public static final String DRAWABLE_FIELD_TAG = "drawable-field";

  /**
   * Literal text for an XML report element.
   */
  public static final String SHAPE_FIELD_TAG = "shape-field";

  /**
   * Literal text for an XML report element.
   */
  public static final String BAND_TAG = "band";

  /**
   * Literal text for an XML report element.
   */
  public static final String MESSAGE_FIELD_TAG = "message-field";

  /**
   * Literal text for an XML report element.
   */
  public static final String ANCHOR_FIELD_TAG = "anchor-field";

  /**
   * Literal text for an XML attribute value.
   */
  private static final String LAYOUT_ATT = "layout";

  private BandElementFactory bandFactory;
  private Band band;
  private ArrayList<ReportElementReadHandler> elementHandlers;
  private ArrayList<StyleExpressionHandler> styleExpressionHandlers;

  public BandReadHandler() {
    this( new Band() );
  }

  protected BandReadHandler( final Band band ) {
    if ( band == null ) {
      throw new NullPointerException();
    }
    this.band = band;
    this.bandFactory = new BandElementFactory( band );
    this.elementHandlers = new ArrayList<ReportElementReadHandler>();
    styleExpressionHandlers = new ArrayList<StyleExpressionHandler>();
  }

  protected TextElementFactory getTextElementFactory() {
    return bandFactory;
  }

  /**
   * Starts parsing.
   *
   * @param attr
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes attr ) throws SAXException {
    super.startParsing( attr );
    handleLayout( attr );
  }

  public Band getBand() {
    return band;
  }

  private void handleLayout( final Attributes attr ) {
    final String layoutManagerName = attr.getValue( getUri(), BandReadHandler.LAYOUT_ATT );
    if ( layoutManagerName != null ) {
      if ( "org.jfree.report.layout.StaticLayoutManager".equals( layoutManagerName ) ) {
        getBand().getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "canvas" );
      } else if ( "org.jfree.report.layout.StackedLayoutManager".equals( layoutManagerName ) ) {
        getBand().getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "block" );
      }
      if ( "org.pentaho.reporting.engine.classic.core.layout.StaticLayoutManager".equals( layoutManagerName ) ) {
        getBand().getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "canvas" );
      } else if ( "org.pentaho.reporting.engine.classic.core.layout.StackedLayoutManager".equals( layoutManagerName ) ) {
        getBand().getStyle().setStyleProperty( BandStyleKeys.LAYOUT, "block" );
      } else {
        getBand().getStyle().setStyleProperty( BandStyleKeys.LAYOUT, layoutManagerName );
      }
    }
  }

  /**
   * Returns the handler for a child element.
   *
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes atts )
    throws SAXException {
    final ReportElementReadHandlerFactory factory = ReportElementReadHandlerFactory.getInstance();
    final ReportElementReadHandler handler = factory.getHandler( uri, tagName );
    if ( handler != null ) {
      elementHandlers.add( handler );
      return handler;
    }

    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "style-expression".equals( tagName ) ) {
      final StyleExpressionHandler stylehandler = new StyleExpressionHandler();
      styleExpressionHandlers.add( stylehandler );
      return stylehandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    for ( int i = 0; i < elementHandlers.size(); i++ ) {
      final XmlReadHandler readHandler = elementHandlers.get( i );
      final Element e = (Element) readHandler.getObject();
      band.addElement( e );
    }

    for ( int i = 0; i < styleExpressionHandlers.size(); i++ ) {
      final StyleExpressionHandler handler = styleExpressionHandlers.get( i );
      if ( handler.getKey() != null ) {
        band.setStyleExpression( handler.getKey(), handler.getExpression() );
      }
    }

    band.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.SOURCE, getRootHandler().getSource() );
    super.doneParsing();
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return band;
  }
}
