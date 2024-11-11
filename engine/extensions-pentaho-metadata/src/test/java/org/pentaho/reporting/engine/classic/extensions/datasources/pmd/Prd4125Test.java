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


package org.pentaho.reporting.engine.classic.extensions.datasources.pmd;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeDataSchemaModel;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.naming.spi.NamingManager;
import java.net.URL;

public class Prd4125Test extends TestCase {
  public Prd4125Test() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    if ( NamingManager.hasInitialContextFactoryBuilder() == false ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }
  }

  public void testRuntime() throws Exception {
    URL resource = getClass().getResource( "Prd-4125.prpt" );
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    DebugReportRunner.executeAll( report );
  }

  public void testDesignTime() throws ResourceException {
    URL resource = getClass().getResource( "Prd-4125.prpt" );
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    MasterReport report = (MasterReport) mgr.createDirectly( resource, MasterReport.class ).getResource();

    DesignTimeDataSchemaModel model = new DesignTimeDataSchemaModel( report );
    String[] columnNames = model.getColumnNames();
    assertEquals( 26, columnNames.length );
  }
}
