/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
