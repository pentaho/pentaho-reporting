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
