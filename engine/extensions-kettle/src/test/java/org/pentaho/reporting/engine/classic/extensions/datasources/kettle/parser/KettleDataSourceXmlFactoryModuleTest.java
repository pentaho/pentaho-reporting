/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;
import org.pentaho.reporting.libraries.xmlns.parser.XmlDocumentInfo;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;

public class KettleDataSourceXmlFactoryModuleTest {

  private KettleDataSourceXmlFactoryModule kettleDataSourceXmlFactoryModule;

  @Before
  public void before() {
    kettleDataSourceXmlFactoryModule = new KettleDataSourceXmlFactoryModule();
  }

  @Test
  public void getDocumentSupport_not_recognized() {
    XmlDocumentInfo xmlDocumentInfo = mock( XmlDocumentInfo.class );
    int actualValue = kettleDataSourceXmlFactoryModule.getDocumentSupport( xmlDocumentInfo );
    assertEquals( XmlFactoryModule.NOT_RECOGNIZED, actualValue );
  }

  @Test
  public void getDocumentSupport_recognized_by_tagname() {
    XmlDocumentInfo xmlDocumentInfo = mock( XmlDocumentInfo.class );
    when( xmlDocumentInfo.getRootElement() ).thenReturn( "kettle-datasource" );
    int actualValue = kettleDataSourceXmlFactoryModule.getDocumentSupport( xmlDocumentInfo );
    assertEquals( XmlFactoryModule.RECOGNIZED_BY_TAGNAME, actualValue );
  }

  @Test
  public void getDocumentSupport_recognized_by_namespace() {
    XmlDocumentInfo xmlDocumentInfo = mock( XmlDocumentInfo.class );
    when( xmlDocumentInfo.getRootElementNameSpace() ).thenReturn(
        "http://jfreereport.sourceforge.net/namespaces/datasources/kettle" );
    when( xmlDocumentInfo.getRootElement() ).thenReturn( "kettle-datasource" );
    int actualValue = kettleDataSourceXmlFactoryModule.getDocumentSupport( xmlDocumentInfo );
    assertEquals( XmlFactoryModule.RECOGNIZED_BY_NAMESPACE, actualValue );
  }

}
