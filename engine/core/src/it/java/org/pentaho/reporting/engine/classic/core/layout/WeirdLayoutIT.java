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


package org.pentaho.reporting.engine.classic.core.layout;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.print.PageFormat;
import java.net.URL;

/**
 * Creation-Date: 05.04.2007, 17:35:00
 *
 * @author Thomas Morgner
 */
public class WeirdLayoutIT extends TestCase {
  public WeirdLayoutIT() {
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testLayout() throws ResourceException, ContentProcessingException, ReportProcessingException {
    final MasterReport basereport = new MasterReport();
    basereport.setPageDefinition( new SimplePageDefinition( new PageFormat() ) );

    final URL target = WeirdLayoutIT.class.getResource( "weird-layouting.xml" );
    final ResourceManager rm = new ResourceManager();
    rm.registerDefaults();
    final Resource directly = rm.createDirectly( target, MasterReport.class );
    final MasterReport report = (MasterReport) directly.getResource();

    final Band band = report.getReportHeader();
    band.setName( "ReportHeader1" );

    DebugReportRunner.layoutSingleBand( report, band, false, true );

  }

}
