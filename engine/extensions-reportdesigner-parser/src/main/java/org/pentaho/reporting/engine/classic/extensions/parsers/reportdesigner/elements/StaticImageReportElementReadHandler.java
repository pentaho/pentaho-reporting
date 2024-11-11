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
import org.pentaho.reporting.engine.classic.core.filter.types.ContentType;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.xml.sax.SAXException;

import java.util.Properties;

public class StaticImageReportElementReadHandler extends AbstractReportElementReadHandler {
  private Element element;

  public StaticImageReportElementReadHandler() {
    element = new Element();
    element.setElementType( new ContentType() );
    element.getStyle().setStyleProperty( ElementStyleKeys.SCALE, Boolean.TRUE );
  }

  protected Element getElement() {
    return element;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final Properties result = getResult();
    final String url = result.getProperty( "url" );
    if ( url != null ) {
      getElement().setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, url );
    }

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
