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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.filter.DataSource;
import org.pentaho.reporting.engine.classic.core.filter.types.LegacyType;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.ExtParserModule;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class LegacyElementReadHandler extends AbstractElementReadHandler {
  private LegacyDataSourceReadHandler dataSourceReadHandler;
  private LegacyTemplateReadHandler templateReadHandler;

  public LegacyElementReadHandler() throws ParseException {
    super( LegacyType.INSTANCE );
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
    if ( ExtParserModule.NAMESPACE.equals( uri ) ) {
      if ( "template".equals( tagName ) ) {
        templateReadHandler = new LegacyTemplateReadHandler();
        return templateReadHandler;
      } else if ( "datasource".equals( tagName ) ) {
        dataSourceReadHandler = new LegacyDataSourceReadHandler();
        return dataSourceReadHandler;
      }
    }

    return super.getHandlerForChild( uri, tagName, atts );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  @SuppressWarnings( "deprecation" )
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    if ( dataSourceReadHandler != null ) {
      getElement().setDataSource( (DataSource) dataSourceReadHandler.getObject() );
    } else if ( templateReadHandler != null ) {
      getElement().setDataSource( (DataSource) templateReadHandler.getObject() );
    }

  }
}
