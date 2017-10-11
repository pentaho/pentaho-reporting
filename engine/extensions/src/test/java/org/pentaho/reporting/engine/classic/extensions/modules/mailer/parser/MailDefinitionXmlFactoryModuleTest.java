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
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.extensions.modules.mailer.MailModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

public class MailDefinitionXmlFactoryModuleTest {

  private MailDefinitionXmlFactoryModule xmlFactoryModule = new MailDefinitionXmlFactoryModule();
  private XmlDocumentInfo docInfo = mock( XmlDocumentInfo.class );

  @Test
  public void testGgetDocumentSupport() {
    int result = xmlFactoryModule.getDocumentSupport( docInfo );
    assertThat( result, is( equalTo( XmlFactoryModule.NOT_RECOGNIZED ) ) );

    doReturn( "incorrect" ).when( docInfo ).getRootElementNameSpace();
    doReturn( null ).when( docInfo ).getRootElement();
    result = xmlFactoryModule.getDocumentSupport( docInfo );
    assertThat( result, is( equalTo( XmlFactoryModule.NOT_RECOGNIZED ) ) );

    doReturn( MailModule.NAMESPACE ).when( docInfo ).getRootElementNameSpace();
    doReturn( "mail-definition" ).when( docInfo ).getRootElement();
    result = xmlFactoryModule.getDocumentSupport( docInfo );
    assertThat( result, is( equalTo( XmlFactoryModule.RECOGNIZED_BY_NAMESPACE ) ) );

    doReturn( MailModule.NAMESPACE ).when( docInfo ).getRootElementNameSpace();
    doReturn( "incorrect" ).when( docInfo ).getRootElement();
    result = xmlFactoryModule.getDocumentSupport( docInfo );
    assertThat( result, is( equalTo( XmlFactoryModule.NOT_RECOGNIZED ) ) );

    doReturn( StringUtils.EMPTY ).when( docInfo ).getRootElementNameSpace();
    doReturn( "mail-definition" ).when( docInfo ).getRootElement();
    result = xmlFactoryModule.getDocumentSupport( docInfo );
    assertThat( result, is( equalTo( XmlFactoryModule.RECOGNIZED_BY_TAGNAME ) ) );
  }

  @Test
  public void testGetDefaultNamespace() {
    String result = xmlFactoryModule.getDefaultNamespace( docInfo );
    assertThat( result, is( equalTo( MailModule.NAMESPACE ) ) );
  }

  @Test
  public void testCreateReadHandler() {
    XmlReadHandler result = xmlFactoryModule.createReadHandler( docInfo );
    assertThat( result, is( notNullValue() ) );
    assertThat( result, is( instanceOf( MailDefinitionReadHandler.class ) ) );
  }
}
