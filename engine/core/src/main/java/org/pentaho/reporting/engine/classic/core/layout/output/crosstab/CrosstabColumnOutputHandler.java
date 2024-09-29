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


package org.pentaho.reporting.engine.classic.core.layout.output.crosstab;

import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabDetailMode;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.build.LayoutModelBuilder;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.GroupOutputHandler;

public class CrosstabColumnOutputHandler implements GroupOutputHandler {
  public CrosstabColumnOutputHandler() {
  }

  public void groupStarted( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final RenderedCrosstabLayout crosstabLayout = outputFunction.getCurrentRenderedCrosstabLayout();
    final LayoutModelBuilder layoutModelBuilder = outputFunction.getRenderer().getNormalFlowLayoutModelBuilder();
    final int gidx = event.getState().getCurrentGroupIndex();
    final CrosstabColumnGroup group = (CrosstabColumnGroup) event.getReport().getGroup( gidx );

    if ( crosstabLayout.getFirstColGroupIndex() == -1 ) {
      // record the start of the column groups.
      crosstabLayout.setFirstColGroupIndex( gidx );
    }

    if ( crosstabLayout.isCrosstabHeaderOpen() == false ) {
      return;
    }

    expandColumnSpanAfterRowStart( crosstabLayout, layoutModelBuilder, gidx );

    if ( crosstabLayout.isGenerateColumnTitleHeaders() ) {
      layoutModelBuilder.startSubFlow( crosstabLayout.getColumnTitleHeaderSubflowId( gidx ) );
      CrosstabOutputHelper.createAutomaticCell( layoutModelBuilder );
      crosstabLayout.setColumnTitleHeaderCellId( gidx - crosstabLayout.getFirstColGroupIndex(), layoutModelBuilder
          .dangerousRawAccess().getInstanceId() );
      outputFunction.getRenderer().add( group.getTitleHeader(), outputFunction.getRuntime() );
      layoutModelBuilder.finishBox();
      layoutModelBuilder.suspendSubFlow();
    }

    layoutModelBuilder.startSubFlow( crosstabLayout.getColumnHeaderSubflowId( gidx ) );
    CrosstabOutputHelper.createAutomaticCell( layoutModelBuilder );
    crosstabLayout.setColumnHeaderCellId( gidx - crosstabLayout.getFirstColGroupIndex(), layoutModelBuilder
        .dangerousRawAccess().getInstanceId() );
    outputFunction.getRenderer().add( group.getHeader(), outputFunction.getRuntime() );
    layoutModelBuilder.finishBox();
    layoutModelBuilder.suspendSubFlow();

  }

  private void expandColumnSpanAfterRowStart( final RenderedCrosstabLayout crosstabLayout,
      final LayoutModelBuilder layoutModelBuilder, final int gidx ) {
    if ( crosstabLayout.isProcessingCrosstabHeader() == false ) {
      crosstabLayout.setProcessingCrosstabHeader( true );

      CrosstabOutputHelper.expandColumnHeaderSpan( crosstabLayout, layoutModelBuilder, gidx );
    }
  }

  public void groupFinished( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    if ( CrosstabOutputHelper.isLastColumnGroup( event ) ) {
      return;
    }

    CrosstabOutputHelper.printCrosstabSummary( outputFunction, event );
  }

  public void groupBodyFinished( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
  }

  public void itemsStarted( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    generateMeasureHeader( outputFunction, event );

    final LayoutModelBuilder layoutModelBuilder = outputFunction.getRenderer().getNormalFlowLayoutModelBuilder();
    CrosstabOutputHelper.createAutomaticCell( layoutModelBuilder );
    layoutModelBuilder.legacyFlagNotEmpty();

    final RenderedCrosstabLayout crosstabLayout = outputFunction.getCurrentRenderedCrosstabLayout();
    crosstabLayout.setDetailsRendered( false );
    crosstabLayout.setProcessingCrosstabHeader( false );
  }

  private void generateMeasureHeader( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final RenderedCrosstabLayout crosstabLayout = outputFunction.getCurrentRenderedCrosstabLayout();
    if ( !crosstabLayout.isCrosstabHeaderOpen() ) {
      return;
    }

    if ( !crosstabLayout.isGenerateMeasureHeaders() ) {
      return;
    }

    final CrosstabCellBody dataBody = event.getReport().getCrosstabCellBody();
    if ( dataBody == null ) {
      throw new InvalidReportStateException();
    }

    final LayoutModelBuilder layoutModelBuilder = outputFunction.getRenderer().getNormalFlowLayoutModelBuilder();
    layoutModelBuilder.startSubFlow( crosstabLayout.getMeasureHeaderSubflowId() );
    CrosstabOutputHelper.createAutomaticCell( layoutModelBuilder );
    outputFunction.getRenderer().add( dataBody.getHeader(), outputFunction.getRuntime() );
    layoutModelBuilder.finishBox();
    layoutModelBuilder.suspendSubFlow();
  }

  public void itemsAdvanced( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final CrosstabCellBody dataBody = event.getReport().getCrosstabCellBody();
    if ( dataBody == null ) {
      return;
    }

    final RenderedCrosstabLayout crosstabLayout = outputFunction.getCurrentRenderedCrosstabLayout();
    if ( crosstabLayout.isDetailsRendered() ) {
      return;
    }

    final CrosstabCell element = dataBody.findElement( null, null );
    if ( element != null ) {
      final CrosstabDetailMode detailMode = crosstabLayout.getDetailMode();
      if ( detailMode == null ) {
        throw new IllegalStateException();
      }
      if ( CrosstabDetailMode.last.equals( detailMode ) ) {
        crosstabLayout.setDetailsRendered( true );
        return;
      }

      outputFunction.getRenderer().startSection( Renderer.SectionType.NORMALFLOW );
      outputFunction.getRenderer().add( element, outputFunction.getRuntime() );
      outputFunction.addSubReportMarkers( outputFunction.getRenderer().endSection() );
      if ( CrosstabDetailMode.first.equals( detailMode ) ) {
        crosstabLayout.setDetailsRendered( true );
      }
    }
  }

  public void itemsFinished( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final RenderedCrosstabLayout crosstabLayout = outputFunction.getCurrentRenderedCrosstabLayout();
    if ( CrosstabDetailMode.last.equals( crosstabLayout.getDetailMode() ) ) {
      final CrosstabCellBody dataBody = event.getReport().getCrosstabCellBody();
      final CrosstabCell element = dataBody.findElement( null, null );
      if ( element != null ) {
        outputFunction.getRenderer().startSection( Renderer.SectionType.NORMALFLOW );
        outputFunction.getRenderer().add( element, outputFunction.getRuntime() );
        outputFunction.addSubReportMarkers( outputFunction.getRenderer().endSection() );
      }
    }

    final LayoutModelBuilder layoutModelBuilder = outputFunction.getRenderer().getNormalFlowLayoutModelBuilder();
    layoutModelBuilder.finishBox();
  }

  public void summaryRowStart( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    throw new ReportProcessingException( "Crosstab-column groups handler cannot contain summary-rows" );
  }

  public void summaryRowEnd( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    throw new ReportProcessingException( "Crosstab-column groups handler cannot contain summary-rows" );
  }

  public void summaryRow( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    throw new ReportProcessingException( "Crosstab-column groups handler cannot contain summary-rows" );
  }
}
