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
 * Copyright (c) 2005-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.mailer.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import java.util.Properties;

import org.apache.xerces.util.AttributesProxy;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

public class SessionPropertiesReadHandlerTest {

  private static final String URI = "test/uri";
  private static final String TAG_NAME = "tag";
  private static final String ATTR_TYPE = "string";
  private static final String PROP_NAME = "test-name";
  private static final String PROP_VALUE = "test_val";

  private SessionPropertiesReadHandler handler;
  private RootXmlReadHandler rootXmlReadHandler;

  @Before
  public void setUp() throws Exception {
    rootXmlReadHandler = mock( RootXmlReadHandler.class );
    handler = new SessionPropertiesReadHandler();
    handler.init( rootXmlReadHandler, URI, TAG_NAME );
  }

  @Test
  public void testGetHandlerForChild() throws SAXException {
    XmlReadHandler result = handler.getHandlerForChild( "incorrect", TAG_NAME, null );
    assertThat( result, is( nullValue() ) );

    result = handler.getHandlerForChild( URI, TAG_NAME, null );
    assertThat( result, is( nullValue() ) );

    result = handler.getHandlerForChild( URI, "property", null );
    assertThat( result, is( instanceOf( SessionPropertyReadHandler.class ) ) );
  }

  @Test
  public void testDoneParsing() throws SAXException {
    Object prop = handler.getObject();
    assertThat( prop, is( nullValue() ) );

    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "name", null, URI ), ATTR_TYPE, PROP_NAME );
    attrs.addAttribute( new QName( null, "value", null, URI ), ATTR_TYPE, PROP_VALUE );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    XmlReadHandler childHandler = handler.getHandlerForChild( URI, "property", null );
    SessionPropertyReadHandler readHandler = (SessionPropertyReadHandler) childHandler;
    readHandler.init( rootXmlReadHandler, URI, TAG_NAME );
    readHandler.startParsing( fAttributesProxy );
    handler.doneParsing();
    prop = handler.getObject();

    assertThat( prop, is( instanceOf( Properties.class ) ) );
    assertThat( ( (Properties) prop ).getProperty( PROP_NAME ), is( equalTo( PROP_VALUE ) ) );
  }
}
