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


package org.pentaho.reporting.engine.classic.extensions.datasources.pmd.parser;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.SimplePmdDataFactory;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SimplePmdDataSourceReadHandler extends AbstractXmlReadHandler implements DataFactoryReadHandler {
  private IPmdConfigReadHandler configReadHandler;
  private SimplePmdDataFactory dataFactory;

  public SimplePmdDataSourceReadHandler() {
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
    final PmdConfigReadHandlerFactory configfactory = PmdConfigReadHandlerFactory.getInstance();
    final XmlReadHandler confighandler = configfactory.getHandler( uri, tagName );
    if ( confighandler instanceof IPmdConfigReadHandler ) {
      configReadHandler = (IPmdConfigReadHandler) confighandler;
      return confighandler;
    }

    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    return null;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    final SimplePmdDataFactory pmddf = new SimplePmdDataFactory();
    pmddf.setConnectionProvider( configReadHandler.getConnectionProvider() );
    pmddf.setDomainId( configReadHandler.getDomain() );
    pmddf.setXmiFile( configReadHandler.getXmiFile() );

    dataFactory = pmddf;
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

  public DataFactory getDataFactory() {
    return dataFactory;
  }
}
