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

package org.pentaho.reporting.engine.classic.wizard.parser;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.wizard.model.RootBandDefinition;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class RootBandDefinitionReadHandler extends AbstractXmlReadHandler {
  private RootBandDefinition rootBand;

  public RootBandDefinitionReadHandler( final RootBandDefinition rootBand ) {
    this.rootBand = rootBand;
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    final String hAlightAttr = attrs.getValue( getUri(), "horizontal-align" );
    rootBand.setHorizontalAlignment( ReportParserUtil.parseHorizontalElementAlignment( hAlightAttr, getLocator() ) );
    final String vAlightAttr = attrs.getValue( getUri(), "vertical-align" );
    rootBand.setVerticalAlignment( ReportParserUtil.parseVerticalElementAlignment( vAlightAttr, getLocator() ) );

    final String colorAttr = attrs.getValue( getUri(), "font-color" );
    final String backgroundColorAttr = attrs.getValue( getUri(), "background-color" );
    rootBand.setFontColor( ReportParserUtil.parseColor( colorAttr ) );
    rootBand.setBackgroundColor( ReportParserUtil.parseColor( backgroundColorAttr ) );

    rootBand.setFontName( attrs.getValue( getUri(), "font-name" ) );
    final String fontSizeAttr = attrs.getValue( getUri(), "font-size" );
    rootBand.setFontSize( ReportParserUtil.parseInteger( fontSizeAttr, getLocator() ) );
    final String visibleAttr = attrs.getValue( getUri(), "visible" );
    rootBand.setVisible( visibleAttr == null || "true".equals( visibleAttr ) );
    final String repeatAttr = attrs.getValue( getUri(), "repeat" );
    rootBand.setRepeat( ParserUtil.parseBoolean( repeatAttr, getLocator() ) );

    final String boldAttr = attrs.getValue( getUri(), "bold" );
    rootBand.setFontBold( ParserUtil.parseBoolean( boldAttr, getLocator() ) );
    final String italicAttr = attrs.getValue( getUri(), "italic" );
    rootBand.setFontItalic( ParserUtil.parseBoolean( italicAttr, getLocator() ) );
    final String underlineAttr = attrs.getValue( getUri(), "underline" );
    rootBand.setFontUnderline( ParserUtil.parseBoolean( underlineAttr, getLocator() ) );
    final String strikethroughAttr = attrs.getValue( getUri(), "strikethrough" );
    rootBand.setFontStrikethrough( ParserUtil.parseBoolean( strikethroughAttr, getLocator() ) );

  }

  public Object getObject() throws SAXException {
    return rootBand;
  }
}
