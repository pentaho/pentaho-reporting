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

package org.pentaho.reporting.engine.classic.core.layout.output.crosstab;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroupBody;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.build.LayoutModelBuilder;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.RenderNode;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableCellRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.model.table.TableSectionRenderBox;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.GroupOutputHandler;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

public class CrosstabRowOutputHandler implements GroupOutputHandler {
  private static final Log logger = LogFactory.getLog( CrosstabRowOutputHandler.class );

  public CrosstabRowOutputHandler() {
  }

  public void groupStarted( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final RenderedCrosstabLayout crosstabLayout = outputFunction.getCurrentRenderedCrosstabLayout();
    final LayoutModelBuilder layoutModelBuilder = outputFunction.getRenderer().getNormalFlowLayoutModelBuilder();
    final int gidx = event.getState().getCurrentGroupIndex();
    final CrosstabRowGroup group = (CrosstabRowGroup) event.getReport().getGroup( gidx );

    if ( crosstabLayout.isCrosstabTableOpen() == false ) {
      buildHeaderPlaceholder( crosstabLayout, layoutModelBuilder );
      crosstabLayout.setFirstRowGroupIndex( gidx );
      crosstabLayout.setCrosstabTableOpen( true );
    }

    if ( crosstabLayout.isCrosstabHeaderOpen() ) {
      layoutModelBuilder.startSubFlow( crosstabLayout.getRowTitleHeaderId() );
      CrosstabOutputHelper.createAutomaticCell( layoutModelBuilder );
      outputFunction.getRenderer().add( group.getTitleHeader(), outputFunction.getRuntime() );
      layoutModelBuilder.finishBox();
      layoutModelBuilder.suspendSubFlow();
    }

    if ( crosstabLayout.isCrosstabRowOpen() == false ) {
      // start a new row if needed ..
      layoutModelBuilder.startBox( CrosstabOutputHelper.createTableRow() );
      crosstabLayout.setCrosstabRowOpen( true );

      // flag not empty is needed to connect the new node with the rest of the layout model.
      layoutModelBuilder.legacyFlagNotEmpty();
      final TableSectionRenderBox rowRenderNode =
          CrosstabOutputHelper.findTableSection( layoutModelBuilder.dangerousRawAccess() );

      for ( int i = crosstabLayout.getFirstRowGroupIndex(), count = 0; i < gidx; i += 1, count += 1 ) {
        final InstanceID rowHeader = crosstabLayout.getRowHeader( i - crosstabLayout.getFirstRowGroupIndex() );
        final RenderNode cell = CrosstabOutputHelper.findNode( rowRenderNode, rowHeader );
        if ( cell instanceof TableCellRenderBox ) {
          final TableCellRenderBox cellBox = (TableCellRenderBox) cell;
          cellBox.update( cellBox.getRowSpan() + 1, cellBox.getColSpan() );
        } else {
          throw new IllegalStateException( "Unable to find previously defined row header. Aborting report processing." );
        }
      }
    }

    final int firstRowGroupIndex = crosstabLayout.getFirstRowGroupIndex();
    if ( gidx == firstRowGroupIndex ) {
      RenderNode renderNode = layoutModelBuilder.dangerousRawAccess();
      RenderBox parentNode =
          (RenderBox) CrosstabOutputHelper.findParentNode( renderNode, crosstabLayout.getCrosstabId() );
      parentNode.setPreventPagination( true );
    }

    CrosstabOutputHelper.createAutomaticCell( layoutModelBuilder, 1, 1, group.getHeader() );
    crosstabLayout.setRowHeader( gidx - crosstabLayout.getFirstRowGroupIndex(), layoutModelBuilder.dangerousRawAccess()
        .getInstanceId() );
    outputFunction.getRenderer().add( group.getHeader(), outputFunction.getRuntime() );
    layoutModelBuilder.finishBox();
  }

  public void groupFinished( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    CrosstabOutputHelper.printCrosstabSummary( outputFunction, event );

    final RenderedCrosstabLayout crosstabLayout = outputFunction.getCurrentRenderedCrosstabLayout();
    final LayoutModelBuilder layoutModelBuilder = outputFunction.getRenderer().getNormalFlowLayoutModelBuilder();

    if ( crosstabLayout.isCrosstabRowOpen() ) {
      layoutModelBuilder.finishBox(); // Table-Row
      crosstabLayout.setCrosstabRowOpen( false );
    }

    if ( crosstabLayout.isCrosstabHeaderOpen() ) {
      // close all header rows
      final InstanceID[] columnHeaders = crosstabLayout.getColumnHeaderSubFlows();
      for ( int i = 0; i < columnHeaders.length; i++ ) {
        final InstanceID columnHeader = columnHeaders[i];
        layoutModelBuilder.startSubFlow( columnHeader );
        layoutModelBuilder.endSubFlow();
      }
      crosstabLayout.setCrosstabHeaderOpen( false );
    }

    final int gidx = event.getState().getCurrentGroupIndex();
    final int firstRowGroupIndex = crosstabLayout.getFirstRowGroupIndex();
    if ( gidx == firstRowGroupIndex ) {
      RenderNode renderNode = layoutModelBuilder.dangerousRawAccess();
      RenderBox parentNode =
          (RenderBox) CrosstabOutputHelper.findParentNode( renderNode, crosstabLayout.getCrosstabId() );
      parentNode.setPreventPagination( false );
    }
  }

  public void groupBodyFinished( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {

  }

  private void buildHeaderPlaceholder( final RenderedCrosstabLayout crosstabLayout,
      final LayoutModelBuilder layoutModelBuilder ) {
    crosstabLayout.setCrosstabId( layoutModelBuilder.startBox( CrosstabOutputHelper.createTable( crosstabLayout
        .getTableLayout() ) ) );

    layoutModelBuilder.startBox( CrosstabOutputHelper.createTableBand( BandStyleKeys.LAYOUT_TABLE_HEADER ) );

    // create column group placeholder rows. We subsequently add content as sub-flows into these groups.
    int columnGroupCount = crosstabLayout.getColumnGroups();
    if ( crosstabLayout.isGenerateColumnTitleHeaders() ) {
      columnGroupCount += crosstabLayout.getColumnGroups();
    }
    if ( crosstabLayout.isGenerateMeasureHeaders() ) {
      columnGroupCount += 1;
    }
    final InstanceID[] columnHeaders = new InstanceID[Math.max( 1, columnGroupCount )];
    for ( int i = 0; i < columnHeaders.length; i += 1 ) {
      columnHeaders[i] = layoutModelBuilder.createSubflowPlaceholder( CrosstabOutputHelper.createTableRow() );
    }

    if ( columnHeaders.length > 1 ) {
      // Adds a empty cell that consumes the area above the row-header
      layoutModelBuilder.startSubFlow( columnHeaders[0] );
      CrosstabOutputHelper.createAutomaticCell( layoutModelBuilder, crosstabLayout.getRowGroups(),
          columnHeaders.length - 1 );
      layoutModelBuilder.legacyFlagNotEmpty();
      layoutModelBuilder.finishBox();
      layoutModelBuilder.suspendSubFlow();
    }

    layoutModelBuilder.finishBox(); // BandStyleKeys.LAYOUT_TABLE_HEADER

    layoutModelBuilder.startBox( CrosstabOutputHelper.createTableBand( BandStyleKeys.LAYOUT_TABLE_BODY ) );

    crosstabLayout.setCrosstabHeaderOpen( true );
    crosstabLayout.setColumnHeaderRowIds( columnHeaders );
  }

  public void itemsStarted( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    throw new ReportProcessingException( "A crosstab-row cannot contain a detail band. Never." );
  }

  public void itemsAdvanced( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    throw new ReportProcessingException( "A crosstab-row cannot contain a detail band. Never." );
  }

  public void itemsFinished( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    throw new ReportProcessingException( "A crosstab-row cannot contain a detail band. Never." );
  }

  public void summaryRowStart( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final RenderedCrosstabLayout crosstabLayout = outputFunction.getCurrentRenderedCrosstabLayout();
    final LayoutModelBuilder layoutModelBuilder = outputFunction.getRenderer().getNormalFlowLayoutModelBuilder();

    if ( crosstabLayout.isCrosstabRowOpen() ) {
      throw new IllegalStateException( "Event Order Error: A summary row cannot be printed while a row is still open." );
    }

    final int gidx = event.getState().getCurrentGroupIndex() + 1;
    final CrosstabRowGroup group = (CrosstabRowGroup) event.getReport().getGroup( gidx );

    if ( group.isPrintSummary() == false ) {
      crosstabLayout.startSummaryRowProcessing( false, gidx, null );
      return;
    }

    if ( group.getField() == null ) {
      crosstabLayout.startSummaryRowProcessing( false, gidx, null );
      return;
    }

    final CrosstabCellBody dataBody = event.getReport().getCrosstabCellBody();
    final CrosstabCell element = dataBody.findElement( group.getField(), null );
    if ( element == null ) {
      crosstabLayout.startSummaryRowProcessing( false, gidx, null );
      return;
    }

    if ( crosstabLayout.isCrosstabRowOpen() == false ) {
      // start a new row if needed ..
      layoutModelBuilder.startBox( CrosstabOutputHelper.createTableRow() );
      crosstabLayout.setCrosstabRowOpen( true );

      // flag not empty is needed to connect the new node with the rest of the layout model.
      layoutModelBuilder.legacyFlagNotEmpty();
      final TableSectionRenderBox rowRenderNode =
          CrosstabOutputHelper.findTableSection( layoutModelBuilder.dangerousRawAccess() );
      for ( int i = crosstabLayout.getFirstRowGroupIndex(), count = 0; i < gidx; i += 1, count += 1 ) {
        final InstanceID rowHeader = crosstabLayout.getRowHeader( i - crosstabLayout.getFirstRowGroupIndex() );
        final RenderNode cell = CrosstabOutputHelper.findNode( rowRenderNode, rowHeader );
        if ( cell instanceof TableCellRenderBox ) {
          final TableCellRenderBox cellBox = (TableCellRenderBox) cell;
          cellBox.update( cellBox.getRowSpan() + 1, cellBox.getColSpan() );
        } else {
          throw new IllegalStateException( "Unable to find previously defined row header. Aborting report processing." );
        }
      }
    }

    // An outer row-group's summary cell spans across all inner row-group header-columns up to the start
    // of the data area.
    final int colSpan = crosstabLayout.getFirstColGroupIndex() - gidx;
    CrosstabOutputHelper.createAutomaticCell( layoutModelBuilder, colSpan, 1, group.getSummaryHeader() );
    crosstabLayout.setRowHeader( gidx - crosstabLayout.getFirstRowGroupIndex(), layoutModelBuilder.dangerousRawAccess()
        .getInstanceId() );
    outputFunction.getRenderer().add( group.getSummaryHeader(), outputFunction.getRuntime() );
    layoutModelBuilder.finishBox();

    crosstabLayout.startSummaryRowProcessing( true, gidx, group.getField() );
    crosstabLayout.setDetailsRendered( false );
    crosstabLayout.setProcessingCrosstabHeader( false );
  }

  public void summaryRowEnd( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final RenderedCrosstabLayout crosstabLayout = outputFunction.getCurrentRenderedCrosstabLayout();
    if ( crosstabLayout.isSummaryRowPrintable() == false ) {
      return;
    }

    final LayoutModelBuilder layoutModelBuilder = outputFunction.getRenderer().getNormalFlowLayoutModelBuilder();

    if ( crosstabLayout.isCrosstabRowOpen() ) {
      printSummaryCell( outputFunction, event );

      layoutModelBuilder.finishBox(); // Table-Row
      crosstabLayout.setCrosstabRowOpen( false );
    }
  }

  public void summaryRow( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final RenderedCrosstabLayout crosstabLayout = outputFunction.getCurrentRenderedCrosstabLayout();
    if ( crosstabLayout.isSummaryRowPrintable() == false ) {
      return;
    }

    printSummaryCell( outputFunction, event );
  }

  private void printSummaryCell( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final RenderedCrosstabLayout crosstabLayout = outputFunction.getCurrentRenderedCrosstabLayout();

    final int gidx = event.getState().getCurrentGroupIndex();
    final Group group = event.getReport().getGroup( gidx );
    final String columnField;
    final GroupBody groupBody = group.getBody();
    if ( groupBody instanceof CrosstabColumnGroupBody ) {
      final CrosstabColumnGroupBody columnGroupBody = (CrosstabColumnGroupBody) groupBody;
      final CrosstabColumnGroup next = columnGroupBody.getGroup();
      if ( next.isPrintSummary() == false ) {
        return;
      }
      columnField = next.getField();
    } else if ( groupBody instanceof CrosstabRowGroupBody ) {
      // the final sum of the row. The column field will be the first column group
      final CrosstabColumnGroup colGroup =
          (CrosstabColumnGroup) event.getReport().getGroup( crosstabLayout.getFirstColGroupIndex() );
      if ( colGroup.isPrintSummary() == false ) {
        return;
      }
      columnField = colGroup.getField();
    } else {
      // a detail level summary row cell.
      columnField = null;
    }

    final LayoutModelBuilder layoutModelBuilder = outputFunction.getRenderer().getNormalFlowLayoutModelBuilder();
    final CrosstabCellBody dataBody = event.getReport().getCrosstabCellBody();
    final CrosstabCell element = dataBody.findElement( crosstabLayout.getSummaryRowField(), columnField );

    if ( element != null ) {
      CrosstabOutputHelper.createAutomaticCell( layoutModelBuilder );
      layoutModelBuilder.legacyFlagNotEmpty();
      outputFunction.getRenderer().startSection( Renderer.SectionType.NORMALFLOW );
      outputFunction.getRenderer().add( element, outputFunction.getRuntime() );
      outputFunction.addSubReportMarkers( outputFunction.getRenderer().endSection() );
      layoutModelBuilder.finishBox();
    } else {
      CrosstabOutputHelper.createAutomaticCell( layoutModelBuilder );
      layoutModelBuilder.legacyFlagNotEmpty();
      logger.debug( String.format( "Unable to find summary cell: %s - %s", // NON-NLS
          crosstabLayout.getSummaryRowField(), columnField ) );
      layoutModelBuilder.finishBox();
    }

  }
}
