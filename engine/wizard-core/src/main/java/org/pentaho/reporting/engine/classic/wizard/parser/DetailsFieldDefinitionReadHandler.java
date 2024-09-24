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

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.ReportParserUtil;
import org.pentaho.reporting.engine.classic.wizard.model.DefaultDetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.DetailFieldDefinition;
import org.pentaho.reporting.engine.classic.wizard.model.Length;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.xmlns.common.ParserUtil;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DetailsFieldDefinitionReadHandler extends AbstractXmlReadHandler {
  private DetailFieldDefinition detailField;

  public DetailsFieldDefinitionReadHandler() {
    detailField = new DefaultDetailFieldDefinition();
  }

  protected void startParsing( final Attributes attrs ) throws SAXException {
    final String hAlightAttr = attrs.getValue( getUri(), "horizontal-align" );
    detailField.setHorizontalAlignment( ReportParserUtil.parseHorizontalElementAlignment( hAlightAttr, getLocator() ) );
    final String vAlightAttr = attrs.getValue( getUri(), "vertical-align" );
    detailField.setVerticalAlignment( ReportParserUtil.parseVerticalElementAlignment( vAlightAttr, getLocator() ) );

    final String colorAttr = attrs.getValue( getUri(), "font-color" );
    final String backgroundColorAttr = attrs.getValue( getUri(), "background-color" );
    detailField.setFontColor( ReportParserUtil.parseColor( colorAttr, null ) );
    detailField.setBackgroundColor( ReportParserUtil.parseColor( backgroundColorAttr, null ) );

    detailField.setFontName( attrs.getValue( getUri(), "font-name" ) );
    final String fontSizeAttr = attrs.getValue( getUri(), "font-size" );
    detailField.setFontSize( ReportParserUtil.parseInteger( fontSizeAttr, getLocator() ) );

    final String boldAttr = attrs.getValue( getUri(), "bold" );
    detailField.setFontBold( ParserUtil.parseBoolean( boldAttr, getLocator() ) );
    final String italicAttr = attrs.getValue( getUri(), "italic" );
    detailField.setFontItalic( ParserUtil.parseBoolean( italicAttr, getLocator() ) );
    final String underlineAttr = attrs.getValue( getUri(), "underline" );
    detailField.setFontUnderline( ParserUtil.parseBoolean( underlineAttr, getLocator() ) );
    final String strikethroughAttr = attrs.getValue( getUri(), "strikethrough" );
    detailField.setFontStrikethrough( ParserUtil.parseBoolean( strikethroughAttr, getLocator() ) );

    detailField.setNullString( attrs.getValue( getUri(), "null-string" ) );
    final String displayName = attrs.getValue( getUri(), "display-name" );
    detailField.setDisplayName( displayName );
    detailField.setField( attrs.getValue( getUri(), "field" ) );
    detailField.setDataFormat( attrs.getValue( getUri(), "data-format" ) );

    final String aggFunctionName = attrs.getValue( getUri(), "aggregation-function" );
    if ( aggFunctionName != null ) {
      final ClassLoader classLoader = ObjectUtilities.getClassLoader( DetailsFieldDefinitionReadHandler.class );
      try {
        final Class c = Class.forName( aggFunctionName, false, classLoader );
        if ( Expression.class.isAssignableFrom( c ) == false ) {
          throw new ParseException( "Failed to parse attribute 'aggregation-function': invalid class name" );
        }
        detailField.setAggregationFunction( c );
      } catch ( final ClassNotFoundException e ) {
        throw new ParseException( "Failed to parse attribute 'aggregation-function': invalid class name", e );
      }
    }

    final String widthAttr = attrs.getValue( getUri(), "width" );
    detailField.setWidth( Length.parseLength( widthAttr ) );
    final String onlyDistinctAttr = attrs.getValue( getUri(), "only-show-distinct" );
    detailField.setOnlyShowChangingValues( ParserUtil.parseBoolean( onlyDistinctAttr, getLocator() ) );
  }

  public Object getObject() throws SAXException {
    return detailField;
  }
}
