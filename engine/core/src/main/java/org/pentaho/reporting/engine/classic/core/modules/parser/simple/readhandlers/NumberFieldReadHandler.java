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

import org.pentaho.reporting.engine.classic.core.elementfactory.NumberFieldElementFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.xml.sax.SAXException;

public class NumberFieldReadHandler extends StringFieldReadHandler {
  public NumberFieldReadHandler() {
    super( new NumberFieldElementFactory() );
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
    final NumberFieldElementFactory elementFactory = (NumberFieldElementFactory) getTextElementFactory();
    elementFactory.setFormatString( atts.getValue( getUri(), "format" ) );
    elementFactory.setExcelCellFormat( atts.getValue( getUri(), "excel-format" ) );
  }
}
