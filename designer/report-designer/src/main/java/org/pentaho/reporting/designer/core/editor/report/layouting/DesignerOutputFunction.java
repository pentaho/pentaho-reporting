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


package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultOutputFunction;
import org.pentaho.reporting.engine.classic.core.layout.output.LayouterLevel;
import org.pentaho.reporting.engine.classic.core.states.ReportState;

public class DesignerOutputFunction extends DefaultOutputFunction {
  public DesignerOutputFunction() {
  }

  protected ExpressionRuntime updateDetailsHeader( final ReportState state,
                                                   final ProcessingContext processingContext,
                                                   final ReportDefinition report,
                                                   final ExpressionRuntime runtime ) throws ReportProcessingException {
    return runtime;
  }

  protected boolean updateRepeatingFooters( final ReportEvent event,
                                            final LayouterLevel[] levels ) throws ReportProcessingException {
    return false;
  }

  protected boolean isPageFooterPrintable( final Band b, final boolean testSticky ) {
    return true;
  }

  protected boolean isNeedPrintRepeatingFooter( final ReportEvent event, final LayouterLevel[] levels ) {
    return false;
  }

  protected ExpressionRuntime updateRepeatingGroupHeader( final ReportState state,
                                                          final ProcessingContext processingContext,
                                                          final ReportDefinition report,
                                                          final LayouterLevel[] levels,
                                                          final ExpressionRuntime runtime )
    throws ReportProcessingException {
    return runtime;
  }

  protected void printDesigntimeFooter( final ReportEvent event ) throws ReportProcessingException {
    Renderer renderer = getRenderer();
    if ( isPrintHeaderAndFooter( event ) ) {
      renderer.startSection( Renderer.SectionType.NORMALFLOW );
      print( getRuntime(), event.getReport().getPageFooter() );
      addSubReportMarkers( renderer.endSection() );
    }
  }

  protected void printDesigntimeHeader( final ReportEvent event ) throws ReportProcessingException {
    Renderer renderer = getRenderer();
    final ReportDefinition report = event.getState().getReport();
    if ( isPrintHeaderAndFooter( event ) ) {
      renderer.startSection( Renderer.SectionType.NORMALFLOW );
      print( getRuntime(), report.getPageHeader() );
      addSubReportMarkers( renderer.endSection() );
    }
  }

  protected boolean isPrintHeaderAndFooter( final ReportEvent event ) {
    if ( event.getState().isSubReportEvent() == false ) {
      return false;
    }
    if ( event.getState().isInlineProcess() == false ) {
      return true;
    }
    return false;
  }

  protected boolean isDesignTime() {
    return true;
  }

  protected void printPerformanceStats() {
  }
}
