/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
