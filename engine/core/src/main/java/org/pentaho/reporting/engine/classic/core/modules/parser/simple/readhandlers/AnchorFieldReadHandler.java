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

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.elementfactory.BandElementFactory;
import org.pentaho.reporting.engine.classic.core.elementfactory.ElementFactory;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;
import org.pentaho.reporting.engine.classic.core.function.sys.GetDataRowValueExpression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.PropertyAttributes;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.SAXException;

public class AnchorFieldReadHandler extends AbstractElementReadHandler {
  private BandElementFactory elementFactory;
  private String fieldName;
  private String formula;

  public AnchorFieldReadHandler() {
    elementFactory = new BandElementFactory();
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
    elementFactory.setName( atts.getValue( getUri(), AbstractElementReadHandler.NAME_ATT ) );

    final String posX = atts.getValue( getUri(), "x" );
    if ( posX != null ) {
      elementFactory.setX( new Float( ReportParserUtil.parseRelativeFloat( posX, "Attribute 'x' not valid",
          getLocator() ) ) );
    }

    final String posY = atts.getValue( getUri(), "y" );
    if ( posY != null ) {
      elementFactory.setY( new Float( ReportParserUtil.parseRelativeFloat( posY, "Attribute 'y' not valid",
          getLocator() ) ) );
    }

    fieldName = atts.getValue( getUri(), "fieldname" );
    formula = atts.getValue( getUri(), "formula" );

    if ( formula == null && fieldName == null ) {
      throw new ParseException( "Either 'fieldname' or 'formula' attribute must be given.", getLocator() );
    }
  }

  protected ElementFactory getElementFactory() {
    return elementFactory;
  }

  /**
   * Done parsing.
   *
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void doneParsing() throws SAXException {
    super.doneParsing();
    final Element element = getElement();
    if ( fieldName != null ) {
      final GetDataRowValueExpression ex = new GetDataRowValueExpression();
      ex.setField( fieldName );
      element.setStyleExpression( ElementStyleKeys.ANCHOR_NAME, ex );
    } else if ( formula != null ) {
      final FormulaExpression fe = new FormulaExpression();
      fe.setFormula( formula );
      element.setStyleExpression( ElementStyleKeys.ANCHOR_NAME, fe );
    }
  }
}
