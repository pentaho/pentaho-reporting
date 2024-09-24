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

package org.pentaho.reporting.engine.classic.core.layout.build;

import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.RenderComponentFactory;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.states.ReportStateKey;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import java.util.ArrayList;

public class ReportRenderModelBuilder implements RenderModelBuilder, Cloneable {
  private static final InlineSubreportMarker[] EMPTY_SUBREPORT_MARKERS = new InlineSubreportMarker[0];
  private LayoutModelBuilder normalFlow;
  private LayoutModelBuilder header;
  private LayoutModelBuilder footer;
  private LayoutModelBuilder repeatedFooter;
  private LayoutModelBuilder watermark;
  private Renderer.SectionType activeSection;
  private LogicalPageBox pageBox;

  private ArrayList<InlineSubreportMarker> collectedSubReportMarker;
  private ProcessingContext processingContext;
  private RenderNodeFactory renderNodeFactory;
  private ReportStateKey stateKey;
  private LayoutBuilderStrategy layoutBuilderStrategy;
  private RenderComponentFactory componentFactory;

  public ReportRenderModelBuilder( final RenderComponentFactory componentFactory ) {
    this.componentFactory = componentFactory;
    this.layoutBuilderStrategy = componentFactory.createLayoutBuilderStrategy();
    this.collectedSubReportMarker = new ArrayList<InlineSubreportMarker>();
  }

  public void startReport( final ReportDefinition report, final ProcessingContext processingContext ) {
    final OutputProcessorMetaData outputProcessorMetaData = processingContext.getOutputProcessorMetaData();

    renderNodeFactory = componentFactory.createRenderNodeFactory();
    renderNodeFactory.initialize( outputProcessorMetaData );

    this.processingContext = processingContext;

    final StyleSheet resolverStyle = report.getComputedStyle();
    this.pageBox = renderNodeFactory.createPage( report, resolverStyle );

    normalFlow = createNormalBuilder( processingContext );

    header = createHeaderBuilder( processingContext );
    footer = createFooterBuilder( processingContext );
    repeatedFooter = createRepeatedFooterBuilder( processingContext );
    watermark = createWatermarkBuilder( processingContext );
  }

  protected LayoutModelBuilder createNormalBuilder( final ProcessingContext processingContext ) {
    LayoutModelBuilder normalFlow = componentFactory.createLayoutModelBuilder( "Section-0" );
    normalFlow.initialize( processingContext, this.pageBox.getContentArea(), renderNodeFactory );
    normalFlow.updateState( stateKey );
    return normalFlow;
  }

  protected LayoutModelBuilder createHeaderBuilder( final ProcessingContext processingContext ) {
    HeaderLayoutModelBuilder header =
        new HeaderLayoutModelBuilder( componentFactory.createLayoutModelBuilder( "Header-1" ) );
    header.initialize( processingContext, this.pageBox.getHeaderArea(), renderNodeFactory );
    header.updateState( stateKey );
    return header;
  }

  protected LayoutModelBuilder createFooterBuilder( final ProcessingContext processingContext ) {
    FooterLayoutModelBuilder footer =
        new FooterLayoutModelBuilder( componentFactory.createLayoutModelBuilder( "Footer-2" ) );
    footer.initialize( processingContext, this.pageBox.getFooterArea(), renderNodeFactory );
    footer.updateState( stateKey );
    return footer;
  }

  protected LayoutModelBuilder createRepeatedFooterBuilder( final ProcessingContext processingContext ) {
    RepeatedFooterLayoutModelBuilder repeatedFooter =
        new RepeatedFooterLayoutModelBuilder( componentFactory.createLayoutModelBuilder( "Repeat-Footer-3" ) );
    repeatedFooter.initialize( processingContext, this.pageBox.getRepeatFooterArea(), renderNodeFactory );
    repeatedFooter.updateState( stateKey );
    return repeatedFooter;
  }

  protected LayoutModelBuilder createWatermarkBuilder( final ProcessingContext processingContext ) {
    WatermarkLayoutModelBuilder watermark =
        new WatermarkLayoutModelBuilder( componentFactory.createLayoutModelBuilder( "Watermark-Section" ) );
    watermark.initialize( processingContext, this.pageBox.getWatermarkArea(), renderNodeFactory );
    watermark.updateState( stateKey );
    return watermark;
  }

  protected RenderNodeFactory getRenderNodeFactory() {
    return renderNodeFactory;
  }

  public ReportStateKey getStateKey() {
    return stateKey;
  }

  protected RenderComponentFactory getComponentFactory() {
    return componentFactory;
  }

  public void updateStateKey( final ReportStateKey stateKey ) {
    this.stateKey = stateKey;
    if ( normalFlow != null ) {
      normalFlow.updateState( stateKey );
    }
    if ( header != null ) {
      header.updateState( stateKey );
    }
    if ( footer != null ) {
      footer.updateState( stateKey );
    }
    if ( repeatedFooter != null ) {
      repeatedFooter.updateState( stateKey );
    }
    if ( watermark != null ) {
      watermark.updateState( stateKey );
    }
  }

  public void startSubReport( final ReportDefinition report, final InstanceID insertationPoint ) {
    if ( insertationPoint == null ) {
      normalFlow.startSubFlow( report );
    } else {
      normalFlow.startSubFlow( insertationPoint );
    }
  }

  public void startGroup( final Group group, final Integer predictedStateCount ) {
    final int count;
    if ( predictedStateCount == null ) {
      count = 0;
    } else {
      count = predictedStateCount;
    }
    normalFlow.startSection( group, count );
  }

  public void startGroupBody( final GroupBody groupBody, final Integer predictedStateCount ) {
    final int count;
    if ( predictedStateCount == null ) {
      count = 0;
    } else {
      count = predictedStateCount;
    }
    normalFlow.startSection( groupBody, count );
  }

  public void startSection( final Renderer.SectionType type ) {
    this.activeSection = type;
    this.collectedSubReportMarker.clear();
    getLayoutModelBuilder().startSection();
  }

  public void addProgressBox() throws ReportProcessingException {
    normalFlow.addProgressMarkerBox();
  }

  public void addEmptyRootLevelBand() throws ReportProcessingException {
    getLayoutModelBuilder().addProgressMarkerBox();
  }

  public void addPageBreak() {
    if ( getPageBox() == null ) {
      throw new IllegalStateException();
    }

    normalFlow.addManualPageBreakBox( getPageBox().getPageOffset() );
  }

  public void add( final ExpressionRuntime runtime, final Band band ) throws ReportProcessingException {
    final LayoutBuilderStrategy builderStrategy = getLayoutBuilderStrategy();
    LayoutModelBuilder layoutModelBuilder = getLayoutModelBuilder();
    builderStrategy.add( runtime, layoutModelBuilder, band, collectedSubReportMarker );
  }

  public void addToNormalFlow( final ExpressionRuntime runtime, final Band band ) throws ReportProcessingException {
    final LayoutBuilderStrategy builderStrategy = getLayoutBuilderStrategy();
    builderStrategy.add( runtime, normalFlow, band, collectedSubReportMarker );
  }

  protected LayoutBuilderStrategy getLayoutBuilderStrategy() {
    return layoutBuilderStrategy;
  }

  public SectionResult endSection() {
    final boolean empty = getLayoutModelBuilder().isEmpty();
    getLayoutModelBuilder().endSection();
    final InlineSubreportMarker[] markers;
    if ( collectedSubReportMarker.isEmpty() ) {
      markers = EMPTY_SUBREPORT_MARKERS;
    } else {
      markers = collectedSubReportMarker.toArray( new InlineSubreportMarker[collectedSubReportMarker.size()] );
    }

    activeSection = Renderer.SectionType.NORMALFLOW;
    return new SectionResult( markers, empty );
  }

  public void endGroupBody() {
    normalFlow.endSection();
  }

  public void endGroup() {
    normalFlow.endSection();
  }

  public void endSubReport() {
    normalFlow.endSubFlow();
  }

  public void endReport() {
    getPageBox().getContentArea().close();
    getPageBox().close();

    normalFlow.close();
    header.close();
    footer.close();
    repeatedFooter.close();
    watermark.close();
    renderNodeFactory.close();
  }

  public LayoutModelBuilder getNormalFlowLayoutModelBuilder() {
    return normalFlow;
  }

  private LayoutModelBuilder getLayoutModelBuilder() {
    switch ( activeSection ) {
      case NORMALFLOW:
        return normalFlow;
      case HEADER:
        return header;
      case FOOTER:
        return footer;
      case REPEAT_FOOTER:
        return repeatedFooter;
      case WATERMARK:
        return watermark;
      default:
        throw new IllegalStateException();
    }
  }

  public ReportRenderModelBuilder clone() {
    try {
      final ReportRenderModelBuilder builder = (ReportRenderModelBuilder) super.clone();
      builder.collectedSubReportMarker = (ArrayList<InlineSubreportMarker>) collectedSubReportMarker.clone();
      return builder;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException( e );
    }
  }

  public LogicalPageBox getPageBox() {
    return pageBox;
  }

  public RenderModelBuilder deriveForStorage() {
    final ReportRenderModelBuilder clone = clone();
    if ( pageBox != null ) {
      clone.pageBox = (LogicalPageBox) clone.pageBox.derive( true );
      clone.normalFlow = clone.normalFlow.deriveForStorage( clone.pageBox.getContentArea() );
      clone.header = clone.header.deriveForStorage( clone.pageBox.getHeaderArea() );
      clone.footer = clone.footer.deriveForStorage( clone.pageBox.getFooterArea() );
      clone.repeatedFooter = clone.repeatedFooter.deriveForStorage( clone.pageBox.getRepeatFooterArea() );
      clone.watermark = clone.watermark.deriveForStorage( clone.pageBox.getWatermarkArea() );
    }
    return clone;
  }

  public RenderModelBuilder deriveForPageBreak() {
    final ReportRenderModelBuilder clone = clone();
    if ( pageBox != null ) {
      clone.normalFlow = clone.normalFlow.deriveForPageBreak();
      clone.header = clone.header.deriveForPageBreak();
      clone.footer = clone.footer.deriveForPageBreak();
      clone.repeatedFooter = clone.repeatedFooter.deriveForPageBreak();
      clone.watermark = clone.watermark.deriveForPageBreak();
    }
    return clone;
  }

  public void performParanoidModelCheck() {
    if ( pageBox == null ) {
      return;
    }
    normalFlow.performParanoidModelCheck( pageBox.getContentArea() );
    header.performParanoidModelCheck( pageBox.getHeaderArea() );
    footer.performParanoidModelCheck( pageBox.getFooterArea() );
    repeatedFooter.performParanoidModelCheck( pageBox.getRepeatFooterArea() );
    watermark.performParanoidModelCheck( pageBox.getWatermarkArea() );
  }

  public void validateAfterCommit() {
    if ( pageBox == null ) {
      return;
    }
    normalFlow.validateAfterCommit();
    header.validateAfterCommit();
    footer.validateAfterCommit();
    repeatedFooter.validateAfterCommit();
    watermark.validateAfterCommit();
  }

  public void restoreStateAfterRollback() {
    header.initialize( processingContext, pageBox.getHeaderArea(), renderNodeFactory );
    header.restoreStateAfterRollback();
    footer.initialize( processingContext, pageBox.getFooterArea(), renderNodeFactory );
    footer.restoreStateAfterRollback();
    repeatedFooter.initialize( processingContext, pageBox.getRepeatFooterArea(), renderNodeFactory );
    repeatedFooter.restoreStateAfterRollback();
    watermark.initialize( processingContext, pageBox.getWatermarkArea(), renderNodeFactory );
    watermark.restoreStateAfterRollback();

    normalFlow.restoreStateAfterRollback();
  }
}
