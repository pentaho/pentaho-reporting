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


package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.types.ResourceMessageType;
import org.xml.sax.SAXException;

import java.util.Properties;

public class ResourceMessageReportElementReadHandler extends AbstractTextElementReadHandler {
  public ResourceMessageReportElementReadHandler() {
    final Element element = new Element();
    element.setElementType( new ResourceMessageType() );
    setElement( element );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final Properties result = getResult();
    final String format = result.getProperty( "resourceBase" );
    if ( format != null ) {
      getElement().setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.RESOURCE_IDENTIFIER, format );
    }

    final String value = result.getProperty( "formatKey" );
    if ( value != null ) {
      getElement().setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, value );
    }
  }
}
