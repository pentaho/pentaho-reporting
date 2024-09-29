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


package org.pentaho.reporting.engine.classic.core.layout.output;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.DetailsFooter;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;

public class RelationalGroupOutputHandler implements GroupOutputHandler {
  public RelationalGroupOutputHandler() {
  }

  public void groupStarted( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final int gidx = event.getState().getCurrentGroupIndex();
    final RelationalGroup group = (RelationalGroup) event.getReport().getGroup( gidx );
    final Band b = group.getHeader();
    final GroupBody groupBody = group.getBody();

    outputFunction.updateFooterArea( event );
    final Renderer renderer = outputFunction.getRenderer();
    renderer.startGroup( group, event.getState().getPredictedStateCount() );
    renderer.startSection( Renderer.SectionType.NORMALFLOW );
    outputFunction.print( outputFunction.getRuntime(), b );
    outputFunction.addSubReportMarkers( renderer.endSection() );
    renderer.startGroupBody( groupBody, event.getState().getPredictedStateCount() );
  }

  public void itemsStarted( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    // activating this state after the page has ended is invalid.
    final int numberOfRows = event.getState().getNumberOfRows();
    final Renderer renderer = outputFunction.getRenderer();
    outputFunction.updateFooterArea( event );

    final DetailsHeader detailsHeader = event.getReport().getDetailsHeader();
    if ( detailsHeader != null ) {
      renderer.startSection( Renderer.SectionType.NORMALFLOW );
      outputFunction.print( outputFunction.getRuntime(), detailsHeader );
      outputFunction.addSubReportMarkers( renderer.endSection() );
    }

    if ( numberOfRows == 0 || outputFunction.getMetaData().isFeatureSupported( OutputProcessorFeature.DESIGNTIME ) ) {
      // ups, we have no data. Lets signal that ...
      final NoDataBand noDataBand = event.getReport().getNoDataBand();
      if ( noDataBand != null ) {
        renderer.startSection( Renderer.SectionType.NORMALFLOW );
        outputFunction.print( outputFunction.getRuntime(), noDataBand );
        outputFunction.addSubReportMarkers( renderer.endSection() );
        // there will be no item-band printed.
      }
    }

  }

  public void itemsAdvanced( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final Renderer renderer = outputFunction.getRenderer();
    outputFunction.updateFooterArea( event );

    final ItemBand itemBand = event.getReport().getItemBand();
    if ( itemBand != null ) {
      renderer.startSection( Renderer.SectionType.NORMALFLOW );
      outputFunction.print( outputFunction.getRuntime(), itemBand );
      outputFunction.addSubReportMarkers( renderer.endSection() );
    }
  }

  public void itemsFinished( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final Renderer renderer = outputFunction.getRenderer();
    outputFunction.updateFooterArea( event );

    final DetailsFooter detailsFooter = event.getReport().getDetailsFooter();
    if ( detailsFooter != null ) {
      renderer.startSection( Renderer.SectionType.NORMALFLOW );
      ExpressionRuntime runtime = outputFunction.getRuntime();
      outputFunction.print( runtime, detailsFooter );
      outputFunction.addSubReportMarkers( renderer.endSection() );
    }
  }

  public void groupBodyFinished( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final Renderer renderer = outputFunction.getRenderer();
    outputFunction.updateFooterArea( event );
    // only happens for inner groups.
    renderer.endGroupBody();
  }

  public void groupFinished( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    final int gidx = event.getState().getCurrentGroupIndex();
    final RelationalGroup g = (RelationalGroup) event.getReport().getGroup( gidx );
    final Band b = g.getFooter();

    final Renderer renderer = outputFunction.getRenderer();
    outputFunction.updateFooterArea( event );

    renderer.startSection( Renderer.SectionType.NORMALFLOW );
    outputFunction.print( outputFunction.getRuntime(), b );
    outputFunction.addSubReportMarkers( renderer.endSection() );
    renderer.endGroup();
  }

  public void summaryRowStart( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    // not used.
  }

  public void summaryRowEnd( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    // not used.
  }

  public void summaryRow( final DefaultOutputFunction outputFunction, final ReportEvent event )
    throws ReportProcessingException {
    // not used.
  }
}
