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

package org.pentaho.reporting.engine.classic.core.modules.parser.data.inlinedata;

import org.pentaho.reporting.engine.classic.core.util.TypedTableModel;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import javax.swing.table.TableModel;
import java.util.ArrayList;

public class InlineTableReadHandler extends AbstractXmlReadHandler {
  private String name;
  private TypedTableModel data;
  private InlineTableDefinitionReadHandler definitionReadHandler;
  private ArrayList rows;

  public InlineTableReadHandler() {
    rows = new ArrayList();
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    name = attrs.getValue( getUri(), "name" );
    if ( name == null ) {
      throw new ParseException( "Required attribute 'name' is not defined.", getLocator() );
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
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "definition".equals( tagName ) ) {
      definitionReadHandler = new InlineTableDefinitionReadHandler();
      return definitionReadHandler;
    } else if ( "row".equals( tagName ) ) {
      if ( definitionReadHandler == null ) {
        throw new ParseException( "A table-definition has to be specified before any row can be defined.", getLocator() );
      }

      final InlineTableRowReadHandler rowReadHandler = new InlineTableRowReadHandler( definitionReadHandler.getTypes() );
      rows.add( rowReadHandler );
      return rowReadHandler;
    }
    return null;
  }

  public TableModel getData() {
    return data;
  }

  public String getName() {
    return name;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    data = new TypedTableModel( definitionReadHandler.getNames(), definitionReadHandler.getTypes(), rows.size() );
    for ( int row = 0; row < rows.size(); row++ ) {
      final InlineTableRowReadHandler handler = (InlineTableRowReadHandler) rows.get( row );
      final Object[] data = (Object[]) handler.getObject();
      for ( int column = 0; column < data.length; column++ ) {
        final Object value = data[column];
        this.data.setValueAt( value, row, column );
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
    return data;
  }
}
