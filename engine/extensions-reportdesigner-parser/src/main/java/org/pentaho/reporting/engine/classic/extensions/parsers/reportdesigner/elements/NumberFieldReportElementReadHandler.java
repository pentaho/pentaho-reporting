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
import org.pentaho.reporting.engine.classic.core.filter.types.NumberFieldType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.xml.sax.SAXException;

import java.util.Properties;

public class NumberFieldReportElementReadHandler extends AbstractTextElementReadHandler {
  public NumberFieldReportElementReadHandler() {
    final Element element = new Element();
    element.setElementType( new NumberFieldType() );
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
    final String format = result.getProperty( "format" );
    if ( format != null ) {
      getElement().setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING, format );
    }

    final String excelFormat = result.getProperty( "excelNumberFormat" );
    if ( excelFormat != null ) {
      getStyle().setStyleProperty( ElementStyleKeys.EXCEL_DATA_FORMAT_STRING, excelFormat );
    }
  }
}
