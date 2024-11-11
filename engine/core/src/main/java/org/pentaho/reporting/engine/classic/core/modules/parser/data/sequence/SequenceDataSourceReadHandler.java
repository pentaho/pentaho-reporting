/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.parser.data.sequence;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sequence.SequenceDataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class SequenceDataSourceReadHandler extends AbstractXmlReadHandler implements DataFactoryReadHandler {
  private ArrayList<SequenceReadHandler> queries;
  private DataFactory dataFactory;

  public SequenceDataSourceReadHandler() {
    queries = new ArrayList<SequenceReadHandler>();
  }

  /**
   * Returns the handler for a child element.
   *
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
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "sequence".equals( tagName ) ) {
      final SequenceReadHandler queryReadHandler = new SequenceReadHandler();
      queries.add( queryReadHandler );
      return queryReadHandler;
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
    final SequenceDataFactory srdf = new SequenceDataFactory();
    for ( int i = 0; i < queries.size(); i++ ) {
      final SequenceReadHandler handler = queries.get( i );
      srdf.addSequence( handler.getName(), handler.getData() );
    }

    dataFactory = srdf;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if there is a parsing error.
   */
  public Object getObject() throws SAXException {
    return dataFactory;
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }
}
