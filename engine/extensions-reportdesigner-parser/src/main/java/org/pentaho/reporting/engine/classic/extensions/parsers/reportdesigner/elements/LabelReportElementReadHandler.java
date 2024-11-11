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
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.xml.sax.SAXException;

public class LabelReportElementReadHandler extends AbstractTextElementReadHandler {
  public LabelReportElementReadHandler() {
    final Element element = new Element();
    element.setElementType( new LabelType() );
    setElement( element );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final String value = getResult().getProperty( "text" );
    getElement().setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, value );
  }
}
