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
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.extensions.modules.mailer.FormulaHeader;
import org.pentaho.reporting.engine.classic.extensions.modules.mailer.MailHeader;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;
import org.xml.sax.SAXException;

public class HeadersReadHandlerTest {

  private static final String URI = "test/uri";

  private HeadersReadHandler handler;

  @Before
  public void setUp() throws SAXException {
    RootXmlReadHandler rootXmlReadHandler = mock( RootXmlReadHandler.class );
    handler = new HeadersReadHandler();
    handler.init( rootXmlReadHandler, URI, "tag" );
  }

  @Test
  public void testGetHandlerForChild() throws SAXException {
    XmlReadHandler childHandler = handler.getHandlerForChild( "incorrectUri", "tag", null );
    assertThat( childHandler, is( nullValue() ) );

    childHandler = handler.getHandlerForChild( URI, "tag", null );
    assertThat( childHandler, is( nullValue() ) );

    childHandler = handler.getHandlerForChild( URI, "formula-header", null );
    assertThat( childHandler, is( notNullValue() ) );
    assertThat( childHandler, is( instanceOf( FormulaHeaderReadHandler.class ) ) );

    childHandler = handler.getHandlerForChild( URI, "static-header", null );
    assertThat( childHandler, is( notNullValue() ) );
    assertThat( childHandler, is( instanceOf( StaticHeaderReadHandler.class ) ) );
  }

  @Test
  public void testDoneParsing() throws SAXException {
    handler.getHandlerForChild( URI, "formula-header", null );
    handler.doneParsing();
    MailHeader[] headers = handler.getHeaders();

    assertThat( headers, is( notNullValue() ) );
    assertThat( headers.length, is( equalTo( 1 ) ) );
    assertThat( headers[0], is( instanceOf( FormulaHeader.class ) ) );

    Object objResult = handler.getObject();
    assertThat( objResult, is( notNullValue() ) );
    assertThat( (MailHeader[]) objResult, is( equalTo( headers ) ) );
  }
}
