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


package org.pentaho.reporting.engine.classic.core.crosstab;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.net.URL;

public class BrokeCellsIT extends TestCase {
  public BrokeCellsIT() {
  }

  public BrokeCellsIT( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testTest() throws Exception {
    final URL url = getClass().getResource( "cells-broken.prpt" );
    final ResourceManager manager = new ResourceManager();
    manager.registerDefaults();
    final Resource res = manager.createDirectly( url, MasterReport.class );
    final MasterReport report = (MasterReport) res.getResource();
    final LogicalPageBox box = DebugReportRunner.layoutPage( report, 0 );
    // DebugReportRunner.showDialog(report);
  }
}
