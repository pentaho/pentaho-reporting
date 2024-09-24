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
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;

import org.apache.xerces.util.AttributesProxy;
import org.apache.xerces.util.XMLAttributesImpl;
import org.apache.xerces.xni.QName;
import org.junit.Test;
import org.pentaho.reporting.libraries.xmlns.parser.RootXmlReadHandler;
import org.xml.sax.SAXException;

public class ConfigReadHandlerTest {

  private static final String URI = "test/uri";
  private static final String LANG_VALUE = "language_value";
  private static final String SCRIPT_VALUE = "script_value";
  private static final String SHUTDOWN_SCRIPT_VALUE = "shutdown-script_value";
  private static final String ATTR_TYPE = "string";

  @Test
  public void testStartParsing() throws SAXException {
    XMLAttributesImpl attrs = new XMLAttributesImpl();
    attrs.addAttribute( new QName( null, "language", null, URI ), ATTR_TYPE, LANG_VALUE );
    attrs.addAttribute( new QName( null, "script", null, URI ), ATTR_TYPE, SCRIPT_VALUE );
    attrs.addAttribute( new QName( null, "shutdown-script", null, URI ), ATTR_TYPE, SHUTDOWN_SCRIPT_VALUE );
    AttributesProxy fAttributesProxy = new AttributesProxy( attrs );

    RootXmlReadHandler rootXmlReadHandler = mock( RootXmlReadHandler.class );

    ConfigReadHandler handler = new ConfigReadHandler();
    handler.init( rootXmlReadHandler, URI, "tag" );
    handler.startParsing( fAttributesProxy );

    assertThat( handler.getLanguage(), is( equalTo( LANG_VALUE ) ) );
    assertThat( handler.getScript(), is( equalTo( SCRIPT_VALUE ) ) );
    assertThat( handler.getShutdownScript(), is( equalTo( SHUTDOWN_SCRIPT_VALUE ) ) );
    assertThat( handler.getObject(), is( nullValue() ) );
  }

}
