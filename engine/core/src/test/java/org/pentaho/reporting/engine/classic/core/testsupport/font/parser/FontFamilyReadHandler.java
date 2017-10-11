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

public class FontFamilyReadHandler extends AbstractXmlReadHandler {
  private String name;
  private ArrayList<FontRecordReadHandler> fontRecords;

  public FontFamilyReadHandler() {
    fontRecords = new ArrayList<FontRecordReadHandler>();
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    name = attrs.getValue( getUri(), "name" );
  }

  protected XmlReadHandler getHandlerForChild( final String uri, final String tagName, final Attributes atts )
    throws SAXException {
    if ( isSameNamespace( uri ) ) {
      final FontRecordReadHandler recordReadHandler = new FontRecordReadHandler();
      fontRecords.add( recordReadHandler );
      return recordReadHandler;
    }
    return null;
  }

  public LocalFontFamily getObject() throws SAXException {
    final LocalFontFamily fontFamily = new LocalFontFamily( name );
    for ( int i = 0; i < fontRecords.size(); i++ ) {
      final FontRecordReadHandler record = fontRecords.get( i );
      fontFamily.setFontRecord( record.isBold(), record.isItalics(), record.getSource() );
    }
    return fontFamily;
  }
}
