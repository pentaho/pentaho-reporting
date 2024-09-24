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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.StringReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DefaultCubeFileProviderReadHandler extends AbstractXmlReadHandler implements CubeFileProviderReadHandler {
  private StringReadHandler pathReadHandler;
  private StringReadHandler connectionNameReadHandler;

  public DefaultCubeFileProviderReadHandler() {
  }

  /**
   * Returns the handler for a child element.
   *
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
    if ( "cube-filename".equals( tagName ) ) {
      pathReadHandler = new StringReadHandler();
      return pathReadHandler;
    }
    if ( "cube-connection-name".equals( tagName ) ) {
      connectionNameReadHandler = new StringReadHandler();
      return connectionNameReadHandler;
    }
    return null;
  }

  /**
   * Done parsing.
   *
   * @throws org.xml.sax.SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    if ( pathReadHandler == null ) {
      throw new ParseException( "Required element 'cube-filename' is not present.", getLocator() );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws org.xml.sax.SAXException if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return getProvider();
  }

  protected String getPath() {
    if ( pathReadHandler == null ) {
      return null;
    }
    return pathReadHandler.getResult();
  }

  protected String getCubeConnectionName() {
    if ( connectionNameReadHandler == null ) {
      return null;
    }
    return connectionNameReadHandler.getResult();
  }

  public CubeFileProvider getProvider() {
    final CubeFileProvider fileProvider =
      ClassicEngineBoot.getInstance().getObjectFactory().get( CubeFileProvider.class );
    if ( pathReadHandler != null ) {
      fileProvider.setDesignTimeFile( pathReadHandler.getResult() );
    }
    if ( connectionNameReadHandler != null ) {
      fileProvider.setCubeConnectionName( connectionNameReadHandler.getResult() );
    }
    return fileProvider;
  }
}
