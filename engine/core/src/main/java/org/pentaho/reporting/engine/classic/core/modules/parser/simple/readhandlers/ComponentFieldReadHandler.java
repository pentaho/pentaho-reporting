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


package org.pentaho.reporting.engine.classic.core.modules.parser.simple.readhandlers;

import org.pentaho.reporting.engine.classic.core.elementfactory.ContentFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.SAXException;

public class ComponentFieldReadHandler extends AbstractElementReadHandler {
  private ContentFieldElementFactory elementFactory;

  public ComponentFieldReadHandler() {
    this.elementFactory = new ContentFieldElementFactory();
  }

  /**
   * Starts parsing.
   *
   * @param atts
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final PropertyAttributes atts ) throws SAXException {
    super.startParsing( atts );

    final String fieldName = atts.getValue( getUri(), "fieldname" );
    if ( fieldName != null ) {
      elementFactory.setFieldname( fieldName );
    } else {
      final String formula = atts.getValue( getUri(), "formula" );
      if ( formula == null ) {
        throw new ParseException( "Either 'fieldname' or 'formula' attribute must be given.", getLocator() );
      }
      elementFactory.setFormula( formula );
    }
  }

  protected ElementFactory getElementFactory() {
    return elementFactory;
  }
}
