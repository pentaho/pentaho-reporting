/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import junit.framework.TestCase;

import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.engine.classic.core.layout.output.FlowSelector;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllItemsHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.PageableHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;

/**
 * @noinspection HardCodedStringLiteral
 */
public class Prd2324IT extends TestCase {
  public static class ReportPageSelector implements FlowSelector {
    private int acceptedPage;

    public ReportPageSelector( final int acceptedPage ) {
      this.acceptedPage = acceptedPage;
    }

    public boolean isLogicalPageAccepted( final LogicalPageKey key ) {
      if ( key == null ) {
        return false;
      }
      return key.getPosition() == acceptedPage;
    }
  }

  public Prd2324IT() {
  }

  public Prd2324IT( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testPageFooterMissing() throws IOException, ReportProcessingException {
    final MasterReport report = new MasterReport();
    report.getReportConfiguration().setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY,
        "false" );
    addLabel( "PageFooter", report.getPageFooter() );
    addLabel( "PageHeader", report.getPageHeader() );
    addLabel( "ReportHeader", report.getReportHeader() );
    addLabel( "ReportFooter", report.getReportFooter() );

    final byte[] bytes = generate( report );
    final String data = new String( bytes, "ISO-8859-1" );

    DebugLog.log( data );

    assertTrue( "Found PageFooter", data.contains( "PageFooter" ) );
    assertTrue( "Found PageHeader", data.contains( "PageHeader" ) );
    assertTrue( "Found ReportFooter", data.contains( "ReportFooter" ) );
    assertTrue( "Found ReportHeader", data.contains( "ReportHeader" ) );

  }

  public void testPageFooterMissingComplex() throws IOException, ReportProcessingException {
    final MasterReport report = new MasterReport();
    report.getReportConfiguration()
        .setConfigProperty( ClassicEngineCoreModule.COMPLEX_TEXT_CONFIG_OVERRIDE_KEY, "true" );
    addLabel( "PageFooter", report.getPageFooter() );
    addLabel( "PageHeader", report.getPageHeader() );
    addLabel( "ReportHeader", report.getReportHeader() );
    addLabel( "ReportFooter", report.getReportFooter() );

    final byte[] bytes = generate( report );
    final String data = new String( bytes, "ISO-8859-1" );

    DebugLog.log( data );

    assertTrue( "Found PageFooter", data.contains( "PageFooter" ) );
    assertTrue( "Found PageHeader", data.contains( "PageHeader" ) );
    assertTrue( "Found ReportFooter", data.contains( "ReportFooter" ) );
    assertTrue( "Found ReportHeader", data.contains( "ReportHeader" ) );

  }

  private void addLabel( final String text, final Band band ) {
    final Element e = new Element();
    e.setElementType( LabelType.INSTANCE );
    e.setAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.VALUE, text );
    e.getStyle().setStyleProperty( ElementStyleKeys.MIN_HEIGHT, new Float( 20 ) );
    e.getStyle().setStyleProperty( ElementStyleKeys.MIN_WIDTH, new Float( 100 ) );
    band.addElement( e );
  }

  public byte[] generate( final MasterReport report ) throws ReportProcessingException, IOException {
    final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    final StreamRepository targetRepository = new StreamRepository( null, outputStream, "report" ); //$NON-NLS-1$
    final ContentLocation targetRoot = targetRepository.getRoot();

    final PageableHtmlOutputProcessor outputProcessor = new PageableHtmlOutputProcessor( report.getConfiguration() );
    final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
    printer.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, "index", "html" ) ); //$NON-NLS-1$//$NON-NLS-2$
    outputProcessor.setPrinter( printer );

    outputProcessor.setFlowSelector( new ReportPageSelector( 0 ) );
    final PageableReportProcessor proc = new PageableReportProcessor( report, outputProcessor );
    proc.processReport();
    proc.close();

    outputStream.flush();
    outputStream.close();
    return outputStream.toByteArray();
  }
}
