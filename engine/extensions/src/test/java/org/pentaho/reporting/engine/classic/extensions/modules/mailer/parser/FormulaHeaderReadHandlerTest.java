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

import org.apache.xerces.util.AttributesProxy;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import org.hamcrest.CoreMatchers;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.extensions.modules.mailer.FormulaHeader;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.SAXException;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

public class FormulaHeaderReadHandlerTest {

  private static final String URI = "test/uri";
  private static final String NAME_VALUE = "name_value";
  private static final String FORMULA_VALUE = "formula_value";
  private static final String ATTR_TYPE = "string";

  private FormulaHeaderReadHandler handler;

  @Before
  public void setUp() throws SAXException {
    RootXmlReadHandler rootXmlReadHandler = mock( RootXmlReadHandler.class );
    handler = new FormulaHeaderReadHandler();
    handler.init( rootXmlReadHandler, URI, "tag" );
  }

  @Test
  public void testStartParsing() throws SAXException {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "name", null, URI ), ATTR_TYPE, NAME_VALUE );
    attrs.addAttribute( new QName( null, "formula", null, URI ), ATTR_TYPE, FORMULA_VALUE );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    handler.startParsing( fAttributesProxy );

    Object result = handler.getObject();
    assertThat( result, is( notNullValue() ) );
    assertThat( result, is( CoreMatchers.instanceOf( FormulaHeader.class ) ) );
    FormulaHeader formulaHeader = (FormulaHeader) result;
    assertThat( formulaHeader.getName(), is( equalTo( NAME_VALUE ) ) );
  }

  @Test( expected = ParseException.class )
  public void testStartParsingWithoutName() throws SAXException {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "name", null, URI ), ATTR_TYPE, null );
    attrs.addAttribute( new QName( null, "formula", null, URI ), ATTR_TYPE, FORMULA_VALUE );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    handler.startParsing( fAttributesProxy );
  }

  @Test( expected = ParseException.class )
  public void testStartParsingWithoutFormula() throws SAXException {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "name", null, URI ), ATTR_TYPE, NAME_VALUE );
    attrs.addAttribute( new QName( null, "formula", null, URI ), ATTR_TYPE, null );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    handler.startParsing( fAttributesProxy );
  }

}
