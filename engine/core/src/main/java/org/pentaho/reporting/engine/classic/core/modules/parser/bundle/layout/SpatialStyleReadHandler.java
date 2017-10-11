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

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * @noinspection UnnecessaryBoxing
 */
public class SpatialStyleReadHandler extends AbstractXmlReadHandler implements StyleReadHandler {
  private ElementStyleSheet styleSheet;

  public SpatialStyleReadHandler() {
  }

  public ElementStyleSheet getStyleSheet() {
    return styleSheet;
  }

  public void setStyleSheet( final ElementStyleSheet styleSheet ) {
    this.styleSheet = styleSheet;
  }

  /**
   * Starts parsing.
   *
   * @param atts
   *          the attributes.
   * @throws SAXException
   *           if there is a parsing error.
   */
  protected void startParsing( final Attributes atts ) throws SAXException {

    final String posX = atts.getValue( getUri(), "x" );
    if ( posX != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.POS_X, new Float( ReportParserUtil.parseRelativeFloat( posX,
          "Attribute 'x' not valid", getLocator() ) ) );
    }

    final String posY = atts.getValue( getUri(), "y" );
    if ( posY != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.POS_Y, new Float( ReportParserUtil.parseRelativeFloat( posY,
          "Attribute 'y' not valid", getLocator() ) ) );
    }

    final String width = atts.getValue( getUri(), "width" );
    if ( width != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.WIDTH, new Float( ReportParserUtil.parseRelativeFloat( width,
          "Attribute 'width' not valid", getLocator() ) ) );
    }

    final String height = atts.getValue( getUri(), "height" );
    if ( height != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.HEIGHT, new Float( ReportParserUtil.parseRelativeFloat( height,
          "Attribute 'height' not valid", getLocator() ) ) );
    }

    final String minWidth = atts.getValue( getUri(), "min-width" );
    if ( minWidth != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( ReportParserUtil.parseRelativeFloat(
          minWidth, "Attribute 'min-width' not valid", getLocator() ) ) );
    }

    final String minHeight = atts.getValue( getUri(), "min-height" );
    if ( minHeight != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( ReportParserUtil.parseRelativeFloat(
          minHeight, "Attribute 'min-height' not valid", getLocator() ) ) );
    }

    final String maxWidth = atts.getValue( getUri(), "max-width" );
    if ( maxWidth != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.MAX_WIDTH, new Float( ReportParserUtil.parseRelativeFloat(
          maxWidth, "Attribute 'max-width' not valid", getLocator() ) ) );
    }

    final String maxHeight = atts.getValue( getUri(), "max-height" );
    if ( maxHeight != null ) {
      styleSheet.setStyleProperty( ElementStyleKeys.MAX_HEIGHT, new Float( ReportParserUtil.parseRelativeFloat(
          maxHeight, "Attribute 'max-height' not valid", getLocator() ) ) );
    }

  }

  /**
   * Returns the object for this element or null, if this element does not create an object.
   *
   * @return the object.
   * @throws SAXException
   *           if an parser error occured.
   */
  public Object getObject() throws SAXException {
    return styleSheet;
  }
}
