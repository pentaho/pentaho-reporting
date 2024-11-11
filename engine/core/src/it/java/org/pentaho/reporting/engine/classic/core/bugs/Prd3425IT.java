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


package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.SimplePageDefinition;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.util.PageFormatFactory;
import org.pentaho.reporting.engine.classic.core.util.PageSize;
import org.pentaho.reporting.libraries.base.util.MemoryByteArrayOutputStream;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.awt.print.PageFormat;
import java.awt.print.Paper;

public class Prd3425IT extends TestCase {
  public Prd3425IT() {
  }

  public Prd3425IT( final String name ) {
    super( name );
  }

  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  public void testLandscapeLoadSave() throws Exception {
    final PageFormatFactory pff = PageFormatFactory.getInstance();
    final Paper format = pff.createPaper( PageSize.LETTER );
    pff.setBorders( format, 10, 20, 30, 40 );

    final MasterReport orgReport = new MasterReport();
    orgReport.setPageDefinition( new SimplePageDefinition( pff.createPageFormat( format, PageFormat.LANDSCAPE ) ) );

    final MasterReport savedReport = postProcess( orgReport );
    assertEquals( orgReport.getPageDefinition(), savedReport.getPageDefinition() );
  }

  public void testReverseLandscapeLoadSave() throws Exception {
    final PageFormatFactory pff = PageFormatFactory.getInstance();
    final Paper format = pff.createPaper( PageSize.LETTER );
    pff.setBorders( format, 10, 20, 30, 40 );

    final MasterReport orgReport = new MasterReport();
    orgReport
        .setPageDefinition( new SimplePageDefinition( pff.createPageFormat( format, PageFormat.REVERSE_LANDSCAPE ) ) );

    final MasterReport savedReport = postProcess( orgReport );
    assertEquals( orgReport.getPageDefinition(), savedReport.getPageDefinition() );
  }

  public void testPortraitLoadSave() throws Exception {
    final PageFormatFactory pff = PageFormatFactory.getInstance();
    final Paper format = pff.createPaper( PageSize.LETTER );
    pff.setBorders( format, 10, 20, 30, 40 );

    final MasterReport orgReport = new MasterReport();
    orgReport.setPageDefinition( new SimplePageDefinition( pff.createPageFormat( format, PageFormat.PORTRAIT ) ) );

    final MasterReport savedReport = postProcess( orgReport );
    assertEquals( orgReport.getPageDefinition(), savedReport.getPageDefinition() );
  }

  protected MasterReport postProcess( final MasterReport originalReport ) throws Exception {
    final MemoryByteArrayOutputStream bout = new MemoryByteArrayOutputStream();
    BundleWriter.writeReportToZipStream( originalReport, bout );
    assertTrue( bout.getLength() > 0 );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource reportRes = mgr.createDirectly( bout.toByteArray(), MasterReport.class );
    return (MasterReport) reportRes.getResource();
  }

}
