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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.metadata.ElementType;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleElementRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.ElementReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public abstract class AbstractRootLevelBandReadHandler extends AbstractXmlReadHandler {
  private RootLevelContentReadHandler contentReadHandler;
  private ArrayList<ElementReadHandler> subReportReadHandler;
  private AbstractRootLevelBand element;
  private ElementType elementType;

  public AbstractRootLevelBandReadHandler( final ElementType elementType ) throws ParseException {
    this.elementType = elementType;
    this.subReportReadHandler = new ArrayList<ElementReadHandler>();
  }

  public void init( final RootXmlReadHandler rootHandler, final String uri, final String tagName ) throws SAXException {
    super.init( rootHandler, uri, tagName );
    this.contentReadHandler = new RootLevelContentReadHandler( elementType, createElement() );
  }

  protected AbstractRootLevelBand createElement() throws ParseException {
    try {
      return (AbstractRootLevelBand) elementType.create();
    } catch ( Exception e ) {
      throw new ParseException( e );
    }
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri
   *          the URI of the namespace of the current element.
   * @param tagName
   *          the tag name.
   * @param atts
   *          the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) ) {
      if ( "root-level-content".equals( tagName ) ) {
        return contentReadHandler;
      } else {
        final ElementReadHandler readHandler =
            BundleElementRegistry.getInstance().getReadHandler( uri, tagName, getLocator() );
        if ( readHandler != null ) {
          this.subReportReadHandler.add( readHandler );
        }
        return readHandler;
      }
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
    element = (AbstractRootLevelBand) contentReadHandler.getElement();
    for ( int i = 0; i < subReportReadHandler.size(); i++ ) {
      final ElementReadHandler handler = subReportReadHandler.get( i );
      final Object o = handler.getObject();
      if ( o instanceof SubReport ) {
        element.addSubReport( (SubReport) o );
      }
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return element;
  }

  public AbstractRootLevelBand getElement() {
    return element;
  }
}
