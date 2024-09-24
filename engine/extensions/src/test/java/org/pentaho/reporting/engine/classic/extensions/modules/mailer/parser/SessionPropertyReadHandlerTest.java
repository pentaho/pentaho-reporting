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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.apache.xerces.util.AttributesProxy;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.SAXException;

public class SessionPropertyReadHandlerTest {

  private static final String URI = "test/uri";
  private static final String TAG_NAME = "tag";
  private static final String ATTR_TYPE = "string";
  private static final String PROP_NAME = "test-name";
  private static final String PROP_VALUE = "test_val";

  private SessionPropertyReadHandler handler;

  @Before
  public void setUp() throws Exception {
    RootXmlReadHandler rootXmlReadHandler = mock( RootXmlReadHandler.class );
    handler = new SessionPropertyReadHandler();
    handler.init( rootXmlReadHandler, URI, TAG_NAME );
  }

  @Test
  public void testParsing() throws SAXException {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "name", null, URI ), ATTR_TYPE, PROP_NAME );
    attrs.addAttribute( new QName( null, "value", null, URI ), ATTR_TYPE, PROP_VALUE );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    handler.startParsing( fAttributesProxy );

    assertThat( handler.getObject(), is( nullValue() ) );
    assertThat( handler.getName(), is( equalTo( PROP_NAME ) ) );
    assertThat( handler.getValue(), is( equalTo( PROP_VALUE ) ) );
  }

  @Test( expected = ParseException.class )
  public void testParsingWithoutName() throws SAXException {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "name", null, URI ), ATTR_TYPE, null );
    attrs.addAttribute( new QName( null, "value", null, URI ), ATTR_TYPE, PROP_VALUE );    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );


    handler.startParsing( fAttributesProxy );
  }

  @Test( expected = ParseException.class )
  public void testParsingWithoutValue() throws SAXException {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "name", null, URI ), ATTR_TYPE, PROP_NAME );
    attrs.addAttribute( new QName( null, "value", null, URI ), ATTR_TYPE, null );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    handler.startParsing( fAttributesProxy );
  }
}
