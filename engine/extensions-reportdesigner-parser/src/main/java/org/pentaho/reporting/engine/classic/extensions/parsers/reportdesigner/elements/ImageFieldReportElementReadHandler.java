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

package org.pentaho.reporting.engine.classic.extensions.parsers.reportdesigner.elements;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.types.ContentFieldType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.xml.sax.SAXException;

public class ImageFieldReportElementReadHandler extends AbstractTextElementReadHandler {
  public ImageFieldReportElementReadHandler() {
    final Element element = new Element();
    element.setElementType( new ContentFieldType() );
    setElement( element );
    element.getStyle().setStyleProperty( ElementStyleKeys.SCALE, Boolean.TRUE );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final String keepAspectRatio = getResult().getProperty( "keepAspect" );
    if ( keepAspectRatio != null ) {
      if ( "true".equals( keepAspectRatio ) ) {
        getStyle().setStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO, Boolean.TRUE );
      } else {
        getStyle().setStyleProperty( ElementStyleKeys.KEEP_ASPECT_RATIO, Boolean.FALSE );
      }
    }


  }
}
