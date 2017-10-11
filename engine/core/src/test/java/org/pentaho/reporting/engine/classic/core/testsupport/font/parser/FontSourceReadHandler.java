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

import org.pentaho.reporting.engine.classic.core.testsupport.font.LocalFontMetricsBase;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

import java.util.ArrayList;

public class FontSourceReadHandler extends AbstractXmlReadHandler {
  private ArrayList<CharWidthReadHandler> charWidthReadHandlers;
  private ArrayList<KerningReadHandler> kerningReadHandlers;
  private GlobalMetricsReadHandler globalMetricsReadHandler;
  private LocalFontMetricsBase localFontMetricsBase;
  private String source;

  public FontSourceReadHandler() {
    charWidthReadHandlers = new ArrayList<CharWidthReadHandler>();
    kerningReadHandlers = new ArrayList<KerningReadHandler>();
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    source = attrs.getValue( getUri(), "source" );
    if ( StringUtils.isEmpty( source ) ) {
      throw new ParseException( "Mandatory attribute 'source' is missing", getLocator() );
    }
    localFontMetricsBase = new LocalFontMetricsBase();
  }

  protected void doneParsing() throws SAXException {
    if ( globalMetricsReadHandler == null ) {
      throw new ParseException( "Mandatory element 'global-metrics' is missing", getLocator() );
    }
    localFontMetricsBase.setAscent( globalMetricsReadHandler.getAscent() );
    localFontMetricsBase.setDescent( globalMetricsReadHandler.getDescent() );
    localFontMetricsBase.setLeading( globalMetricsReadHandler.getLeading() );
    localFontMetricsBase.setItalicAngle( globalMetricsReadHandler.getItalicAngle() );
    localFontMetricsBase.setMaxAscent( globalMetricsReadHandler.getMaxAscent() );
    localFontMetricsBase.setMaxDescent( globalMetricsReadHandler.getMaxDescent() );
    localFontMetricsBase.setMaxHeight( globalMetricsReadHandler.getMaxHeight() );
    localFontMetricsBase.setMaxCharAdvance( globalMetricsReadHandler.getMaxCharAdvance() );
    localFontMetricsBase.setXHeight( globalMetricsReadHandler.getXheight() );
    localFontMetricsBase.setOverlinePosition( globalMetricsReadHandler.getOverlinePosition() );
    localFontMetricsBase.setUnderlinePosition( globalMetricsReadHandler.getUnderlinePosition() );
    localFontMetricsBase.setStrikeThroughPosition( globalMetricsReadHandler.getStrikethroughPosition() );
    localFontMetricsBase.setUniformFontMetrics( globalMetricsReadHandler.isUniformFontMetrics() );

    for ( int i = 0; i < charWidthReadHandlers.size(); i++ ) {
      final CharWidthReadHandler readHandler = charWidthReadHandlers.get( i );
      localFontMetricsBase.setCharWidth( readHandler.getCodepoint(), readHandler.getValue() );
    }

    for ( int i = 0; i < kerningReadHandlers.size(); i++ ) {
      final KerningReadHandler kerningReadHandler = kerningReadHandlers.get( i );
      localFontMetricsBase.setKerning( kerningReadHandler.getCodepoint(), kerningReadHandler.getPrev(),
          kerningReadHandler.getValue() );
    }
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) == false ) {
      return null;
    }

    if ( "global-metrics".equals( tagName ) ) {
      globalMetricsReadHandler = new GlobalMetricsReadHandler();
      return globalMetricsReadHandler;
    }
    if ( "char-width".equals( tagName ) ) {
      final CharWidthReadHandler charWidthReadHandler = new CharWidthReadHandler();
      charWidthReadHandlers.add( charWidthReadHandler );
      return charWidthReadHandler;
    }
    if ( "kerning".equals( tagName ) ) {
      final KerningReadHandler kerningReadHandler = new KerningReadHandler();
      kerningReadHandlers.add( kerningReadHandler );
      return kerningReadHandler;
    }
    return null;
  }

  public LocalFontMetricsBase getLocalFontMetricsBase() {
    return localFontMetricsBase;
  }

  public LocalFontMetricsBase getObject() throws SAXException {
    return localFontMetricsBase;
  }

  public String getSource() {
    return source;
  }
}
