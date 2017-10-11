/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.testsupport.font.parser;

import org.pentaho.reporting.engine.classic.core.testsupport.font.LocalFontFamily;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class FontSpecReadHandler extends AbstractXmlReadHandler {
  private ArrayList<FontFamilyReadHandler> fontFamilyReadHandlers;
  private ArrayList<FontSourceReadHandler> fontSourceReadHandlers;
  private FontMetricsCollection fontMetrics;
  private String fallbackName;

  public FontSpecReadHandler() {
    fontSourceReadHandlers = new ArrayList<FontSourceReadHandler>();
    fontFamilyReadHandlers = new ArrayList<FontFamilyReadHandler>();
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    fallbackName = attrs.getValue( getUri(), "fallback-font" );
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }
    if ( "font-family".equals( tagName ) ) {
      final FontFamilyReadHandler ffr = new FontFamilyReadHandler();
      fontFamilyReadHandlers.add( ffr );
      return ffr;
    }
    if ( "font-source".equals( tagName ) ) {
      final FontSourceReadHandler fsr = new FontSourceReadHandler();
      fontSourceReadHandlers.add( fsr );
      return fsr;
    }
    return null;
  }

  protected void doneParsing() throws SAXException {
    fontMetrics = new FontMetricsCollection();
    for ( int i = 0; i < fontSourceReadHandlers.size(); i++ ) {
      final FontSourceReadHandler readHandler = fontSourceReadHandlers.get( i );
      fontMetrics.defineMetrics( readHandler.getSource(), readHandler.getLocalFontMetricsBase() );
    }

    for ( int i = 0; i < fontFamilyReadHandlers.size(); i++ ) {
      final FontFamilyReadHandler familyReadHandler = fontFamilyReadHandlers.get( i );
      final LocalFontFamily fontFamily = familyReadHandler.getObject();
      fontMetrics.addFontFamily( fontFamily );
    }

    fontMetrics.setFallbackName( fallbackName );
  }

  public FontMetricsCollection getObject() throws SAXException {

    // font families
    // font source with metrics
    return fontMetrics;
  }
}
