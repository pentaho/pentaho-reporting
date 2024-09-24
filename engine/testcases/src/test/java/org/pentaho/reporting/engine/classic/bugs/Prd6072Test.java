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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportParameterValidationException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.SinglePageFlowSelector;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllItemsHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.PageableHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.StreamHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugJndiContextFactoryBuilder;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;
import org.pentaho.reporting.libraries.repository.zipwriter.ZipRepository;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.naming.spi.NamingManager;

import static org.junit.Assert.assertNotNull;

public class Prd6072Test {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
    if ( NamingManager.hasInitialContextFactoryBuilder() == false ) {
      NamingManager.setInitialContextFactoryBuilder( new DebugJndiContextFactoryBuilder() );
    }
  }

  @Test
  public void testPrd6072() throws Exception {
    final URL resource = getClass().getResource( "Prd-6072.prpt" );
    assertNotNull( resource );

    final ResourceManager mgr = new ResourceManager();
    mgr.registerDefaults();
    final Resource parsed = mgr.createDirectly( resource, MasterReport.class );
    final MasterReport report = (MasterReport) parsed.getResource();

    String outHtml = initFlowReportProcessor( report );
    //check for no duplicate content on both td and div - false is good
    Assert.assertFalse( outHtml.contains( "<td valign=\"middle\" title=\"Change integration code\" "
      + "class=\"style-14\"><div title=\"Change integration code\"></div></td>" ) );
  }


  public String initFlowReportProcessor( MasterReport report) throws Exception {
    try {
      if ( report == null ) {
        throw new NullPointerException();
      }

      try {
        final ByteArrayOutputStream out = new ByteArrayOutputStream();
        final StreamRepository html = new StreamRepository( out );

        final ZipRepository zipRepository = new ZipRepository( new NullOutputStream() );
        final ContentLocation data = RepositoryUtilities.createLocation
          ( zipRepository, RepositoryUtilities.splitPath( "data", "/" ) );
        final ContentLocation root = html.getRoot();

        final StreamHtmlOutputProcessor outputProcessor =
          new StreamHtmlOutputProcessor( report.getConfiguration() );

        final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
        printer.setContentWriter( root, new DefaultNameGenerator( root, "report.html" ) );
        printer.setDataWriter( data, new DefaultNameGenerator( data, "content" ) );
        printer.setUrlRewriter( new Prd5143Test.NullURLRewriter() );
        outputProcessor.setPrinter( printer );

        final FlowReportProcessor sp = new FlowReportProcessor( report, outputProcessor );
        sp.processReport();
        sp.close();
        zipRepository.close();
        return out.toString( "UTF-8");

      } catch ( IOException ioe ) {
        throw ioe;
      } catch ( ReportProcessingException re ) {
        throw re;
      } catch ( Exception re ) {
        throw new ReportProcessingException( "Failed to process the report", re );
      }
    } catch ( ReportParameterValidationException e ) {
      Assert.fail();
      throw new IllegalStateException( "This statement is never reached" );
    }

  }
}
