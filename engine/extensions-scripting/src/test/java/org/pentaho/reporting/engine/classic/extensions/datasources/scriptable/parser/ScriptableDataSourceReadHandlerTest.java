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

package org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.apache.xerces.util.AttributesProxy;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactory;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.PropertyReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.Locator;

public class ScriptableDataSourceReadHandlerTest {

  private static final String URI = "test/uri";
  private static final String TAG_NAME = "tag";
  private static final String LANG_VALUE = "language_value";
  private static final String SCRIPT_VALUE = "script_value";
  private static final String SHUTDOWN_SCRIPT_VALUE = "shutdown-script_value";
  private static final String QUERY_NAME = "query_name";
  private static final String ATTR_TYPE = "string";

  private ScriptableDataSourceReadHandler handler = new ScriptableDataSourceReadHandler();
  private RootXmlReadHandler rootXmlReadHandler = mock( RootXmlReadHandler.class );;

  @Before
  public void setUp() {
    Locator locator = mock( Locator.class );
    handler = new ScriptableDataSourceReadHandler();
    doReturn( locator ).when( rootXmlReadHandler ).getDocumentLocator();
  }

  @Test
  public void testGetHandlerForChild() throws Exception {
    handler.init( rootXmlReadHandler, URI, TAG_NAME );

    XmlReadHandler result = handler.getHandlerForChild( "__uri", "tagName", null );
    assertThat( result, is( nullValue() ) );

    result = handler.getHandlerForChild( URI, "tagName", null );
    assertThat( result, is( nullValue() ) );

    result = handler.getHandlerForChild( URI, "config", null );
    assertThat( result, is( notNullValue() ) );
    assertThat( result, is( instanceOf( ConfigReadHandler.class ) ) );

    result = handler.getHandlerForChild( URI, "query", null );
    assertThat( result, is( notNullValue() ) );
    assertThat( result, is( instanceOf( PropertyReadHandler.class ) ) );
  }

  @Test( expected = ParseException.class )
  public void testDoneParsingException() throws Exception {
    handler.init( rootXmlReadHandler, URI, TAG_NAME );
    handler.doneParsing();
  }

  @Test
  public void testDoneParsingWithConfHandler() throws Exception {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "language", null, URI ), ATTR_TYPE, LANG_VALUE );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    handler.init( rootXmlReadHandler, URI, TAG_NAME );
    ConfigReadHandler confHandler = (ConfigReadHandler) handler.getHandlerForChild( URI, "config", null );
    confHandler.init( rootXmlReadHandler, URI, TAG_NAME );
    confHandler.startParsing( fAttributesProxy );

    handler.doneParsing();

    assertThat( handler.getDataFactory(), is( notNullValue() ) );
    assertThat( handler.getDataFactory(), is( instanceOf( ScriptableDataFactory.class ) ) );
    ScriptableDataFactory sdf = (ScriptableDataFactory) handler.getDataFactory();
    assertThat( sdf.getLanguage(), is( equalTo( LANG_VALUE ) ) );
    assertThat( sdf.getScript(), is( nullValue() ) );
    assertThat( sdf.getShutdownScript(), is( nullValue() ) );
  }

  @Test
  public void testDoneParsing() throws Exception {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "language", null, URI ), ATTR_TYPE, LANG_VALUE );
    attrs.addAttribute( new QName( null, "script", null, URI ), ATTR_TYPE, SCRIPT_VALUE );
    attrs.addAttribute( new QName( null, "shutdown-script", null, URI ), ATTR_TYPE, SHUTDOWN_SCRIPT_VALUE );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    handler.init( rootXmlReadHandler, URI, TAG_NAME );
    ConfigReadHandler confHandler = (ConfigReadHandler) handler.getHandlerForChild( URI, "config", null );
    confHandler.init( rootXmlReadHandler, URI, TAG_NAME );
    confHandler.startParsing( fAttributesProxy );
    PropertyReadHandler queryHandler = (PropertyReadHandler) handler.getHandlerForChild( URI, "query", null );
    queryHandler.init( rootXmlReadHandler, URI, TAG_NAME );
    XMLAttributesImpl queryAttrs = new XMLAttributesImpl();
    queryAttrs.addAttribute( new QName( null, "name", null, URI ), ATTR_TYPE, QUERY_NAME );
    AttributesProxy queryAttrsProxy = new AttributesProxy( queryAttrs );
    queryHandler.startElement( URI, TAG_NAME, queryAttrsProxy );
    char[] chars = new char[] { 'b' };
    queryHandler.characters( chars, 0, chars.length );
    queryHandler.endElement( URI, TAG_NAME );

    handler.doneParsing();

    assertThat( handler.getDataFactory(), is( notNullValue() ) );
    assertThat( handler.getDataFactory(), is( instanceOf( ScriptableDataFactory.class ) ) );
    ScriptableDataFactory sdf = (ScriptableDataFactory) handler.getDataFactory();
    assertThat( sdf.getLanguage(), is( equalTo( LANG_VALUE ) ) );
    assertThat( sdf.getScript(), is( equalTo( SCRIPT_VALUE ) ) );
    assertThat( sdf.getShutdownScript(), is( equalTo( SHUTDOWN_SCRIPT_VALUE ) ) );
    assertThat( sdf.getQuery( QUERY_NAME ), is( equalTo( "b" ) ) );
    assertThat( handler.getObject(), is( instanceOf( ScriptableDataFactory.class ) ) );
    assertThat( (ScriptableDataFactory) handler.getObject(), is( equalTo( sdf ) ) );
  }
}
