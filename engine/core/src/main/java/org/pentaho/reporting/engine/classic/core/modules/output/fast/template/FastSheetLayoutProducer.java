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


package org.pentaho.reporting.engine.classic.core.modules.output.fast.template;

import java.util.HashSet;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.StreamingRenderer;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.ContentProcessingException;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.engine.classic.core.modules.output.fast.FastExportTemplate;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableLayoutProducer;
import org.pentaho.reporting.engine.classic.core.states.DefaultPerformanceMonitorContext;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.resolver.SimpleStyleResolver;

public class FastSheetLayoutProducer implements FastExportTemplate {
  private OutputProcessorMetaData metaData;
  private SheetLayout sharedSheetLayout;
  private HashSet<DynamicStyleKey> processedBands;

  public FastSheetLayoutProducer( final SheetLayout sharedSheetLayout ) {
    this.sharedSheetLayout = sharedSheetLayout;
    this.processedBands = new HashSet<DynamicStyleKey>();
  }

  public void write( final Band band, final ExpressionRuntime runtime ) throws InvalidReportStateException {
    DynamicStyleKey dynamicStyleKey = DynamicStyleKey.create( band, runtime );
    if ( processedBands.contains( dynamicStyleKey ) ) {
      return;
    }

    try {
      LayoutProducer templateListener = new LayoutProducer( metaData, sharedSheetLayout );
      final OutputProcessor op =
          new TemplatingOutputProcessor( runtime.getProcessingContext().getOutputProcessorMetaData(), templateListener );

      FastSheetLayoutProducer.performLayout( band, runtime, op );

      processedBands.add( dynamicStyleKey );
    } catch ( ContentProcessingException e ) {
      throw new InvalidReportStateException();
    } catch ( ReportProcessingException e ) {
      throw new InvalidReportStateException();
    }
  }

  public void initialize( final ReportDefinition reportDefinition, final ExpressionRuntime runtime,
      final boolean pagination ) {
    this.metaData = runtime.getProcessingContext().getOutputProcessorMetaData();
  }

  public void finishReport() {
    this.sharedSheetLayout.pageCompleted();
  }

  private static class LayoutProducer implements FastExportTemplateListener {
    private SheetLayout sheetLayout;
    private OutputProcessorMetaData metaData;

    private LayoutProducer( final OutputProcessorMetaData metaData, final SheetLayout sheetLayout ) {
      this.metaData = metaData;
      this.sheetLayout = sheetLayout;
    }

    public void produceTemplate( final LogicalPageBox pageBox ) {
      sheetLayout.clearVerticalInfo();

      TableLayoutProducer currentLayout = new TableLayoutProducer( metaData, sheetLayout );
      currentLayout.update( pageBox, false );
      currentLayout.pageCompleted();
    }
  }

  public static void
    performLayout( final Band band, final ExpressionRuntime runtime, final OutputProcessor outputTarget )
      throws ReportProcessingException, ContentProcessingException {
    MasterReport report = createDummyReport( band );

    StreamingRenderer renderer = new StreamingRenderer( outputTarget );
    renderer.startReport( report, runtime.getProcessingContext(), new DefaultPerformanceMonitorContext() );
    renderer.startSection( Renderer.SectionType.NORMALFLOW );
    renderer.add( band, runtime );
    renderer.endSection();
    renderer.endReport();
    renderer.applyAutoCommit();
    if ( renderer.validatePages() == Renderer.LayoutResult.LAYOUT_UNVALIDATABLE ) {
      throw new ReportProcessingException( "Template layout is not valid, aborting" );
    }

    renderer.processPage( null, null, true );
  }

  protected static MasterReport createDummyReport( final Band band ) {
    ReportDefinition masterReport = band.getMasterReport();

    MasterReport dummy = new MasterReport();
    dummy.copyAttributes( masterReport.getAttributes() );
    dummy.setPageDefinition( masterReport.getPageDefinition() );

    SimpleStyleResolver simpleStyleResolver = new SimpleStyleResolver( true );
    ResolverStyleSheet resolveStyleSheet = new ResolverStyleSheet();
    simpleStyleResolver.resolve( dummy, resolveStyleSheet );
    dummy.setComputedStyle( new SimpleStyleSheet( resolveStyleSheet ) );
    return dummy;
  }
}
