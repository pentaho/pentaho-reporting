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
import org.pentaho.reporting.engine.classic.core.util.beans.BeanPropertyLookupParser;
import org.xml.sax.SAXException;

import java.io.Serializable;

public class PropertyStringReadHandler extends AbstractPropertyXmlReadHandler implements Serializable {
  private class StringLookupParser extends BeanPropertyLookupParser {
    protected StringLookupParser() {
    }

    protected Object performInitialLookup( final String name ) {
      return getRootHandler().getHelperObject( name );
    }
  }

  private StringBuffer buffer;
  private StringLookupParser lookupParser;

  public PropertyStringReadHandler() {
    this.buffer = new StringBuffer( 100 );
    this.lookupParser = new StringLookupParser();
  }

  /**
   * This method is called to process the character data between element tags.
   *
   * @param ch
   *          the character buffer.
   * @param start
   *          the start index.
   * @param length
   *          the length.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  public void characters( final char[] ch, final int start, final int length ) throws SAXException {
    buffer.append( ch, start, length );
  }

  public String getResult() {
    if ( Boolean.TRUE.equals( getRootHandler().getHelperObject( "property-expansion" ) ) ) {
      return lookupParser.translateAndLookup( buffer.toString() );
    }
    return buffer.toString();
  }

  public void startParsing( final PropertyAttributes attrs ) throws SAXException {
    super.startParsing( attrs );
  }

  public void doneParsing() throws SAXException {
    super.doneParsing();
  }

  /**
   * Returns the object for this element.
   *
   * @return the object.
   */
  public Object getObject() {
    return getResult();
  }
}
