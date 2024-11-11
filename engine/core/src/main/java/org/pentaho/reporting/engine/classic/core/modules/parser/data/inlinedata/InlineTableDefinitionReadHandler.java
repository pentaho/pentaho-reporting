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
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class InlineTableDefinitionReadHandler extends AbstractXmlReadHandler {
  private ArrayList rowDefinitions;
  private String[] names;
  private Class[] types;

  public InlineTableDefinitionReadHandler() {
    rowDefinitions = new ArrayList();
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

    if ( "column".equals( tagName ) ) {
      final InlineTableColumnReadHandler crh = new InlineTableColumnReadHandler();
      rowDefinitions.add( crh );
      return crh;
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
    final int size = rowDefinitions.size();
    names = new String[size];
    types = new Class[size];

    for ( int i = 0; i < rowDefinitions.size(); i++ ) {
      final InlineTableColumnReadHandler handler = (InlineTableColumnReadHandler) rowDefinitions.get( i );
      names[i] = handler.getName();
      types[i] = handler.getType();
    }
  }

  public String[] getNames() {
    return names;
  }

  public Class[] getTypes() {
    return types;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return null;
  }
}
