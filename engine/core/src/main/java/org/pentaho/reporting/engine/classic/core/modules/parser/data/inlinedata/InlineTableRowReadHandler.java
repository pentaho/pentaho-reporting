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


package org.pentaho.reporting.engine.classic.core.modules.parser.data.inlinedata;

import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.IgnoreAnyChildReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class InlineTableRowReadHandler extends AbstractXmlReadHandler {
  private ArrayList data;
  private Class[] types;
  private int columnCount;

  public InlineTableRowReadHandler( final Class[] types ) {
    if ( types == null ) {
      throw new NullPointerException( "Type-array must not be null." );
    }
    this.types = (Class[]) types.clone();
    this.data = new ArrayList();
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

    if ( "data".equals( tagName ) ) {
      if ( columnCount >= types.length ) {
        return new IgnoreAnyChildReadHandler();
      }

      final InlineTableDataReadHandler dataReadHandler = new InlineTableDataReadHandler( types[columnCount] );
      columnCount += 1;
      data.add( dataReadHandler );
      return dataReadHandler;
    }

    return null;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    final Object[] result = new Object[types.length];

    final int size = Math.min( data.size(), types.length );
    for ( int i = 0; i < size; i++ ) {
      final InlineTableDataReadHandler handler = (InlineTableDataReadHandler) data.get( i );
      result[i] = handler.getObject();
    }

    return result;
  }
}
