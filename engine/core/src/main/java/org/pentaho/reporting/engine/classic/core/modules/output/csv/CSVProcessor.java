/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.output.csv;

import org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.GenericOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.libraries.base.config.Configuration;

import java.io.Writer;

/**
 * The <code>CSVProcessor</code> coordinates the writing process for the raw CSV output.
 * <p/>
 * A {@link CSVWriter} is added to the private copy of the report to handle the output process.
 *
 * @author Thomas Morgner
 */
public class CSVProcessor extends AbstractReportProcessor {
  private static class CSVDataOutputProcessor extends AbstractOutputProcessor {
    private OutputProcessorMetaData metaData;

    private CSVDataOutputProcessor() {
      metaData = new GenericOutputProcessorMetaData( EXPORT_DESCRIPTOR );
    }

    protected void processPageContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage )
      throws ContentProcessingException {

    }

    public OutputProcessorMetaData getMetaData() {
      return metaData;
    }
  }

  protected static final int MAX_EVENTS_PER_RUN = 200;
  protected static final int MIN_ROWS_PER_EVENT = 200;

  public static final String CSV_SEPARATOR = "org.pentaho.reporting.engine.classic.core.modules.output.csv.Separator";

  public static final String CSV_ENCODING = "org.pentaho.reporting.engine.classic.core.modules.output.csv.Encoding";
  public static final String CSV_DATAROWNAME =
      "org.pentaho.reporting.engine.classic.core.modules.output.csv.WriteDatarowNames";

  public static final String CSV_WRITE_STATECOLUMNS =
      "org.pentaho.reporting.engine.classic.core.modules.output.csv.WriteStateColumns";
  public static final String CSV_ENABLE_REPORTHEADER =
      "org.pentaho.reporting.engine.classic.core.modules.output.csv.EnableReportHeader";
  public static final String CSV_ENABLE_REPORTFOOTER =
      "org.pentaho.reporting.engine.classic.core.modules.output.csv.EnableReportFooter";
  public static final String CSV_ENABLE_GROUPHEADERS =
      "org.pentaho.reporting.engine.classic.core.modules.output.csv.EnableGroupHeaders";
  public static final String CSV_ENABLE_GROUPFOOTERS =
      "org.pentaho.reporting.engine.classic.core.modules.output.csv.EnableGroupFooters";
  public static final String CSV_ENABLE_ITEMBANDS =
      "org.pentaho.reporting.engine.classic.core.modules.output.csv.EnableItembands";

  /**
   * The character stream writer to be used by the {@link CSVWriter} function.
   */
  private Writer writer;

  private static final String EXPORT_DESCRIPTOR = "data/csv";
  private String separator;
  private boolean writeDataRowNames;

  /**
   * Creates a new <code>CSVProcessor</code>. The processor will use a comma (",") to separate the column values, unless
   * defined otherwise in the report configuration. The processor creates a private copy of the clone, so that no change
   * to the original report will influence the report processing. DataRow names are not written.
   *
   * @param report
   *          the report to be processed.
   * @throws ReportProcessingException
   *           if the report initialisation failed.
   */
  public CSVProcessor( final MasterReport report ) throws ReportProcessingException {
    this( report, report.getReportConfiguration().getConfigProperty( CSVProcessor.CSV_SEPARATOR, "," ) );
  }

  /**
   * Creates a new CSVProcessor. The processor will use the specified separator, the report configuration is not queried
   * for a separator. The processor creates a private copy of the clone, so that no change to the original report will
   * influence the report processing. DataRowNames are not written.
   *
   * @param report
   *          the report to be processed.
   * @param separator
   *          the separator string to mark column boundaries.
   * @throws ReportProcessingException
   *           if the report initialisation failed.
   */
  public CSVProcessor( final MasterReport report, final String separator ) throws ReportProcessingException {
    this( report, separator, CSVProcessor.queryBoolConfig( report.getReportConfiguration(),
        CSVProcessor.CSV_DATAROWNAME ) );
  }

  /**
   * Creates a new CSVProcessor. The processor will use the specified separator, the report configuration is not queried
   * for a separator. The processor creates a private copy of the clone, so that no change to the original report will
   * influence the report processing. The first row will contain the datarow names.
   *
   * @param report
   *          the report to be processed.
   * @param separator
   *          the separator string to mark column boundaries.
   * @param writeDataRowNames
   *          controls whether or not the data row names are output.
   * @throws ReportProcessingException
   *           if the report initialisation failed.
   */
  public CSVProcessor( final MasterReport report, final String separator, final boolean writeDataRowNames )
    throws ReportProcessingException {
    super( report, new CSVDataOutputProcessor() );
    this.separator = separator;
    this.writeDataRowNames = writeDataRowNames;
  }

  private static boolean queryBoolConfig( final Configuration config, final String name ) {
    return "true".equals( config.getConfigProperty( name, "false" ) );
  }

  /**
   * Returns the writer used in this Processor.
   *
   * @return the writer
   */
  public Writer getWriter() {
    return writer;
  }

  /**
   * Defines the writer which should be used to write the contents of the report.
   *
   * @param writer
   *          the writer.
   */
  public void setWriter( final Writer writer ) {
    this.writer = writer;
  }

  protected OutputFunction createLayoutManager() {
    final CSVWriter lm = new CSVWriter();
    lm.setSeparator( separator );
    lm.setWriteDataRowNames( writeDataRowNames );
    lm.setWriter( getWriter() );

    final Configuration config = getReport().getReportConfiguration();
    lm.setWriteStateColumns( CSVProcessor.queryBoolConfig( config, CSVProcessor.CSV_WRITE_STATECOLUMNS ) );
    lm.setEnableReportHeader( CSVProcessor.queryBoolConfig( config, CSVProcessor.CSV_ENABLE_REPORTHEADER ) );
    lm.setEnableReportFooter( CSVProcessor.queryBoolConfig( config, CSVProcessor.CSV_ENABLE_REPORTFOOTER ) );
    lm.setEnableGroupHeader( CSVProcessor.queryBoolConfig( config, CSVProcessor.CSV_ENABLE_GROUPHEADERS ) );
    lm.setEnableGroupFooter( CSVProcessor.queryBoolConfig( config, CSVProcessor.CSV_ENABLE_GROUPFOOTERS ) );
    lm.setEnableItemband( CSVProcessor.queryBoolConfig( config, CSVProcessor.CSV_ENABLE_ITEMBANDS ) );

    return lm;
  }

  /**
   * Checks whether report processing should be aborted when an exception occurs.
   *
   * @param config
   *          the configuration.
   * @return if strict error handling is enabled.
   * @deprecated No longer needed.
   */
  protected static boolean isStrictErrorHandling( final Configuration config ) {
    final String strictError = config.getConfigProperty( ClassicEngineCoreModule.STRICT_ERROR_HANDLING_KEY );
    return "true".equals( strictError );
  }
}
