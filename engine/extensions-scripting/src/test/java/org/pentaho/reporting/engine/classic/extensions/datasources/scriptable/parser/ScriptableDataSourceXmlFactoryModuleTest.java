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

package org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.parser;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.ScriptableDataFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlReadHandler;

public class ScriptableDataSourceXmlFactoryModuleTest {

  private ScriptableDataSourceXmlFactoryModule module = new ScriptableDataSourceXmlFactoryModule();

  @Test
  public void testGetDefaultNamespace() {
    String result = module.getDefaultNamespace( null );
    assertThat( result, is( equalTo( ScriptableDataFactoryModule.NAMESPACE ) ) );

    XmlDocumentInfo documentInfo = mock( XmlDocumentInfo.class );
    doReturn( "test_namespace" ).when( documentInfo ).getDefaultNameSpace();
    result = module.getDefaultNamespace( documentInfo );
    assertThat( result, is( equalTo( ScriptableDataFactoryModule.NAMESPACE ) ) );
  }

  @Test
  public void testCreateReadHandler() {
    XmlReadHandler result = module.createReadHandler( null );
    assertThat( result, is( notNullValue() ) );
    assertThat( result, is( instanceOf( ScriptableDataSourceReadHandler.class ) ) );
  }

  @Test
  public void testGetDocumentSupport() {
    XmlDocumentInfo documentInfo = mock( XmlDocumentInfo.class );
    doReturn( null ).when( documentInfo ).getRootElementNameSpace();
    doReturn( "test_elem" ).when( documentInfo ).getRootElement();

    int result = module.getDocumentSupport( documentInfo );
    assertThat( result, is( equalTo( XmlFactoryModule.NOT_RECOGNIZED ) ) );

    doReturn( "test_namespace" ).when( documentInfo ).getRootElementNameSpace();
    result = module.getDocumentSupport( documentInfo );
    assertThat( result, is( equalTo( XmlFactoryModule.NOT_RECOGNIZED ) ) );

    doReturn( ScriptableDataFactoryModule.NAMESPACE ).when( documentInfo ).getRootElementNameSpace();
    result = module.getDocumentSupport( documentInfo );
    assertThat( result, is( equalTo( XmlFactoryModule.NOT_RECOGNIZED ) ) );

    doReturn( ScriptableDataFactoryModule.NAMESPACE ).when( documentInfo ).getRootElementNameSpace();
    doReturn( "scriptable-datasource" ).when( documentInfo ).getRootElement();
    result = module.getDocumentSupport( documentInfo );
    assertThat( result, is( equalTo( XmlFactoryModule.RECOGNIZED_BY_NAMESPACE ) ) );

    doReturn( null ).when( documentInfo ).getRootElementNameSpace();
    doReturn( "scriptable-datasource" ).when( documentInfo ).getRootElement();
    result = module.getDocumentSupport( documentInfo );
    assertThat( result, is( equalTo( XmlFactoryModule.RECOGNIZED_BY_TAGNAME ) ) );
  }
}
