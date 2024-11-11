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

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.filter.templates.AnchorFieldTemplate;
import org.pentaho.reporting.engine.classic.core.filter.types.LegacyType;
import org.xml.sax.SAXException;

public class AnchorFieldElementReadHandler extends AbstractReportElementReadHandler {
  private Element element;
  private AnchorFieldTemplate anchorFieldTemplate;

  public AnchorFieldElementReadHandler() {
    this.element = new Element();
    this.element.setElementType( LegacyType.INSTANCE );
    this.anchorFieldTemplate = new AnchorFieldTemplate();
    this.element.setDataSource( anchorFieldTemplate );
  }

  /**
   * Done parsing.
   *
   * @throws SAXException if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final String fieldName = getResult().getProperty( "fieldName" );
    anchorFieldTemplate.setField( fieldName );
  }

  protected Element getElement() {
    return element;
  }
}
