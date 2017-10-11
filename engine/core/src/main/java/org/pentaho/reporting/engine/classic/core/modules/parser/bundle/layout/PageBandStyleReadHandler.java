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
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class PageBandStyleReadHandler extends AbstractXmlReadHandler implements StyleReadHandler {
  private ElementStyleSheet styleSheet;

  public PageBandStyleReadHandler() {
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
    final String repeat = atts.getValue( getUri(), "repeat" );
    if ( repeat != null ) {
      styleSheet.setBooleanStyleProperty( BandStyleKeys.REPEAT_HEADER, "true".equals( repeat ) );
    }

    final String displayOnFirstPage = atts.getValue( getUri(), "display-on-first-page" );
    if ( displayOnFirstPage != null ) {
      styleSheet.setBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_FIRSTPAGE, "true".equals( displayOnFirstPage ) );
    }

    final String displayOnLastPage = atts.getValue( getUri(), "display-on-last-page" );
    if ( displayOnLastPage != null ) {
      styleSheet.setBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_LASTPAGE, "true".equals( displayOnLastPage ) );
    }

    final String sticky = atts.getValue( getUri(), "sticky" );
    if ( sticky != null ) {
      styleSheet.setBooleanStyleProperty( BandStyleKeys.STICKY, "true".equals( sticky ) );
    }

    final String fixedPosition = atts.getValue( getUri(), "fixed-position" );
    if ( fixedPosition != null ) {
      styleSheet.setStyleProperty( BandStyleKeys.FIXED_POSITION, new Float( ReportParserUtil.parseRelativeFloat(
          fixedPosition, "Attribute 'fixed-position' not valid", getLocator() ) ) );
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
