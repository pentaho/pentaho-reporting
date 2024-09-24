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
