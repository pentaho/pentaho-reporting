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
