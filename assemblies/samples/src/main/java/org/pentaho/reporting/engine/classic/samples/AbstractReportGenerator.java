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
* Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.samples;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.base.PageableReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.pdf.PdfOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.FlowReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.StreamReportProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.AllItemsHtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.FileSystemURLRewriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.HtmlPrinter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.StreamHtmlOutputProcessor;
import org.pentaho.reporting.engine.classic.core.modules.output.table.xls.FlowExcelOutputProcessor;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.DefaultNameGenerator;
import org.pentaho.reporting.libraries.repository.stream.StreamRepository;

/**
 * This is the base class used with the report generation examples. It contains the actual <code>embedding</code>
 * of the reporting engine and report generation. All example embedded implementations will need to extend this class
 * and perform the following:
 * <ol>
 * <li>Implement the <code>getReportDefinition()</code> method and return the report definition (how the report
 * definition is generated is up to the implementing class).
 * <li>Implement the <code>getTableDataFactory()</code> method and return the data factory to be used (how
 * this is created is up to the implementing class).
 * <li>Implement the <code>getReportParameters()</code> method and return the set of report parameters to be used.
 * If no report parameters are required, then this method can simply return <code>null</code>
 * </ol>
 */
public abstract class AbstractReportGenerator {
  /**
   * The supported output types for this sample
   */
  public static enum OutputType {
    PDF, EXCEL, HTML
  }

  /**
   * Performs the basic initialization required to generate a report
   */
  public AbstractReportGenerator() {
    // Initialize the reporting engine
    ClassicEngineBoot.getInstance().start();
  }

  /**
   * Returns the report definition used by this report generator. If this method returns <code>null</code>,
   * the report generation process will throw a <code>NullPointerException</code>.
   *
   * @return the report definition used by thus report generator
   */
  public abstract MasterReport getReportDefinition();

  /**
   * Returns the data factory used by this report generator. If this method returns <code>null</code>,
   * the report generation process will use the data factory used in the report definition.
   *
   * @return the data factory used by this report generator
   */
  public abstract DataFactory getDataFactory();

  /**
   * Returns the set of parameters that will be passed to the report generation process. If there are no parameters
   * required for report generation, this method may return either an empty or a <code>null</code> <code>Map</code>
   *
   * @return the set of report parameters to be used by the report generation process, or <code>null</code> if no
   *         parameters are required.
   */
  public abstract Map<String, Object> getReportParameters();

  /**
   * Generates the report in the specified <code>outputType</code> and writes it into the specified
   * <code>outputFile</code>.
   *
   * @param outputType the output type of the report (HTML, PDF, HTML)
   * @param outputFile the file into which the report will be written
   * @throws IllegalArgumentException  indicates the required parameters were not provided
   * @throws IOException               indicates an error opening the file for writing
   * @throws ReportProcessingException indicates an error generating the report
   */
  public void generateReport( final OutputType outputType, File outputFile )
      throws IllegalArgumentException, IOException, ReportProcessingException {
    if ( outputFile == null ) {
      throw new IllegalArgumentException( "The output file was not specified" );
    }

    OutputStream outputStream = null;
    try {
      // Open the output stream
      outputStream = new BufferedOutputStream( new FileOutputStream( outputFile ) );

      // Generate the report to this output stream
      generateReport( outputType, outputStream );
    } finally {
      if ( outputStream != null ) {
        outputStream.close();
      }
    }
  }

  /**
   * Generates the report in the specified <code>outputType</code> and writes it into the specified
   * <code>outputStream</code>.
   * <p/>
   * It is the responsibility of the caller to close the <code>outputStream</code>
   * after this method is executed.
   *
   * @param outputType   the output type of the report (HTML, PDF, HTML)
   * @param outputStream the stream into which the report will be written
   * @throws IllegalArgumentException  indicates the required parameters were not provided
   * @throws ReportProcessingException indicates an error generating the report
   */
  public void generateReport( final OutputType outputType, OutputStream outputStream )
      throws IllegalArgumentException, ReportProcessingException {
    if ( outputStream == null ) {
      throw new IllegalArgumentException( "The output stream was not specified" );
    }

    // Get the report and data factory
    final MasterReport report = getReportDefinition();
    final DataFactory dataFactory = getDataFactory();

    // Set the data factory for the report
    if ( dataFactory != null ) {
      report.setDataFactory( dataFactory );
    }

    // Add any parameters to the report
    final Map<String, Object> reportParameters = getReportParameters();
    if ( null != reportParameters ) {
      for ( String key : reportParameters.keySet() ) {
        report.getParameterValues().put( key, reportParameters.get( key ) );
      }
    }

    // Prepare to generate the report
    AbstractReportProcessor reportProcessor = null;
    try {
      // Greate the report processor for the specified output type
      switch ( outputType ) {
        case PDF:
        {
          final PdfOutputProcessor outputProcessor =
              new PdfOutputProcessor( report.getConfiguration(), outputStream, report.getResourceManager() );
          reportProcessor = new PageableReportProcessor( report, outputProcessor );
          break;
        }

        case EXCEL:
        {
          final FlowExcelOutputProcessor target =
              new FlowExcelOutputProcessor( report.getConfiguration(), outputStream, report.getResourceManager() );
          reportProcessor = new FlowReportProcessor( report, target );
          break;
        }

        case HTML:
        {
          final StreamRepository targetRepository = new StreamRepository( outputStream );
          final ContentLocation targetRoot = targetRepository.getRoot();
          final HtmlOutputProcessor outputProcessor = new StreamHtmlOutputProcessor( report.getConfiguration() );
          final HtmlPrinter printer = new AllItemsHtmlPrinter( report.getResourceManager() );
          printer.setContentWriter( targetRoot, new DefaultNameGenerator( targetRoot, "index", "html" ) );
          printer.setDataWriter( null, null );
          printer.setUrlRewriter( new FileSystemURLRewriter() );
          outputProcessor.setPrinter( printer );
          reportProcessor = new StreamReportProcessor( report, outputProcessor );
          break;
        }
      }

      // Generate the report
      reportProcessor.processReport();
    } finally {
      if ( reportProcessor != null ) {
        reportProcessor.close();
      }
    }
  }
}
