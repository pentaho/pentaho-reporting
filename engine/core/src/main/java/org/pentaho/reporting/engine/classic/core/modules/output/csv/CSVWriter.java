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

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.FunctionProcessingException;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.process.SubReportProcessType;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * The CSV Writer is the content creation function used to create the CSV content. This implementation does no
 * layouting, the DataRow's raw data is written to the supplied writer.
 *
 * @author Thomas Morgner.
 * @noinspection HardCodedStringLiteral
 * @deprecated Will be removed in the future, as PDI is a better CSV generator.
 */
public class CSVWriter extends AbstractFunction implements OutputFunction {
  /**
   * The CSVRow is used to collect the data of a single row of data.
   */
  private static class CSVRow {
    /**
     * The data.
     */
    private final ArrayList<Object> data;

    /**
     * A quoter utility object.
     */
    private final CSVQuoter quoter;

    /**
     * The line separator.
     */
    private final String lineSeparator;

    /**
     * Creates a new CSVQuoter. The Quoter uses the system's default line separator.
     *
     * @param quoter
     *          a utility class for quoting CSV strings.
     */
    protected CSVRow( final CSVQuoter quoter ) {
      data = new ArrayList<Object>();
      this.quoter = quoter;
      lineSeparator = ClassicEngineBoot.getInstance().getGlobalConfig().getConfigProperty( "line.separator", "\n" );
    }

    /**
     * appends the given integer value as java.lang.Integer to this row.
     *
     * @param value
     *          the appended int value
     */
    public void append( final int value ) {
      data.add( value );
    }

    /**
     * appends the given Object to this row.
     *
     * @param o
     *          the appended value
     */
    public void append( final Object o ) {
      data.add( o );
    }

    /**
     * Writes the contents of the collected row, using the defined separator.
     *
     * @param w
     *          the writer.
     * @throws IOException
     *           if an I/O error occurred.
     */
    public void write( final Writer w ) throws IOException {
      final Iterator it = data.iterator();
      while ( it.hasNext() ) {
        w.write( quoter.doQuoting( String.valueOf( it.next() ) ) );
        if ( it.hasNext() ) {
          w.write( quoter.getSeparator() );
        }
      }
      w.write( lineSeparator );
    }
  }

  /**
   * the writer used to output the generated data.
   */
  private Writer w;

  /**
   * the functions dependency level, -1 by default.
   */
  private int depLevel;

  /**
   * the CSVQuoter used to encode the column values.
   */
  private final CSVQuoter quoter;

  /**
   * a flag indicating whether to writer data row names as column header.
   */
  private boolean writeDataRowNames;
  private boolean writeStateColumns;
  private boolean enableReportHeader;
  private boolean enableReportFooter;
  private boolean enableGroupHeader;
  private boolean enableGroupFooter;
  private boolean enableItemband;

  private ArrayList<InlineSubreportMarker> inlineSubreports;

  /**
   * DefaulConstructor. Creates a CSVWriter with a dependency level of -1 and a default CSVQuoter.
   */
  public CSVWriter() {
    setDependencyLevel( LayoutProcess.LEVEL_PAGINATE );
    quoter = new CSVQuoter();
    this.inlineSubreports = new ArrayList<InlineSubreportMarker>();
  }

  /**
   * Returns whether to print dataRow column names as header.
   *
   * @return true, if column names are printed, false otherwise.
   */
  public boolean isWriteDataRowNames() {
    return writeDataRowNames;
  }

  /**
   * Defines, whether to print column names in the first row.
   *
   * @param writeDataRowNames
   *          true, if column names are printed, false otherwise
   */
  public void setWriteDataRowNames( final boolean writeDataRowNames ) {
    this.writeDataRowNames = writeDataRowNames;
  }

  public boolean isWriteStateColumns() {
    return writeStateColumns;
  }

  public void setWriteStateColumns( final boolean writeStateColumns ) {
    this.writeStateColumns = writeStateColumns;
  }

  public boolean isEnableGroupFooter() {
    return enableGroupFooter;
  }

  public void setEnableGroupFooter( final boolean enableGroupFooter ) {
    this.enableGroupFooter = enableGroupFooter;
  }

  public boolean isEnableGroupHeader() {
    return enableGroupHeader;
  }

  public void setEnableGroupHeader( final boolean enableGroupHeader ) {
    this.enableGroupHeader = enableGroupHeader;
  }

  public boolean isEnableItemband() {
    return enableItemband;
  }

  public void setEnableItemband( final boolean enableItemband ) {
    this.enableItemband = enableItemband;
  }

  public boolean isEnableReportFooter() {
    return enableReportFooter;
  }

  public void setEnableReportFooter( final boolean enableReportFooter ) {
    this.enableReportFooter = enableReportFooter;
  }

  public boolean isEnableReportHeader() {
    return enableReportHeader;
  }

  public void setEnableReportHeader( final boolean enableReportHeader ) {
    this.enableReportHeader = enableReportHeader;
  }

  /**
   * Returns the writer used to output the generated data.
   *
   * @return the writer
   */
  public Writer getWriter() {
    return w;
  }

  /**
   * Defines the writer which should be used to output the generated data.
   *
   * @param w
   *          the writer
   */
  public void setWriter( final Writer w ) {
    this.w = w;
  }

  /**
   * Defines the separator, which is used to separate columns in a row.
   *
   * @param separator
   *          the separator string, never null.
   * @throws NullPointerException
   *           if the separator is null.
   * @throws IllegalArgumentException
   *           if the separator is an empty string.
   */
  public void setSeparator( final String separator ) {
    if ( separator == null ) {
      throw new NullPointerException();
    }
    if ( separator.length() == 0 ) {
      throw new IllegalArgumentException( "Separator must not be an empty string" );
    }
    this.quoter.setSeparator( separator );
  }

  /**
   * Gets the separator which is used to separate columns in a row.
   *
   * @return the separator, never null.
   */
  public String getSeparator() {
    return quoter.getSeparator();
  }

  /**
   * Writes the contents of the dataRow into the CSVRow.
   *
   * @param dr
   *          the dataRow which should be written
   * @param row
   *          the CSVRow used to collect the RowData.
   */
  private void writeDataRow( final DataRow dr, final CSVRow row ) {
    final String[] names = dr.getColumnNames();
    final int length = names.length;
    for ( int i = 0; i < length; i++ ) {
      final Object o = dr.get( names[i] );
      row.append( o );
    }
  }

  /**
   * Writes the names of the columns of the dataRow into the CSVRow.
   *
   * @param dr
   *          the dataRow which should be written
   * @param row
   *          the CSVRow used to collect the RowData.
   */
  private void writeDataRowNames( final DataRow dr, final CSVRow row ) {
    final String[] names = dr.getColumnNames();
    final int length = names.length;
    for ( int i = 0; i < length; i++ ) {
      final String columnName = names[i];
      row.append( columnName );
    }
  }

  /**
   * Writes the ReportHeader and (if defined) the dataRow names.
   *
   * @param event
   *          the event.
   */
  public void reportStarted( final ReportEvent event ) {
    if ( event.getState().isPrepareRun() ) {
      collectSubReports( event.getReport().getReportHeader() );
      return;
    }

    try {
      if ( isWriteDataRowNames() ) {
        final CSVRow names = new CSVRow( quoter );
        if ( isWriteStateColumns() ) {
          names.append( "report.currentgroup" );
          names.append( "report.eventtype" );
        }
        writeDataRowNames( event.getDataRow(), names );
        names.write( getWriter() );
      }

      if ( isEnableReportHeader() == false ) {
        return;
      }

      final CSVRow row = new CSVRow( quoter );
      if ( isWriteStateColumns() ) {
        row.append( -1 );
        row.append( "reportheader" );
      }
      writeDataRow( event.getDataRow(), row );
      row.write( getWriter() );

      collectSubReports( event.getReport().getReportHeader() );
    } catch ( IOException ioe ) {
      throw new FunctionProcessingException( "Error writing the current datarow", ioe );
    }
  }

  /**
   * Writes the ReportFooter.
   *
   * @param event
   *          the event.
   */
  public void reportFinished( final ReportEvent event ) {
    if ( event.getState().isPrepareRun() ) {
      collectSubReports( event.getReport().getReportFooter() );
      return;
    }

    if ( isEnableReportFooter() == false ) {
      collectSubReports( event.getReport().getReportFooter() );
      return;
    }

    try {
      final CSVRow row = new CSVRow( quoter );
      if ( isWriteStateColumns() ) {
        row.append( -1 );
        row.append( "reportfooter" );
      }
      writeDataRow( event.getDataRow(), row );
      row.write( getWriter() );

      collectSubReports( event.getReport().getReportFooter() );
    } catch ( IOException ioe ) {
      throw new FunctionProcessingException( "Error writing the current datarow", ioe );
    }
  }

  /**
   * Writes the GroupHeader of the current group.
   *
   * @param event
   *          the event.
   */
  public void groupStarted( final ReportEvent event ) {
    if ( ( event.getState().isPrepareRun() ) || ( isEnableGroupHeader() == false ) ) {
      final int currentIndex = event.getState().getCurrentGroupIndex();
      final Group g = event.getReport().getGroup( currentIndex );
      collectSubReports( g, ElementMetaData.TypeClassification.HEADER );
      return;
    }

    try {
      final int currentIndex = event.getState().getCurrentGroupIndex();

      final CSVRow row = new CSVRow( quoter );
      if ( isWriteStateColumns() ) {
        row.append( currentIndex );
        final Group g = event.getReport().getGroup( currentIndex );
        final String bandInfo = "groupheader name=\"" + g.getName() + '\"';
        row.append( bandInfo );
      }
      writeDataRow( event.getDataRow(), row );
      row.write( getWriter() );

      final Group g = event.getReport().getGroup( currentIndex );

      collectSubReports( g, ElementMetaData.TypeClassification.RELATIONAL_HEADER );
    } catch ( IOException ioe ) {
      throw new FunctionProcessingException( "Error writing the current datarow", ioe );
    }
  }

  /**
   * Writes the GroupFooter of the active group.
   *
   * @param event
   *          the event.
   */
  public void groupFinished( final ReportEvent event ) {
    if ( ( event.getState().isPrepareRun() ) || ( isEnableGroupFooter() == false ) ) {
      final int currentIndex = event.getState().getCurrentGroupIndex();
      final Group g = event.getReport().getGroup( currentIndex );
      collectSubReports( g, ElementMetaData.TypeClassification.FOOTER );
      return;
    }

    try {
      final int currentIndex = event.getState().getCurrentGroupIndex();

      final CSVRow row = new CSVRow( quoter );
      if ( isWriteStateColumns() ) {
        row.append( currentIndex );
        final Group g = event.getReport().getGroup( currentIndex );
        final String bandInfo = "groupfooter name=\"" + g.getName() + '\"';
        row.append( bandInfo );
      }
      writeDataRow( event.getDataRow(), row );
      row.write( getWriter() );
      final Group g = event.getReport().getGroup( currentIndex );
      collectSubReports( g, ElementMetaData.TypeClassification.RELATIONAL_FOOTER );
    } catch ( IOException ioe ) {
      throw new FunctionProcessingException( "Error writing the current datarow", ioe );
    }
  }

  /**
   * Receives notification that a group of item bands is about to be processed.
   * <P>
   * The next events will be itemsAdvanced events until the itemsFinished event is raised.
   *
   * @param event
   *          The event.
   */
  public void itemsStarted( final ReportEvent event ) {
    collectSubReports( event.getReport().getDetailsHeader() );
  }

  /**
   * Receives notification that a group of item bands has been completed.
   * <P>
   * The itemBand is finished, the report starts to close open groups.
   *
   * @param event
   *          The event.
   */
  public void itemsFinished( final ReportEvent event ) {
    collectSubReports( event.getReport().getDetailsFooter() );
  }

  /**
   * Writes the current ItemBand.
   *
   * @param event
   *          the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    if ( event.getState().isPrepareRun() ) {
      collectSubReports( event.getReport().getItemBand() );
      return;
    }

    if ( isEnableItemband() == false ) {
      collectSubReports( event.getReport().getItemBand() );
      return;
    }

    try {
      final CSVRow row = new CSVRow( quoter );
      if ( isWriteStateColumns() ) {
        row.append( event.getState().getCurrentGroupIndex() );
        row.append( "itemband" );
      }
      writeDataRow( event.getDataRow(), row );
      row.write( getWriter() );
      collectSubReports( event.getReport().getItemBand() );
    } catch ( IOException ioe ) {
      throw new FunctionProcessingException( "Error writing the current datarow", ioe );
    }
  }

  /**
   * Return a selfreference of this CSVWriter. This selfreference is used to confiugre the output process.
   *
   * @return this CSVWriter.
   */
  public Object getValue() {
    return this;
  }

  /**
   * The dependency level defines the level of execution for this function. Higher dependency functions are executed
   * before lower dependency functions. For ordinary functions and expressions, the range for dependencies is defined to
   * start from 0 (lowest dependency possible) to 2^31 (upper limit of int).
   * <p/>
   * PageLayouter functions override the default behaviour an place them self at depency level -1, an so before any user
   * defined function.
   *
   * @return the level.
   */
  public int getDependencyLevel() {
    return depLevel;
  }

  /**
   * Overrides the depency level. Should be lower than any other function depency.
   *
   * @param deplevel
   *          the new depency level.
   */
  public void setDependencyLevel( final int deplevel ) {
    this.depLevel = deplevel;
  }

  /**
   * This method simply clones the function. The CSVWriter does not maintain large internal states and therefore need
   * not to be aware of any advanced optimizations.
   *
   * @return the derived function.
   */
  public OutputFunction deriveForStorage() {
    try {
      return (OutputFunction) clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  /**
   * This method simply clones the function. The CSVWriter does not maintain large internal states and therefore need
   * not to be aware of any advanced optimizations.
   *
   * @return the derived function.
   */
  public OutputFunction deriveForPagebreak() {
    try {
      return (OutputFunction) clone();
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  /**
   * Clones the expression. The expression should be reinitialized after the cloning.
   * <P>
   * Expressions maintain no state, cloning is done at the beginning of the report processing to disconnect the
   * expression from any other object space.
   *
   * @return a clone of this expression.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public Object clone() throws CloneNotSupportedException {
    final CSVWriter o = (CSVWriter) super.clone();
    o.inlineSubreports = (ArrayList<InlineSubreportMarker>) inlineSubreports.clone();
    return o;
  }

  public InlineSubreportMarker[] getInlineSubreports() {
    return inlineSubreports.toArray( new InlineSubreportMarker[inlineSubreports.size()] );
  }

  public void clearInlineSubreports( final SubReportProcessType inlineExecution ) {
    final InlineSubreportMarker[] subreports = getInlineSubreports();
    for ( int i = 0; i < subreports.length; i++ ) {
      final InlineSubreportMarker subreport = subreports[i];
      if ( inlineExecution == subreport.getProcessType() ) {
        inlineSubreports.remove( i );
      }
    }
  }

  private void collectSubReports( final Group g, final ElementMetaData.TypeClassification type )
    throws FunctionProcessingException {
    final int elementCount = g.getElementCount();
    for ( int i = 0; i < elementCount; i += 1 ) {
      final Element e = g.getElement( i );
      if ( e.getMetaData().getReportElementType() != type ) {
        continue;
      }
      if ( e instanceof Band == false ) {
        continue;
      }

      collectSubReports( (Band) e );
    }
  }

  private void collectSubReports( final Band band ) throws FunctionProcessingException {
    final Element[] elements = band.getElementArray();
    for ( int i = 0; i < elements.length; i++ ) {
      final Element element = elements[i];
      if ( element instanceof SubReport ) {
        final InlineSubreportMarker marker =
            new InlineSubreportMarker( (SubReport) element.clone(), null, SubReportProcessType.BANDED );
        inlineSubreports.add( marker );
      } else if ( element instanceof Band ) {
        collectSubReports( (Band) element );
      }
    }
  }

  public void groupBodyFinished( final ReportEvent event ) {

  }

  public void restart( final ReportState state ) {

  }

  public boolean createRollbackInformation() {
    return false;
  }
}
