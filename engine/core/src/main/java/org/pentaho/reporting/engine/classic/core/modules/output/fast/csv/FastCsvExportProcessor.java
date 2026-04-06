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

package org.pentaho.reporting.engine.classic.core.modules.output.fast.csv;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.EmptyReportException;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportProgressEvent;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractOutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.AbstractReportProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.output.LogicalPageKey;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.FastExportOutputFunction;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.CSVTableModule;
import org.pentaho.reporting.engine.classic.core.modules.output.table.csv.helper.CSVOutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.states.process.EndDetailsHandler;
import org.pentaho.reporting.engine.classic.core.states.process.ProcessState;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.CSVQuoter;
import org.pentaho.reporting.libraries.fonts.encoding.EncodingRegistry;

import javax.swing.table.TableModel;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

/**
 * A fast CSV export processor optimized for large datasets (millions of rows).
 * <p>
 * Uses a 3-phase hybrid approach:
 * <ol>
 *   <li><b>Phase 1 — Structural headers:</b> Lets the ProcessState state machine handle
 *       structural events (REPORT_INITIALIZED, REPORT_STARTED, GROUP_STARTED, ITEMS_STARTED)
 *       via normal {@code advance()/commit()}.</li>
 *   <li><b>Phase 2 — Bulk data:</b> Switches to direct {@code TableModel} iteration for the
 *       bulk data rows, applying format patterns (date/number) and column visibility extracted
 *       from the report's ItemBand elements.</li>
 *   <li><b>Phase 3 — Structural footers:</b> Resumes the state machine for closing events
 *       (ITEMS_FINISHED, GROUP_FINISHED, REPORT_FINISHED, REPORT_DONE).</li>
 * </ol>
 */
public class FastCsvExportProcessor extends AbstractReportProcessor {
  private static final Log logger = LogFactory.getLog( FastCsvExportProcessor.class );

  private static final int FLUSH_INTERVAL = 10000;
  private static final int PROGRESS_INTERVAL = 5000;
  private static final int WRITE_BUFFER_SIZE = 65536;
  private static final String LINE_SEPARATOR = "\r\n";

  private static class CSVDataOutputProcessor extends AbstractOutputProcessor {
    private final OutputProcessorMetaData metaData;

    private CSVDataOutputProcessor() {
      metaData = new CSVOutputProcessorMetaData( CSVOutputProcessorMetaData.PAGINATION_NONE ) {
        @Override
        public void initialize( final Configuration configuration ) {
          super.initialize( configuration );
          addFeature( OutputProcessorFeature.FAST_EXPORT );
        }
      };
    }

    @Override
    protected void processPageContent( final LogicalPageKey logicalPageKey, final LogicalPageBox logicalPage )
        throws ContentProcessingException {
      // CSV has no page content processing
    }

    @Override
    public OutputProcessorMetaData getMetaData() {
      return metaData;
    }
  }

  private final OutputStream outputStream;
  private final String encoding;

  public FastCsvExportProcessor( final MasterReport report, final OutputStream outputStream )
      throws ReportProcessingException {
    this( report, outputStream, null );
  }

  public FastCsvExportProcessor( final MasterReport report, final OutputStream outputStream, final String encoding )
      throws ReportProcessingException {
    super( report, new CSVDataOutputProcessor() );
    this.outputStream = outputStream;
    this.encoding = encoding;
  }

  @Override
  protected OutputFunction createLayoutManager() {
    return new FastExportOutputFunction( new FastCsvExportTemplate( new NullOutputStream(), encoding ) );
  }

  private static class NullOutputStream extends OutputStream {
    @Override
    public void write( int b ) {
      // discard
    }

    @Override
    public void write( byte[] b, int off, int len ) {
      // discard
    }
  }

  @Override
  public void processReport() throws ReportProcessingException {
    try {
      fireProcessingStarted( new ReportProgressEvent( this ) );

      final ProcessState startState = initializeReportState();
      final TableModel tableModel = extractTableModel( startState );

      final int rowCount = tableModel.getRowCount();
      final int columnCount = computeRealColumnCount( tableModel );

      // Phase 1: advance state machine through structural headers
      ProcessState state = advanceThroughHeaders( startState );
      if ( state.isFinish() ) {
        fireProcessingFinished( new ReportProgressEvent( this ) );
        return;
      }

      // Phase 2: write title + column headers + bulk data rows with formatting
      writeDataRows( tableModel, rowCount, columnCount );

      // Phase 3: resume state machine through structural footers
      advanceThroughFooters( state );

      fireProcessingFinished( new ReportProgressEvent( this ) );
    } catch ( ReportProcessingException re ) {
      throw re;
    } catch ( IOException ioe ) {
      throw new ReportProcessingException( "Failed to write CSV output", ioe );
    } catch ( Exception e ) {
      throw new ReportProcessingException( "Failed to process the report", e );
    }
  }

  private ProcessState initializeReportState() throws ReportProcessingException {
    final DefaultProcessingContext processingContext = createProcessingContext();
    final MasterReport report = getReport();
    final OutputFunction lm = createLayoutManager();

    final ProcessState startState = new ProcessState();
    try {
      boolean isQueryLimitReached = startState.initializeForMasterReport(
          report, processingContext, (OutputFunction) lm.getInstance() );
      setQueryLimitReached( isQueryLimitReached );
    } finally {
      setProcessStateHandle( startState.getProcessHandle() );
    }
    return startState;
  }

  private TableModel extractTableModel( final ProcessState startState ) throws EmptyReportException {
    final TableModel tableModel = startState.getFlowController().getMasterRow().getReportData();
    if ( tableModel == null ) {
      throw new EmptyReportException( "Report query returned no data." );
    }
    if ( tableModel.getRowCount() == 0 || tableModel.getColumnCount() == 0 ) {
      throw new EmptyReportException( "Report did not generate any content." );
    }
    return tableModel;
  }

  private int computeRealColumnCount( final TableModel tableModel ) {
    final int totalColumns = tableModel.getColumnCount();
    int realCount = 0;
    for ( int col = 0; col < totalColumns; col++ ) {
      final String name = tableModel.getColumnName( col );
      if ( name == null || !name.startsWith( ClassicEngineBoot.INDEX_COLUMN_PREFIX ) ) {
        realCount++;
      }
    }
    return realCount;
  }

  private ProcessState advanceThroughHeaders( ProcessState state ) throws ReportProcessingException {
    while ( !state.isFinish() ) {
      checkInterrupted();
      if ( state.getAdvanceHandler().getEventCode() == ReportEvent.ITEMS_ADVANCED ) {
        break;
      }
      state = state.advance();
      state = state.commit();
    }
    return state;
  }

  /**
   * Phase 2: Writes all data rows directly from the TableModel with formatting applied.
   * <p>
   * Format patterns (date/number) and column visibility are extracted from the report's
   * ItemBand elements so that the CSV output matches the report definition.
   */
  private void writeDataRows( final TableModel tableModel, final int rowCount, final int columnCount )
      throws IOException {
    final CsvWriteConfig csvConfig = createCsvWriteConfig();
    final StringBuilder line = new StringBuilder( columnCount * 20 );
    final ReportProgressEvent progressEvent = new ReportProgressEvent( this );
    final String reportTitle = getReport().getTitle();
    final OutputStream currentOut = wrapOutputStream( outputStream );

    // Build formatters from the ItemBand to apply date/number patterns and visibility.
    // If ItemBand traversal fails (e.g., "No computed style" for nested elements),
    // fall back to null formatters — data will use String.valueOf() but output won't be empty.
    ColumnFormatter[] formatters = null;
    try {
      formatters = buildColumnFormatters( tableModel, columnCount );
    } catch ( Exception e ) {
      logger.warn( "Could not extract format patterns from ItemBand, falling back to raw values", e );
    }

    writeReportTitle( line, reportTitle, csvConfig, currentOut );
    writeColumnHeaders( line, tableModel, columnCount, csvConfig, currentOut, formatters );

    for ( int row = 0; row < rowCount; row++ ) {
      writeSingleRow( line, tableModel, row, columnCount, csvConfig, currentOut, formatters );

      if ( ( row + 1 ) % FLUSH_INTERVAL == 0 ) {
        currentOut.flush();
      }
      fireProgressIfNeeded( progressEvent, row, rowCount );
    }
    currentOut.flush();
  }

  private void advanceThroughFooters( ProcessState state ) throws ReportProcessingException {
    state.setAdvanceHandler( EndDetailsHandler.HANDLER );
    state.setInItemGroup( false );

    while ( !state.isFinish() ) {
      checkInterrupted();
      state = state.advance();
      state = state.commit();
    }
  }

  // =========================================================================
  // CSV row rendering with formatting
  // =========================================================================

  private void writeColumnHeaders( final StringBuilder line, final TableModel tableModel,
                                   final int columnCount, final CsvWriteConfig csvConfig,
                                   final OutputStream out,
                                   final ColumnFormatter[] formatters ) throws IOException {
    line.setLength( 0 );
    boolean firstCol = true;
    for ( int col = 0; col < columnCount; col++ ) {
      if ( isHiddenColumn( formatters, col ) ) {
        continue;
      }
      if ( !firstCol ) {
        line.append( csvConfig.separatorChar );
      }
      firstCol = false;
      final String columnName = tableModel.getColumnName( col );
      if ( columnName != null ) {
        line.append( csvConfig.quoter.doQuoting( columnName ) );
      }
    }
    line.append( LINE_SEPARATOR );
    out.write( line.toString().getBytes( csvConfig.encoding ) );
  }

  private void writeReportTitle( final StringBuilder line, final String title,
                                 final CsvWriteConfig csvConfig, final OutputStream out ) throws IOException {
    if ( title == null || title.trim().isEmpty() ) {
      return;
    }
    line.setLength( 0 );
    line.append( csvConfig.quoter.doQuoting( title ) );
    line.append( LINE_SEPARATOR );
    out.write( line.toString().getBytes( csvConfig.encoding ) );
  }

  /**
   * Writes a single CSV row applying format patterns from the report's ItemBand.
   * <ul>
   *   <li>Date values → formatted via {@code SimpleDateFormat} with the element's format-string</li>
   *   <li>Number values → formatted via {@code DecimalFormat} with the element's format-string</li>
   *   <li>Hidden columns (visible=false) → skipped</li>
   *   <li>All other values → {@code String.valueOf()}</li>
   * </ul>
   */
  private void writeSingleRow( final StringBuilder line, final TableModel tableModel,
                               final int row, final int columnCount,
                               final CsvWriteConfig csvConfig, final OutputStream out,
                               final ColumnFormatter[] formatters ) throws IOException {
    line.setLength( 0 );
    boolean firstCol = true;
    for ( int col = 0; col < columnCount; col++ ) {
      if ( isHiddenColumn( formatters, col ) ) {
        continue;
      }
      if ( !firstCol ) {
        line.append( csvConfig.separatorChar );
      }
      firstCol = false;
      final Object value = tableModel.getValueAt( row, col );
      if ( value != null ) {
        line.append( csvConfig.quoter.doQuoting( formatValue( value, col, formatters ) ) );
      }
    }
    line.append( LINE_SEPARATOR );
    out.write( line.toString().getBytes( csvConfig.encoding ) );
  }

  private boolean isHiddenColumn( final ColumnFormatter[] formatters, final int col ) {
    return formatters != null && col < formatters.length
        && formatters[col] != null && !formatters[col].visible;
  }

  private String formatValue( final Object value, final int col, final ColumnFormatter[] formatters ) {
    if ( formatters == null || col >= formatters.length || formatters[col] == null ) {
      return String.valueOf( value );
    }
    final ColumnFormatter fmt = formatters[col];
    if ( fmt.dateFormat != null && value instanceof Date dateValue ) {
      return fmt.dateFormat.format( dateValue );
    }
    if ( fmt.numberFormat != null && value instanceof Number ) {
      return fmt.numberFormat.format( value );
    }
    return String.valueOf( value );
  }

  // =========================================================================
  // Column formatting extraction from the ItemBand
  // =========================================================================

  private static final class ColumnFormatter {
    final DateFormat dateFormat;
    final DecimalFormat numberFormat;
    final boolean visible;

    ColumnFormatter( final DateFormat dateFormat, final DecimalFormat numberFormat, final boolean visible ) {
      this.dateFormat = dateFormat;
      this.numberFormat = numberFormat;
      this.visible = visible;
    }
  }

  /**
   * Walks the report's ItemBand to extract format patterns for each column.
   * For each child element that has a {@code field} attribute matching a TableModel column name,
   * reads the element type, format-string, and visibility, then creates an appropriate formatter.
   */
  private ColumnFormatter[] buildColumnFormatters( final TableModel tableModel, final int columnCount ) {
    final ItemBand itemBand = getReport().getItemBand();
    if ( itemBand == null ) {
      return new ColumnFormatter[0];
    }

    final Map<String, Integer> nameToIndex = new HashMap<>();
    for ( int col = 0; col < columnCount; col++ ) {
      final String name = tableModel.getColumnName( col );
      if ( name != null ) {
        nameToIndex.put( name, col );
      }
    }

    Locale locale = null;
    TimeZone timeZone = null;
    try {
      locale = getReport().getResourceBundleFactory().getLocale();
      timeZone = getReport().getResourceBundleFactory().getTimeZone();
    } catch ( Exception e ) {
      logger.debug( "Could not obtain locale/timezone from ResourceBundleFactory", e );
    }
    if ( locale == null ) {
      locale = Locale.getDefault();
    }
    if ( timeZone == null ) {
      timeZone = TimeZone.getDefault();
    }
    final ColumnFormatter[] formatters = new ColumnFormatter[columnCount];

    try {
      collectFormatters( itemBand, nameToIndex, formatters, locale, timeZone );
    } catch ( Exception e ) {
      logger.warn( "Error while extracting formatters from ItemBand elements", e );
    }
    return formatters;
  }

  private void collectFormatters( final Section section,
                                  final Map<String, Integer> nameToIndex,
                                  final ColumnFormatter[] formatters,
                                  final Locale locale,
                                  final TimeZone timeZone ) {
    final int count = section.getElementCount();
    for ( int i = 0; i < count; i++ ) {
      try {
        final ReportElement element = section.getElement( i );
        if ( element instanceof Section sectionValue) {
          collectFormatters( sectionValue, nameToIndex, formatters, locale, timeZone );
        } else {
          extractFormatter( element, nameToIndex, formatters, locale, timeZone );
        }
      } catch ( Exception e ) {
        logger.debug( "Skipping element at index " + i + " due to error: " + e.getMessage() );
      }
    }
  }

  private void extractFormatter( final ReportElement element,
                                 final Map<String, Integer> nameToIndex,
                                 final ColumnFormatter[] formatters,
                                 final Locale locale,
                                 final TimeZone timeZone ) {
    final Object fieldAttr = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FIELD );
    if ( fieldAttr == null ) {
      return;
    }
    final Integer colIndex = nameToIndex.get( String.valueOf( fieldAttr ) );
    if ( colIndex == null || colIndex >= formatters.length ) {
      return;
    }

    // Visibility is inherited in the computed-style tree and is NOT available via getStyle()
    // (which only returns explicitly declared properties). Default to visible=true.
    // Only hide a column if the element explicitly declares visible=false.
    boolean visible = true;
    try {
      if ( element.getStyle() != null ) {
        final Object visibleProp = element.getStyle().getStyleProperty( ElementStyleKeys.VISIBLE, null );
        if ( visibleProp instanceof Boolean visibleBool) {
          visible = visibleBool;
        }
      }
    } catch ( Exception e ) {
      // If style access fails (e.g., "No computed style"), default to visible
      logger.debug( "Could not read visibility for element, defaulting to visible", e );
    }

    final Object fmtRaw = element.getAttribute( AttributeNames.Core.NAMESPACE, AttributeNames.Core.FORMAT_STRING );
    final String fmtStr = ( fmtRaw != null && !"".equals( fmtRaw ) ) ? String.valueOf( fmtRaw ) : null;

    final String typeName = ( element.getElementType() != null && element.getElementType().getMetaData() != null )
        ? element.getElementType().getMetaData().getName() : "";

    formatters[colIndex] = createColumnFormatter( typeName, fmtStr, visible, locale, timeZone );
  }

  private ColumnFormatter createColumnFormatter( final String typeName, final String formatString,
                                                 final boolean visible, final Locale locale,
                                                 final TimeZone timeZone ) {
    DateFormat dateFormat = null;
    DecimalFormat numberFormat = null;

    if ( "date-field".equals( typeName ) && formatString != null ) {
      dateFormat = buildDateFormat( formatString, locale, timeZone );
    } else if ( "number-field".equals( typeName ) && formatString != null ) {
      numberFormat = buildNumberFormat( formatString, locale );
    }
    return new ColumnFormatter( dateFormat, numberFormat, visible );
  }

  private DateFormat buildDateFormat( final String pattern, final Locale locale, final TimeZone timeZone ) {
    try {
      SimpleDateFormat sdf = new SimpleDateFormat( pattern, locale );
      if ( timeZone != null ) {
        sdf.setTimeZone( timeZone );
      }
      return sdf;
    } catch ( IllegalArgumentException e ) {
      logger.debug( "Invalid date format pattern: " + pattern, e );
      return null;
    }
  }

  private DecimalFormat buildNumberFormat( final String pattern, final Locale locale ) {
    try {
      return new DecimalFormat( pattern, new DecimalFormatSymbols( locale ) );
    } catch ( IllegalArgumentException e ) {
      logger.debug( "Invalid number format pattern: " + pattern, e );
      return null;
    }
  }

  // =========================================================================
  // Progress & configuration helpers
  // =========================================================================

  private void fireProgressIfNeeded( final ReportProgressEvent progressEvent,
                                     final int row, final int rowCount ) {
    if ( ( row + 1 ) % PROGRESS_INTERVAL == 0 ) {
      progressEvent.reuse( ReportProgressEvent.GENERATING_CONTENT, row, rowCount, 0, 0, 1 );
      fireStateUpdate( progressEvent );

      if ( logger.isDebugEnabled() && ( row + 1 ) % 100000 == 0 ) {
        logger.debug( "FastCsvExportProcessor: Phase 2 - Processed "
            + ( row + 1 ) + " / " + rowCount + " rows" );
      }
    }
  }

  private CsvWriteConfig createCsvWriteConfig() {
    final Configuration config = getReport().getConfiguration();
    final String separator = config.getConfigProperty(
        CSVTableModule.SEPARATOR, CSVTableModule.SEPARATOR_DEFAULT );
    if ( separator == null || separator.isEmpty() ) {
      throw new IllegalStateException( "CSV separator must not be null or empty" );
    }
    final char separatorChar = separator.charAt( 0 );
    final String enclosure = config.getConfigProperty(
        CSVTableModule.ENCLOSURE_CHAR, CSVTableModule.ENCLOSURE_CHAR_DEFAULT );
    final char enclosureChar = enclosure.charAt( 0 );
    final String forceEnclosureProp = config.getConfigProperty(
        CSVTableModule.FORCE_ENCLOSURE, CSVTableModule.FORCE_ENCLOSURE_DEFAULT );
    final boolean forceEnclosure = Boolean.parseBoolean( forceEnclosureProp );
    final CSVQuoter quoter = new CSVQuoter( separatorChar, enclosureChar, forceEnclosure );

    String csvEncoding = this.encoding;
    if ( csvEncoding == null ) {
      csvEncoding = config.getConfigProperty(
          "org.pentaho.reporting.engine.classic.core.modules.output.table.csv.Encoding",
          EncodingRegistry.getPlatformDefaultEncoding() );
    }
    return new CsvWriteConfig( separatorChar, quoter, csvEncoding );
  }

  private static final class CsvWriteConfig {
    final char separatorChar;
    final CSVQuoter quoter;
    final String encoding;

    CsvWriteConfig( final char separatorChar, final CSVQuoter quoter, final String encoding ) {
      this.separatorChar = separatorChar;
      this.quoter = quoter;
      this.encoding = encoding;
    }
  }

  private OutputStream wrapOutputStream( final OutputStream out ) {
    if ( out instanceof BufferedOutputStream ) {
      return out;
    }
    return new BufferedOutputStream( out, WRITE_BUFFER_SIZE );
  }
}

