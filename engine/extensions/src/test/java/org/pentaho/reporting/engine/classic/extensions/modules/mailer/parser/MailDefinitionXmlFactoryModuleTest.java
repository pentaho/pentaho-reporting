/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

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
