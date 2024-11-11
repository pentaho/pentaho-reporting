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


package org.pentaho.reporting.engine.classic.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldenSampleGenerator;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;

public class StyleInheritanceLegacyTest extends TestCase {
  public StyleInheritanceLegacyTest() {
  }

  public StyleInheritanceLegacyTest( final String name ) {
    super( name );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testSample() throws ResourceException {
    final File marker = GoldenSampleGenerator.findMarker();
    final File report = new File( marker, "reports/stylesheets.xml" );
    ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    Resource directly = mgr.createDirectly( report, MasterReport.class );
    final MasterReport resource = (MasterReport) directly.getResource();
  }
}
