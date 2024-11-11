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
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.apache.xerces.util.AttributesProxy;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.pentaho.reporting.engine.classic.extensions.modules.mailer.StaticHeader;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.SAXException;

public class StaticHeaderReadHandlerTest {
  private static final String URI = "test/uri";
  private static final String TAG_NAME = "tag";
  private static final String ATTR_TYPE = "string";
  private static final String PROP_NAME = "test-name";
  private static final String PROP_VALUE = "test_val";

  private StaticHeaderReadHandler handler;


  @Before
  public void setUp() throws Exception {
    RootXmlReadHandler rootXmlReadHandler = mock( RootXmlReadHandler.class );
    handler = new StaticHeaderReadHandler();
    handler.init( rootXmlReadHandler, URI, TAG_NAME );
  }

  @Test
  public void testParsing() throws SAXException {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "name", null, URI ), ATTR_TYPE, PROP_NAME );
    attrs.addAttribute( new QName( null, "value", null, URI ), ATTR_TYPE, PROP_VALUE );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    handler.startParsing( fAttributesProxy );

    Object result = handler.getObject();
    assertThat( result, is( CoreMatchers.instanceOf( StaticHeader.class ) ) );
    StaticHeader header = (StaticHeader) result;
    assertThat( header.getName(), is( equalTo( PROP_NAME ) ) );
    assertThat( header.getValue( null ), is( equalTo( PROP_VALUE ) ) );
  }

  @Test( expected = ParseException.class )
  public void testParsingWithoutName() throws SAXException {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "name", null, URI ), ATTR_TYPE, null );
    attrs.addAttribute( new QName( null, "value", null, URI ), ATTR_TYPE, PROP_VALUE );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

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
