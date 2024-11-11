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


package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class GroupFieldsReadHandler extends AbstractPropertyXmlReadHandler {
  private RelationalGroup group;
  private ArrayList fieldHandlers;

  public GroupFieldsReadHandler( final RelationalGroup group ) {
    this.group = group;
    this.fieldHandlers = new ArrayList();
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
  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final PropertyAttributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "field".equals( tagName ) ) {
      final GroupFieldReadHandler readHandler = new GroupFieldReadHandler();
      fieldHandlers.add( readHandler );
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
    for ( int i = 0; i < fieldHandlers.size(); i++ ) {
      final GroupFieldReadHandler readHandler = (GroupFieldReadHandler) fieldHandlers.get( i );
      group.addField( (String) readHandler.getObject() );
    }
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   */
  public Object getObject() {
    return group;
  }
}
