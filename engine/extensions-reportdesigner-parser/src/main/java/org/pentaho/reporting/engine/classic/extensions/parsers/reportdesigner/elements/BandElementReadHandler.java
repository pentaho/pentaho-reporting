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

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.ReportDesignerParserModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;
import java.util.Properties;

public class BandElementReadHandler extends AbstractReportElementReadHandler {
  private Band band;
  private ArrayList<AbstractReportElementReadHandler> childElements;

  public BandElementReadHandler( final Band band ) {
    this.childElements = new ArrayList<AbstractReportElementReadHandler>();
    this.band = band;
  }

  protected Element getElement() {
    return band;
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
                                               final Attributes atts ) throws SAXException {
    if ( isSameNamespace( uri ) ) {
      if ( "child".equalsIgnoreCase( tagName ) ) {
        final String type = atts.getValue( uri, "type" );
        if ( "org.pentaho.reportdesigner.crm.report.model.BandReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new BandElementReadHandler( new Band() );
          childElements.add( readHandler );
          return readHandler;
        }
        if ( "org.pentaho.reportdesigner.crm.report.model.AnchorFieldReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new AnchorFieldElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }
        if ( "org.pentaho.reportdesigner.crm.report.model.DateFieldReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new DateFieldReportElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }

        if ( "org.pentaho.reportdesigner.crm.report.model.ChartReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new ChartElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }

        if ( "org.pentaho.reportdesigner.crm.report.model.EllipseReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new EllipseElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }
        if ( "org.pentaho.reportdesigner.crm.report.model.ImageURLFieldReportElement".equals( type ) ||
          "org.pentaho.reportdesigner.crm.report.model.ImageFieldReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new ImageFieldReportElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }
        if ( "org.pentaho.reportdesigner.crm.report.model.LabelReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new LabelReportElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }
        if ( "org.pentaho.reportdesigner.crm.report.model.LineReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new LineReportElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }
        if ( "org.pentaho.reportdesigner.crm.report.model.MessageFieldReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new MessageReportElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }
        if ( "org.pentaho.reportdesigner.crm.report.model.NumberFieldReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new NumberFieldReportElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }
        if ( "org.pentaho.reportdesigner.crm.report.model.RectangleReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new RectangleElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }
        if ( "org.pentaho.reportdesigner.crm.report.model.ResourceFieldReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new ResourceFieldReportElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }
        if ( "org.pentaho.reportdesigner.crm.report.model.ResourceLabelReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new ResourceLabelReportElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }
        if ( "org.pentaho.reportdesigner.crm.report.model.ResourceMessageReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new ResourceMessageReportElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }
        if ( "org.pentaho.reportdesigner.crm.report.model.StaticImageReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new StaticImageReportElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }
        if ( "org.pentaho.reportdesigner.crm.report.model.TextFieldReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new TextFieldReportElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }
        if ( "org.pentaho.reportdesigner.crm.report.model.DrawableFieldReportElement".equals( type ) ) {
          final AbstractReportElementReadHandler readHandler = new DrawableFieldReportElementReadHandler();
          childElements.add( readHandler );
          return readHandler;
        }
        //

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
    final Properties result = getResult();


    final String showInGUI = result.getProperty( "showInLayoutGUI" );
    if ( showInGUI != null ) {
      if ( "true".equals( showInGUI ) ) {
        band.setAttribute( ReportDesignerParserModule.NAMESPACE,
          ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.FALSE );
      } else {
        band.setAttribute( ReportDesignerParserModule.NAMESPACE,
          ReportDesignerParserModule.HIDE_IN_LAYOUT_GUI_ATTRIBUTE, Boolean.TRUE );
      }
    }

    final String layoutType = result.getProperty( "reportLayoutManagerType" );
    band.getStyle().setStyleProperty( BandStyleKeys.LAYOUT, parseLayoutManager( layoutType ) );

    for ( int i = 0; i < childElements.size(); i++ ) {
      final AbstractReportElementReadHandler handler = childElements.get( i );
      band.addElement( handler.getElement() );
    }
  }

  private Object parseLayoutManager( final String layoutType ) {
    if ( layoutType == null ) {
      return null;
    }
    if ( "STACKED".equals( layoutType ) ) {
      return "block";
    }
    if ( "NULL".equals( layoutType ) ) {
      return "canvas";
    }
    return null;
  }

  public Band getBand() {
    return band;
  }

  /**
   * Returns the resulting properties collection, never null.
   *
   * @return the properties.
   * @throws SAXException if there is a parsing error.
   */
  public Object getObject() throws SAXException {
    return band;
  }
}
