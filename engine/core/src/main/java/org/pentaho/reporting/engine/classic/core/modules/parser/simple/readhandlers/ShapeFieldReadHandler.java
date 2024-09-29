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


package org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers;

import org.pentaho.reporting.engine.classic.core.elementfactory.ContentFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.SAXException;

public class ShapeFieldReadHandler extends AbstractShapeElementReadHandler {
  private ContentFieldElementFactory elementFactory;

  public ShapeFieldReadHandler() {
    this.elementFactory = new ContentFieldElementFactory();
  }

  /**
   * Starts parsing.
   *
   * @param atts
   *          the attributes.
   * @throws org.xml.sax.SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes atts ) throws SAXException {
    super.startParsing( atts );

    final String fieldName = atts.getValue( getUri(), "fieldname" );
    if ( fieldName == null ) {
      throw new ParseException( "Required attribute 'fieldname' is missing.", getLocator() );
    }
    elementFactory.setFieldname( fieldName );
  }

  protected ElementFactory getElementFactory() {
    return elementFactory;
  }
}
