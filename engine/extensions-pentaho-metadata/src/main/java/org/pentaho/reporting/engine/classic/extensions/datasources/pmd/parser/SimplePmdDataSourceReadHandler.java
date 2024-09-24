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
