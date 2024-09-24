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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractMDXDataFactory;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.PropertiesReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public abstract class AbstractMDXDataSourceReadHandler extends AbstractXmlReadHandler
  implements DataFactoryReadHandler {
  private MondrianConnectionReadHandler connectionReadHandler;
  private AbstractMDXDataFactory dataFactory;
  private PropertiesReadHandler propertiesReadHandler;

  public AbstractMDXDataSourceReadHandler() {
  }

  public DataFactory getDataFactory() {
    return dataFactory;
  }

  /**
   * Returns the handler for a child element.
   *
   * @param uri     the URI of the namespace of the current element.
   * @param tagName the tag name.
   * @param atts    the attributes.
   * @return the handler or null, if the tagname is invalid.
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected XmlReadHandler getHandlerForChild( final String uri,
                                               final String tagName,
                                               final Attributes atts ) throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "connection".equals( tagName ) ) {
      connectionReadHandler = new MondrianConnectionReadHandler();
      return connectionReadHandler;
    }
    if ( "mondrian-properties".equals( tagName ) ) {
      propertiesReadHandler = new PropertiesReadHandler();
      return propertiesReadHandler;
    }
    return null;
  }

  protected abstract AbstractMDXDataFactory createDataFactory();

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final AbstractMDXDataFactory srdf = createDataFactory();
    if ( connectionReadHandler != null ) {
      connectionReadHandler.configure( srdf );
    }

    if ( propertiesReadHandler != null ) {
      srdf.setBaseConnectionProperties( propertiesReadHandler.getResult() );
    }
    dataFactory = srdf;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return dataFactory;
  }
}
