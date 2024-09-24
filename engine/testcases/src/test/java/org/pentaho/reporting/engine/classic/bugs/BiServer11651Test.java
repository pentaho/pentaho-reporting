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

package org.pentaho.reporting.engine.classic.bugs;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.html.FastHtmlContentItems;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.html.FastHtmlExportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.validator.ReportStructureValidator;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllItemsHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.StaticURLRewriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.StreamHtmlOutputProcessor;
import org.pentaho.reporting.libraries.repository.ContentIOException;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;
import org.pentaho.reporting.libraries.repository.zip.ZipRepository;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;

public class BiServer11651Test {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testTooltips() throws ContentIOException, ReportProcessingException, IOException, ResourceException {
    URL res = getClass().getResource( "BISERVER-11651-tooltip.prpt" );
    Assert.assertNotNull( res );

    MasterReport report = (MasterReport) new ResourceManager().createDirectly( res, MasterReport.class ).getResource();
    final ByteArrayOutputStream boutFast = new ByteArrayOutputStream();
    final ByteArrayOutputStream boutSlow = new ByteArrayOutputStream();
    processFastStreamHtml( report, boutFast );
    processSlowStreamHtml( report, boutSlow );
    String htmlFast = boutFast.toString( "UTF-8" );
    String htmlSlow = boutSlow.toString( "UTF-8" );
    Assert.assertEquals( htmlSlow, htmlFast );
  }

  @Test
  public void testEPAMsample() throws ContentIOException, ReportProcessingException, IOException, ResourceException {
    URL res = getClass().getResource( "BISERVER-11651-validation.prpt" );
    Assert.assertNotNull( res );

    MasterReport report = (MasterReport) new ResourceManager().createDirectly( res, MasterReport.class ).getResource();
    final ByteArrayOutputStream boutFast = new ByteArrayOutputStream();
    final ByteArrayOutputStream boutSlow = new ByteArrayOutputStream();
    processFastStreamHtml( report, boutFast );
    processSlowStreamHtml( report, boutSlow );
    String htmlFast = boutFast.toString( "UTF-8" );
    String htmlSlow = boutSlow.toString( "UTF-8" );
    Assert.assertEquals( htmlSlow, htmlFast );
  }

  @Test
  public void testURLs() throws ContentIOException, ReportProcessingException, IOException, ResourceException {
    URL res = getClass().getResource( "BISERVER-11651-url.prpt" );
    Assert.assertNotNull( res );

    MasterReport report = (MasterReport) new ResourceManager().createDirectly( res, MasterReport.class ).getResource();
    final ByteArrayOutputStream boutFast = new ByteArrayOutputStream();
    final ByteArrayOutputStream boutSlow = new ByteArrayOutputStream();
    processFastStreamHtml( report, boutFast );
    processSlowStreamHtml( report, boutSlow );
    String htmlFast = boutFast.toString( "UTF-8" );
    String htmlSlow = boutSlow.toString( "UTF-8" );
    Assert.assertEquals( htmlSlow, htmlFast );
  }


  public static void processSlowStreamHtml( final MasterReport report,
                                            final OutputStream outputStream )
    throws ReportProcessingException, ContentIOException {
    if ( report == null ) {
      throw new NullPointerException();
    }
    if ( outputStream == null ) {
      throw new NullPointerException();
    }

    final ZipRepository zipRepository = new ZipRepository();
    final StreamRepository targetRepository = new StreamRepository( outputStream );
    final ContentLocation targetRoot = targetRepository.getRoot();

    final HtmlOutputProcessor outputProcessor = new StreamHtmlOutputProcessor( report.getConfiguration() );
    final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
    printer.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, "index", "html" ) );
    printer.setDataWriter( zipRepository.getRoot(), new DefaultNameGenerator( targetRoot, "data", "bin" ) );
    printer.setUrlRewriter( new StaticURLRewriter( "http://localhost:12345/content/{0}" ) );
    outputProcessor.setPrinter( printer );

    final StreamReportProcessor sp = new StreamReportProcessor( report, outputProcessor );
    sp.processReport();
    sp.close();
  }

  public static void processFastStreamHtml( MasterReport report,
                                            OutputStream out )
    throws ReportProcessingException, IOException, ContentIOException {
    ReportStructureValidator validator = new ReportStructureValidator();
    Assert.assertTrue( validator.isValidForFastProcessing( report ) );

    final ZipRepository zipRepository = new ZipRepository();
    final StreamRepository targetRepository = new StreamRepository( out );
    final ContentLocation targetRoot = targetRepository.getRoot();
    final FastHtmlContentItems contentItems = new FastHtmlContentItems();
    contentItems.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, "index", "html" ) );
    contentItems.setDataWriter( zipRepository.getRoot(), new DefaultNameGenerator( targetRoot, "data", "bin" ) );
    contentItems.setUrlRewriter( new StaticURLRewriter( "http://localhost:12345/content/{0}" ) );

    final FastHtmlExportProcessor reportProcessor = new FastHtmlExportProcessor( report, contentItems );
    reportProcessor.processReport();
    reportProcessor.close();
    out.flush();
  }
}
