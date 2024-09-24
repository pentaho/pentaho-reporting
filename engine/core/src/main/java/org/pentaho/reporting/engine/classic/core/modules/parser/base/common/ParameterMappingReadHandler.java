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

package org.pentaho.reporting.engine.classic.core.modules.parser.base.common;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.SAXException;

/**
 * Creation-Date: Jan 9, 2007, 6:01:15 PM
 *
 * @author Thomas Morgner
 */
public class ParameterMappingReadHandler extends AbstractPropertyXmlReadHandler {
  private String name;
  private String alias;

  public ParameterMappingReadHandler() {
  }

  protected void startParsing( final PropertyAttributes attrs ) throws SAXException {
    name = attrs.getValue( getUri(), "name" );
    if ( name == null ) {
      throw new ParseException( "Required attribute 'name' is missing.", getLocator() );
    }
    alias = attrs.getValue( getUri(), "alias" );
    if ( alias == null ) {
      alias = name;
    }
  }

  public String getName() {
    return name;
  }

  public String getAlias() {
    return alias;
  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if there is a parsing error.
   */
  public Object getObject() throws SAXException {
    return getName();
  }
}
