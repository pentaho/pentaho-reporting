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


package org.pentaho.reporting.engine.classic.core.bugs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.SQLReportDataFactory;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class Prd4843IT {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testSampleReport() throws Exception {
    URL resource = getClass().getResource( "Prd-4843.prpt" );
    MasterReport report =
        (MasterReport) new ResourceManager().createDirectly( resource, MasterReport.class ).getResource();
    CompoundDataFactory dataFactory = (CompoundDataFactory) report.getDataFactory();
    SQLReportDataFactory dataFactory1 = (SQLReportDataFactory) dataFactory.get( 0 );
    DriverConnectionProvider conProv1 = (DriverConnectionProvider) dataFactory1.getConnectionProvider();
    Assert.assertEquals( "abcdefghijk", conProv1.getProperty( "user" ) );
    Assert.assertEquals( "abcdefghijk", conProv1.getProperty( "password" ) );
    SQLReportDataFactory dataFactory2 = (SQLReportDataFactory) dataFactory.get( 1 );
    DriverConnectionProvider conProv2 = (DriverConnectionProvider) dataFactory2.getConnectionProvider();
    Assert.assertEquals( "abcdefghijkl", conProv2.getProperty( "user" ) );
    Assert.assertEquals( "abcdefghijkl", conProv2.getProperty( "password" ) );
  }
}
