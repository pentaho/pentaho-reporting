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
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportParameterValidationException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.SinglePageFlowSelector;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllItemsHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.PageableHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.URLRewriteException;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.URLRewriter;
import org.pentaho.reporting.engine.classic.core.testsupport.DebugReportRunner;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.repository.ContentEntity;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.RepositoryUtilities;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;
import org.pentaho.reporting.libraries.repository.zipwriter.ZipRepository;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Prd5143Test {
  @Before
  public void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testReportAsHTMLInNormalMode() throws Exception {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Income Statement PRD-5-minimal.prpt" );
    report.getReportConfiguration()
      .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "false" );
    String pageableHTML = createPageableHTML( report, 0 );
    Assert.assertTrue( pageableHTML.contains( "From June 1 through June 30, 2005" ) );
  }

  @Test
  public void testReportAsHTMLInComplexMode() throws Exception {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Income Statement PRD-5-minimal.prpt" );
    report.getReportConfiguration()
      .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );
    String pageableHTML = createPageableHTML( report, 0 );
    Assert.assertTrue( pageableHTML.contains( "From June 1 through June 30, 2005" ) );
  }

  @Test
  public void testFullReportAsHTMLInNormalMode() throws Exception {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Income Statement PRD-5.prpt" );
    report.getReportConfiguration()
      .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "false" );
    String pageableHTML = createPageableHTML( report, 0 );
    Assert.assertTrue( pageableHTML.contains( "From June 1 through June 30, 2005" ) );
  }

  @Test
  public void testFullReportAsHTMLInComplexMode() throws Exception {
    MasterReport report = DebugReportRunner.parseGoldenSampleReport( "Income Statement PRD-5.prpt" );
    report.getReportConfiguration()
      .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );
    String pageableHTML = createPageableHTML( report, 0 );
    Assert.assertTrue( pageableHTML.contains( "From June 1 through June 30, 2005" ) );
  }


  public static String createPageableHTML( final MasterReport report, int page )
    throws Exception {
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

        final PageableHtmlOutputProcessor outputProcessor =
          new PageableHtmlOutputProcessor( report.getConfiguration() );
        outputProcessor.setFlowSelector( new SinglePageFlowSelector( page, true ) );

        final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
        printer.setContentWriter( root, new DefaultNameGenerator( root, "report.html" ) );
        printer.setDataWriter( data, new DefaultNameGenerator( data, "content" ) );
        printer.setUrlRewriter( new NullURLRewriter() );
        outputProcessor.setPrinter( printer );

        final PageableReportProcessor sp = new PageableReportProcessor( report, outputProcessor );
        sp.processReport();
        sp.close();
        zipRepository.close();
        return out.toString( "UTF-8" );
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

  public static class NullURLRewriter implements URLRewriter {
    public String rewrite( final ContentEntity sourceDocument, final ContentEntity dataEntity )
      throws URLRewriteException {
      return null;
    }
  }

}
