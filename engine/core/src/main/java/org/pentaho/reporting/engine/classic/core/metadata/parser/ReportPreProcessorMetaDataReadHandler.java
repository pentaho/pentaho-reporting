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

package org.pentaho.reporting.engine.classic.core.metadata.parser;

import java.util.ArrayList;

import org.pentaho.reporting.engine.classic.core.metadata.ReportPreProcessorMetaData;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class ReportPreProcessorMetaDataReadHandler extends AbstractXmlReadHandler {
  private ArrayList<ReportPreProcessorReadHandler> elements;
  private ReportPreProcessorMetaDataCollection typeCollection;

  public ReportPreProcessorMetaDataReadHandler() {
    elements = new ArrayList<ReportPreProcessorReadHandler>();
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
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( getUri().equals( uri ) == false ) {
      return null;
    }
    if ( "pre-processor".equals( tagName ) ) { // NON-NLS
      final ReportPreProcessorReadHandler readHandler = new ReportPreProcessorReadHandler();
      elements.add( readHandler );
      return readHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final ReportPreProcessorMetaData[] result = new ReportPreProcessorMetaData[elements.size()];
    for ( int i = 0; i < elements.size(); i++ ) {
      final ReportPreProcessorReadHandler handler = elements.get( i );
      result[i] = (ReportPreProcessorMetaData) handler.getObject();
    }

    typeCollection = new ReportPreProcessorMetaDataCollection( result );
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return typeCollection;
  }
}
