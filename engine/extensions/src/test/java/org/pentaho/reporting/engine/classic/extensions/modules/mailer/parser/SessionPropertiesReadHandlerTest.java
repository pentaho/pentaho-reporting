/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
