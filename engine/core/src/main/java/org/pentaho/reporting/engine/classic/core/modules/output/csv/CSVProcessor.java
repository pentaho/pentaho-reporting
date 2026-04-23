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
  public static final String CSV_SEPARATOR_DEFAULT = ",";

  public static final String CSV_ENCODING = "org.pentaho.reporting.engine.classic.core.modules.output.csv.Encoding";
  public static final String CSV_DATAROWNAME =
      "org.pentaho.reporting.engine.classic.core.modules.output.csv.WriteDatarowNames";
  public static final String CSV_ENCLOSURE_CHAR =
      "org.pentaho.reporting.engine.classic.core.modules.output.csv.Enclosure";
  public static final String CSV_ENCLOSURE_CHAR_DEFAULT = "\"";

  public static final String CSV_ENCLOSURE_FORCED =
      "org.pentaho.reporting.engine.classic.core.modules.output.csv.ForceEnclosure";

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
  private final String separator;
  private final boolean writeDataRowNames;
  private final char enclosure;

  /**
   * Creates a new <code>CSVProcessor</code>. Separator, enclosure and writeDataRowNames are read from the report
   * configuration. DataRow names are not written by default.
   *
   * @param report
   *          the report to be processed.
   * @throws ReportProcessingException
   *           if the report initialisation failed.
   */
  public CSVProcessor( final MasterReport report ) throws ReportProcessingException {
    this( report,
        readSeparator( report.getReportConfiguration() ),
        readEnclosureChar( report.getReportConfiguration() ) );
  }

  /**
   * Creates a new CSVProcessor with explicit separator and enclosure. The report configuration is not queried for
   * either. DataRowNames are not written.
   *
   * @param report
   *          the report to be processed.
   * @param separator
   *          the separator string to mark column boundaries.
   * @param enclosure
   *          the enclosure character to wrap field values.
   * @throws ReportProcessingException
   *           if the report initialisation failed.
   */
  public CSVProcessor( final MasterReport report, final String separator, final char enclosure )
    throws ReportProcessingException {
    this( report, separator, enclosure,
        CSVProcessor.queryBoolConfig( report.getReportConfiguration(), CSVProcessor.CSV_DATAROWNAME ) );
  }

  /**
   * Creates a new CSVProcessor with explicit separator, enclosure and writeDataRowNames flag.
   *
   * @param report
   *          the report to be processed.
   * @param separator
   *          the separator string to mark column boundaries.
   * @param enclosure
   *          the enclosure character to wrap field values.
   * @param writeDataRowNames
   *          controls whether or not the data row names are output.
   * @throws ReportProcessingException
   *           if the report initialisation failed.
   */
  public CSVProcessor( final MasterReport report, final String separator, final char enclosure,
      final boolean writeDataRowNames )
    throws ReportProcessingException {
    super( report, new CSVDataOutputProcessor() );
    this.separator = separator;
    this.enclosure = enclosure;
    this.writeDataRowNames = writeDataRowNames;
  }

  private static char readEnclosureChar( final Configuration config ) {
    final String enclosureStr = config.getConfigProperty( CSV_ENCLOSURE_CHAR, CSV_ENCLOSURE_CHAR_DEFAULT );
    if ( enclosureStr.length() != 1 ) {
      throw new IllegalArgumentException( "CSV enclosure must be a single character." );
    }
    return enclosureStr.charAt( 0 );
  }

  private static String readSeparator( final Configuration config ) {
    final String separator = config.getConfigProperty( CSV_SEPARATOR, CSV_SEPARATOR_DEFAULT );
    if ( separator.isEmpty() ) {
      throw new IllegalArgumentException( "CSV separator cannot be an empty string." );
    }
    return separator;
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
    lm.setEnclosure( enclosure );
    lm.setAlwaysDoQuotes( CSVProcessor.queryBoolConfig( config, CSVProcessor.CSV_ENCLOSURE_FORCED ) );
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
