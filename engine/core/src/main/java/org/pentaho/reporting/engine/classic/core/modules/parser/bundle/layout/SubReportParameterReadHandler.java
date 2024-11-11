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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class SubReportParameterReadHandler extends AbstractXmlReadHandler {
  private String masterName;
  private String detailName;

  public SubReportParameterReadHandler() {
  }

  /**
   * Starts parsing.
   *
   * @param attrs
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes attrs ) throws SAXException {
    masterName = attrs.getValue( getUri(), "master-fieldname" );
    if ( masterName == null ) {
      throw new NullPointerException( "Required parameter 'master-fieldname' is missing." );
    }

    detailName = attrs.getValue( getUri(), "detail-fieldname" );
    if ( detailName == null ) {
      throw new NullPointerException( "Required parameter 'detail-fieldname' is missing." );
    }
  }

  public String getMasterName() {
    return masterName;
  }

  public String getDetailName() {
    return detailName;
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
