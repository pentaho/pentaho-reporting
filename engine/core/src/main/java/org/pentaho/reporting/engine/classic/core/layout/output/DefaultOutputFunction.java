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

package org.pentaho.reporting.engine.classic.core.layout.output;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.DetailsFooter;
import org.pentaho.reporting.engine.classic.core.DetailsHeader;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupFooter;
import org.pentaho.reporting.engine.classic.core.GroupHeader;
import org.pentaho.reporting.engine.classic.core.InvalidReportStateException;
import org.pentaho.reporting.engine.classic.core.PageFooter;
import org.pentaho.reporting.engine.classic.core.PageHeader;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Watermark;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.function.AbstractFunction;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.OutputFunction;
import org.pentaho.reporting.engine.classic.core.function.ProcessingContext;
import org.pentaho.reporting.engine.classic.core.layout.InlineSubreportMarker;
import org.pentaho.reporting.engine.classic.core.layout.Renderer;
import org.pentaho.reporting.engine.classic.core.layout.output.crosstab.CrosstabRowOutputHandler;
import org.pentaho.reporting.engine.classic.core.layout.output.crosstab.RenderedCrosstabLayout;
import org.pentaho.reporting.engine.classic.core.layout.output.crosstab.RenderedCrosstabOutputHandlerFactory;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.engine.classic.core.states.datarow.MasterDataRow;
import org.pentaho.reporting.engine.classic.core.states.process.SubReportProcessType;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.StyleSheet;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;
import org.pentaho.reporting.libraries.base.util.FastStack;

import javax.swing.table.TableModel;
import java.util.ArrayList;

public class DefaultOutputFunction extends AbstractFunction implements OutputFunction, PageEventListener {
  private static final Log logger = LogFactory.getLog( DefaultOutputFunction.class );
  private static final LayouterLevel[] EMPTY_LAYOUTER_LEVEL = new LayouterLevel[0];
  public static final InlineSubreportMarker[] EMPTY_INLINE_SUBREPORT_MARKERS = new InlineSubreportMarker[0];

  private ReportEvent currentEvent;
  private Renderer renderer;
  private boolean lastPagebreak;
  private DefaultLayoutPagebreakHandler pagebreakHandler;
  private ArrayList<InlineSubreportMarker> inlineSubreports;
  private FastStack<GroupOutputHandler> outputHandlers;
  private int beginOfRow;
  private FastStack<RenderedCrosstabLayout> renderedCrosstabLayouts;
  private GroupOutputHandlerFactory groupOutputHandlerFactory;
  private ElementChangeChecker elementChangeChecker;
  private int printedFooter;
  private int printedRepeatingFooter;
  private int avoidedFooter;
  private int avoidedRepeatingFooter;
  private RepeatingFooterValidator repeatingFooterValidator;
  private boolean clearedFooter;
  private ArrayList<InstanceID> subReportFooterTracker;

  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  public DefaultOutputFunction() {
    this.subReportFooterTracker = new ArrayList<InstanceID>();
    this.repeatingFooterValidator = new RepeatingFooterValidator();
    this.pagebreakHandler = new DefaultLayoutPagebreakHandler();
    this.inlineSubreports = new ArrayList<InlineSubreportMarker>();
    this.outputHandlers = new FastStack<GroupOutputHandler>();
    this.renderedCrosstabLayouts = new FastStack<RenderedCrosstabLayout>();
    this.groupOutputHandlerFactory = new RenderedCrosstabOutputHandlerFactory();
    this.elementChangeChecker = new ElementChangeChecker();
  }

  protected OutputProcessorMetaData getMetaData() {
    return getRenderer().getOutputProcessor().getMetaData();
  }

  /**
   * Return the current expression value.
   * <P>
   * The value depends (obviously) on the expression implementation.
   *
   * @return the value of the function.
   */
  public Object getValue() {
    return null;
  }

  public void reportInitialized( final ReportEvent event ) {
    // there can be no pending page-start, we just have started ...
    if ( event.getState().getParentSubReportState() != null ) {
      // except if we are a subreport, of course ..
      clearPendingPageStart( event );
    }

    // activating this state after the page has ended is invalid.
    setCurrentEvent( event );
    try {
      // activating this state after the page has ended is invalid.
      final ReportDefinition report = event.getReport();
      if ( event.getState().isSubReportEvent() == false ) {
        renderer.startReport( report, getRuntime().getProcessingContext(), event.getState()
            .getPerformanceMonitorContext() );

        final ReportState reportState = event.getState();
        final ExpressionRuntime runtime = getRuntime();
        try {
          reportState.firePageStartedEvent( reportState.getEventCode() );
        } finally {
          // restore the current event, as the page-started event will clear it ..
          setRuntime( runtime );
          setCurrentEvent( event );
        }
      } else {
        renderer.startSubReport( report, event.getState().getCurrentSubReportMarker().getInsertationPointId() );
      }
    } catch ( final InvalidReportStateException fe ) {
      throw fe;
    } catch ( final Exception e ) {
      throw new InvalidReportStateException( "ReportInitialized failed", e );
    } finally {
      clearCurrentEvent();
    }
  }

  /**
   * Receives notification that the report has started. Also invokes the start of the first page ...
   * <P>
   * Layout and draw the report header after the PageStartEvent was fired.
   *
   * @param event
   *          the event.
   */
  public void reportStarted( final ReportEvent event ) {
    clearPendingPageStart( event );

    // activating this state after the page has ended is invalid.
    setCurrentEvent( event );
    try {
      // activating this state after the page has ended is invalid.
      updateFooterArea( event );

      final ReportDefinition report = event.getReport();

      renderer.startSection( Renderer.SectionType.NORMALFLOW );
      print( getRuntime(), report.getReportHeader() );
      addSubReportMarkers( renderer.endSection() );

      printDesigntimeHeader( event );

    } catch ( final InvalidReportStateException fe ) {
      throw fe;
    } catch ( final Exception e ) {
      throw new InvalidReportStateException( "ReportStarted failed", e );
    } finally {
      clearCurrentEvent();
    }
  }

  protected void printDesigntimeHeader( final ReportEvent event ) throws ReportProcessingException {
  }

  public void addSubReportMarkers( final InlineSubreportMarker[] markers ) {
    for ( int i = 0; i < markers.length; i++ ) {
      final InlineSubreportMarker marker = markers[i];
      inlineSubreports.add( marker );
    }
  }

  /**
   * Receives notification that a group has started.
   * <P>
   * Prints the GroupHeader
   *
   * @param event
   *          Information about the event.
   */
  public void groupStarted( final ReportEvent event ) {
    final int type = event.getType();

    final GroupOutputHandler groupOutputHandler = groupOutputHandlerFactory.getOutputHandler( event, beginOfRow );
    outputHandlers.push( groupOutputHandler );
    if ( ( type & ReportEvent.CROSSTABBING_ROW ) == ReportEvent.CROSSTABBING_ROW ) {
      beginOfRow = event.getState().getCurrentRow();
    }

    clearPendingPageStart( event );

    // activating this state after the page has ended is invalid.
    setCurrentEvent( event );
    try {
      final GroupOutputHandler handler = outputHandlers.peek();
      handler.groupStarted( this, event );
    } catch ( final InvalidReportStateException fe ) {
      throw fe;
    } catch ( final Exception e ) {
      throw new InvalidReportStateException( "GroupStarted failed", e );
    } finally {
      clearCurrentEvent();
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
    clearPendingPageStart( event );

    setCurrentEvent( event );

    try {
      final GroupOutputHandler handler = outputHandlers.peek();
      handler.itemsStarted( this, event );
    } catch ( final InvalidReportStateException fe ) {
      throw fe;
    } catch ( final Exception e ) {
      throw new InvalidReportStateException( "ItemsStarted failed", e );
    } finally {
      clearCurrentEvent();
    }
  }

  /**
   * Receives notification that a row of data is being processed.
   * <P>
   * prints the ItemBand.
   *
   * @param event
   *          Information about the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    clearPendingPageStart( event );

    setCurrentEvent( event );
    try {
      final GroupOutputHandler handler = outputHandlers.peek();
      handler.itemsAdvanced( this, event );
    } catch ( final InvalidReportStateException fe ) {
      throw fe;
    } catch ( final Exception e ) {
      throw new InvalidReportStateException( "ItemsAdvanced failed", e );
    } finally {
      clearCurrentEvent();
    }
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
    clearPendingPageStart( event );

    setCurrentEvent( event );

    try {
      final GroupOutputHandler handler = outputHandlers.peek();
      handler.itemsFinished( this, event );
    } catch ( final InvalidReportStateException fe ) {
      throw fe;
    } catch ( final Exception e ) {
      throw new InvalidReportStateException( "ItemsFinished failed", e );
    } finally {
      clearCurrentEvent();
    }
  }

  public void groupBodyFinished( final ReportEvent event ) {
    clearPendingPageStart( event );

    setCurrentEvent( event );
    try {
      final GroupOutputHandler handler = outputHandlers.peek();
      handler.groupBodyFinished( this, event );
    } catch ( final InvalidReportStateException fe ) {
      throw fe;
    } catch ( final Exception e ) {
      throw new InvalidReportStateException( "GroupBody failed", e );
    } finally {
      clearCurrentEvent();
    }
  }

  /**
   * Receives notification that a group has finished.
   * <P>
   * Prints the GroupFooter.
   *
   * @param event
   *          Information about the event.
   */
  public void groupFinished( final ReportEvent event ) {
    clearPendingPageStart( event );

    setCurrentEvent( event );
    try {
      final GroupOutputHandler handler = outputHandlers.pop();
      handler.groupFinished( this, event );
    } catch ( final InvalidReportStateException fe ) {
      throw fe;
    } catch ( final Exception e ) {
      throw new InvalidReportStateException( "GroupFinished failed", e );
    } finally {
      clearCurrentEvent();
    }
  }

  public void summaryRowSelection( final ReportEvent event ) {
    clearPendingPageStart( event );

    setCurrentEvent( event );

    try {
      if ( ( event.getType() & ReportEvent.SUMMARY_ROW_START ) == ReportEvent.SUMMARY_ROW_START ) {
        final GroupOutputHandler handler = new CrosstabRowOutputHandler();
        outputHandlers.push( handler );
        handler.summaryRowStart( this, event );
      } else if ( ( event.getType() & ReportEvent.SUMMARY_ROW_END ) == ReportEvent.SUMMARY_ROW_END ) {
        final GroupOutputHandler handler = outputHandlers.pop();
        handler.summaryRowEnd( this, event );
      } else {
        final GroupOutputHandler handler = outputHandlers.peek();
        handler.summaryRow( this, event );
      }
    } catch ( final InvalidReportStateException fe ) {
      throw fe;
    } catch ( final Exception e ) {
      throw new InvalidReportStateException( "Summary Row Selection event failed", e );
    } finally {
      clearCurrentEvent();
    }
  }

  /**
   * Receives notification that the report has finished.
   * <P>
   * Prints the ReportFooter and forces the last pagebreak.
   *
   * @param event
   *          Information about the event.
   */
  public void reportFinished( final ReportEvent event ) {
    clearPendingPageStart( event );

    setCurrentEvent( event );
    try {
      // a deep traversing event means, we are in a subreport ..

      // force that this last pagebreak ... (This is an indicator for the
      // pagefooter's print-on-last-page) This is highly unclean and may or
      // may not work ..
      renderer.startSection( Renderer.SectionType.NORMALFLOW );
      print( getRuntime(), event.getReport().getReportFooter() );
      addSubReportMarkers( renderer.endSection() );

      if ( event.isDeepTraversing() == false ) {
        lastPagebreak = true;
      }
      updateFooterArea( event );

      printDesigntimeFooter( event );
    } catch ( final InvalidReportStateException fe ) {
      throw fe;
    } catch ( final Exception e ) {
      throw new InvalidReportStateException( "ReportFinished failed", e );
    } finally {
      clearCurrentEvent();
    }
  }

  protected void printDesigntimeFooter( final ReportEvent event ) throws ReportProcessingException {
  }

  /**
   * Receives notification that report generation has completed, the report footer was printed, no more output is done.
   * This is a helper event to shut down the output service.
   *
   * @param event
   *          The event.
   */
  public void reportDone( final ReportEvent event ) {
    if ( event.getState().isSubReportEvent() == false ) {
      renderer.endReport();
    } else {
      renderer.endSubReport();
    }

    printPerformanceStats();
  }

  protected void printPerformanceStats() {
    elementChangeChecker.reportCachePerformance();
    logger.info( String.format(
        "Performance: footer-printed=%d footer-avoided=%d repeating-footer-printed=%d repeating-footer-avoided=%d",
        printedFooter, avoidedFooter, printedRepeatingFooter, avoidedRepeatingFooter ) );
  }

  private static LayoutExpressionRuntime createRuntime( final MasterDataRow masterRow, final ReportState state,
      final ProcessingContext processingContext ) {
    final TableModel reportDataModel = masterRow.getReportData();
    return new LayoutExpressionRuntime( masterRow.getGlobalView(), masterRow.getDataSchema(), state, reportDataModel,
        processingContext );
  }

  private static LayouterLevel[] collectSubReportStates( final ReportState state,
      final ProcessingContext processingContext ) {
    if ( processingContext == null ) {
      throw new NullPointerException();
    }
    ReportState parentState = state.getParentSubReportState();
    if ( parentState == null ) {
      return EMPTY_LAYOUTER_LEVEL;
    }

    MasterDataRow dataRow = state.getFlowController().getMasterRow();
    dataRow = dataRow.getParentDataRow();
    if ( dataRow == null ) {
      throw new IllegalStateException( "Parent-DataRow in a subreport-state must be defined." );
    }

    final ArrayList<LayouterLevel> stack = new ArrayList<LayouterLevel>();
    while ( parentState != null ) {
      if ( parentState.isInlineProcess() == false ) {
        final LayoutExpressionRuntime runtime = createRuntime( dataRow, parentState, processingContext );
        stack.add( new LayouterLevel( parentState.getReport(), parentState.getPresentationGroupIndex(), runtime,
            parentState.isInItemGroup() ) );
      }
      parentState = parentState.getParentSubReportState();
      dataRow = dataRow.getParentDataRow();
      if ( dataRow == null ) {
        throw new IllegalStateException( "Parent-DataRow in a subreport-state must be defined." );
      }
    }
    return stack.toArray( new LayouterLevel[stack.size()] );
  }

  private int computeCurrentPage() {
    return renderer.getPageCount() + 1;
  }

  private boolean isPageHeaderPrinting( final Band b, final boolean testSticky ) {
    final StyleSheet resolverStyleSheet = b.getComputedStyle();
    if ( resolverStyleSheet == null ) {
      throw new InvalidReportStateException( "Inv" );
    }
    if ( isDesignTime() ) {
      return true;
    }

    if ( testSticky && resolverStyleSheet.getBooleanStyleProperty( BandStyleKeys.STICKY ) == false ) {
      return false;
    }

    final boolean displayOnFirstPage = resolverStyleSheet.getBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_FIRSTPAGE );
    if ( computeCurrentPage() == 1 && displayOnFirstPage == false ) {
      return false;
    }

    final boolean displayOnLastPage = resolverStyleSheet.getBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_LASTPAGE );
    if ( isLastPagebreak() && ( displayOnLastPage == false ) ) {
      return false;
    }

    return true;
  }

  protected boolean isLastPagebreak() {
    return lastPagebreak;
  }

  /**
   * Receives notification that a page has started.
   * <P>
   * This prints the PageHeader. If this is the first page, the header is not printed if the pageheader style-flag
   * DISPLAY_ON_FIRSTPAGE is set to false. If this event is known to be the last pageStarted event, the
   * DISPLAY_ON_LASTPAGE is evaluated and the header is printed only if this flag is set to TRUE.
   * <p/>
   * If there is an active repeating GroupHeader, print the last one. The GroupHeader is searched for the current group
   * and all parent groups, starting at the current group and ascending to the parents. The first goupheader that has
   * the StyleFlag REPEAT_HEADER set to TRUE is printed.
   * <p/>
   * The PageHeader and the repeating GroupHeader are spooled until the first real content is printed. This way, the
   * LogicalPage remains empty until an other band is printed.
   *
   * @param event
   *          Information about the event.
   */
  public void pageStarted( final ReportEvent event ) {
    // activating this state after the page has ended is invalid.
    setCurrentEvent( event );
    try {
      final int mask = ReportEvent.REPORT_INITIALIZED | ReportEvent.NO_PARENT_PASSING_EVENT;
      if ( event.getState().isSubReportEvent() && ( event.getType() & mask ) == mask ) {
        // if this is the artificial subreport-page-start event that is fired from the
        // init-report event handler, then do not rebuild the header if the page is not empty.
        if ( renderer.isCurrentPageEmpty() == false
            || renderer.validatePages() == Renderer.LayoutResult.LAYOUT_UNVALIDATABLE ) {
          return;
        }
      }
      renderer.newPageStarted();
      clearedFooter = true;
      updateHeaderArea( event.getState() );
    } catch ( final InvalidReportStateException fe ) {
      throw fe;
    } catch ( final Exception e ) {
      throw new InvalidReportStateException( "PageStarted failed", e );
    } finally {
      clearCurrentEvent();
    }
  }

  protected void updateHeaderArea( final ReportState givenState ) throws ReportProcessingException {
    ReportState state = givenState;
    while ( state != null && state.isInlineProcess() ) {
      state = state.getParentSubReportState();
    }
    if ( state == null ) {
      return;
    }

    final ProcessingContext processingContext = getRuntime().getProcessingContext();
    final ReportDefinition report = state.getReport();
    LayouterLevel[] levels = null;
    ExpressionRuntime runtime = null;
    final OutputProcessorMetaData metaData = renderer.getOutputProcessor().getMetaData();
    if ( metaData.isFeatureSupported( OutputProcessorFeature.WATERMARK_SECTION ) ) {
      renderer.startSection( Renderer.SectionType.WATERMARK );
      // a new page has started, so reset the cursor ...
      // Check the subreport for sticky watermarks ...
      levels = DefaultOutputFunction.collectSubReportStates( state, processingContext );

      runtime = updateWatermark( state, processingContext, report, levels, runtime );
      addSubReportMarkers( renderer.endSection() );
    }

    if ( metaData.isFeatureSupported( OutputProcessorFeature.PAGE_SECTIONS ) ) {
      renderer.startSection( Renderer.SectionType.HEADER );
      // after printing the watermark, we are still at the top of the page.

      if ( levels == null ) {
        levels = DefaultOutputFunction.collectSubReportStates( state, processingContext );
      }

      runtime = updatePageHeader( state, processingContext, report, levels, runtime );
      runtime = updateRepeatingGroupHeader( state, processingContext, report, levels, runtime );
      updateDetailsHeader( state, processingContext, report, runtime );

      addSubReportMarkers( renderer.endSection() );
    }
    // mark the current position to calculate the maxBand-Height
  }

  protected ExpressionRuntime updateWatermark( final ReportState state, final ProcessingContext processingContext,
      final ReportDefinition report, final LayouterLevel[] levels, ExpressionRuntime runtime )
    throws ReportProcessingException {
    for ( int i = levels.length - 1; i >= 0; i -= 1 ) {
      final LayouterLevel level = levels[i];
      final ReportDefinition def = level.getReportDefinition();
      final Watermark watermark = def.getWatermark();
      if ( isPageHeaderPrinting( watermark, true ) ) {
        print( level.getRuntime(), watermark );
      }
    }

    // and finally print the watermark of the subreport itself ..
    final Band watermark = report.getWatermark();
    if ( isPageHeaderPrinting( watermark, false ) ) {
      runtime = createRuntime( state.getFlowController().getMasterRow(), state, processingContext );
      print( runtime, watermark );
    }
    return runtime;
  }

  protected ExpressionRuntime updatePageHeader( final ReportState state, final ProcessingContext processingContext,
      final ReportDefinition report, final LayouterLevel[] levels, ExpressionRuntime runtime )
    throws ReportProcessingException {
    for ( int i = levels.length - 1; i >= 0; i -= 1 ) {
      // This is propably wrong (or at least incomplete) in case a subreport uses header or footer which should
      // not be printed with the report-footer or header ..
      final LayouterLevel level = levels[i];
      final ReportDefinition def = level.getReportDefinition();
      final PageHeader header = def.getPageHeader();

      if ( isPageHeaderPrinting( header, true ) ) {
        print( level.getRuntime(), header );
      }
    }

    // and print the ordinary page header ..
    final Band b = report.getPageHeader();
    if ( isPageHeaderPrinting( b, false ) ) {
      if ( runtime == null ) {
        runtime = createRuntime( state.getFlowController().getMasterRow(), state, processingContext );
      }
      print( runtime, b );
    }
    return runtime;
  }

  protected ExpressionRuntime updateRepeatingGroupHeader( final ReportState state,
      final ProcessingContext processingContext, final ReportDefinition report, final LayouterLevel[] levels,
      ExpressionRuntime runtime ) throws ReportProcessingException {
    if ( isDesignTime() ) {
      return runtime;
    }
    /**
     * Dive into the pending group to print the group header ...
     */

    for ( int i = levels.length - 1; i >= 0; i -= 1 ) {
      final LayouterLevel level = levels[i];
      final ReportDefinition def = level.getReportDefinition();

      for ( int gidx = 0; gidx <= level.getGroupIndex(); gidx++ ) {
        final Group g = def.getGroup( gidx );
        if ( g instanceof RelationalGroup ) {
          final RelationalGroup rg = (RelationalGroup) g;
          final GroupHeader header = rg.getHeader();
          if ( isGroupSectionPrintableInternal( header, true, true ) ) {
            print( level.getRuntime(), header );
          }
        }
      }

      if ( level.isInItemGroup() ) {
        final DetailsHeader detailsHeader = def.getDetailsHeader();
        if ( detailsHeader != null && isGroupSectionPrintableInternal( detailsHeader, true, true ) ) {
          print( level.getRuntime(), detailsHeader );
        }
      }
    }

    final int groupsPrinted = state.getPresentationGroupIndex();
    for ( int gidx = 0; gidx <= groupsPrinted; gidx++ ) {
      final Group g = report.getGroup( gidx );
      if ( g instanceof RelationalGroup ) {
        final RelationalGroup rg = (RelationalGroup) g;
        final GroupHeader header = rg.getHeader();
        if ( isGroupSectionPrintableInternal( header, false, true ) ) {
          if ( runtime == null ) {
            runtime = createRuntime( state.getFlowController().getMasterRow(), state, processingContext );
          }
          print( runtime, header );
        }
      }
    }
    return runtime;
  }

  protected ExpressionRuntime updateDetailsHeader( final ReportState state, final ProcessingContext processingContext,
      final ReportDefinition report, ExpressionRuntime runtime ) throws ReportProcessingException {
    if ( isDesignTime() ) {
      return runtime;
    }

    if ( state.isInItemGroup() ) {
      final DetailsHeader detailsHeader = report.getDetailsHeader();
      if ( detailsHeader != null && isGroupSectionPrintableInternal( detailsHeader, false, true ) ) {
        if ( runtime == null ) {
          runtime = createRuntime( state.getFlowController().getMasterRow(), state, processingContext );
        }
        print( runtime, detailsHeader );
      }
    }
    return runtime;
  }

  /**
   * Receives notification that a page has ended.
   * <p/>
   * This prints the PageFooter. If this is the first page, the footer is not printed if the pagefooter style-flag
   * DISPLAY_ON_FIRSTPAGE is set to false. If this event is known to be the last pageFinished event, the
   * DISPLAY_ON_LASTPAGE is evaluated and the footer is printed only if this flag is set to TRUE.
   * <p/>
   *
   * @param event
   *          the report event.
   */
  public void pageFinished( final ReportEvent event ) {
    setCurrentEvent( event );
    try {
      updateFooterArea( event );
    } catch ( final InvalidReportStateException fe ) {
      throw fe;
    } catch ( final Exception e ) {
      throw new InvalidReportStateException( "PageFinished failed", e );
    } finally {
      clearCurrentEvent();
    }
  }

  public void updateFooterArea( final ReportEvent event ) throws ReportProcessingException {
    final OutputProcessorMetaData metaData = renderer.getOutputProcessor().getMetaData();
    if ( metaData.isFeatureSupported( OutputProcessorFeature.PAGE_SECTIONS ) == false ) {
      return;
    }
    if ( event.getState().isInlineProcess() ) {
      return;
    }

    final LayouterLevel[] levels =
        DefaultOutputFunction.collectSubReportStates( event.getState(), getRuntime().getProcessingContext() );
    if ( isSubReportConfigurationChanged( levels ) ) {
      clearedFooter = true;
      refreshSubReportFooterConfiguration( levels );
    }
    updateRepeatingFooters( event, levels );
    updatePageFooter( event, levels );
    clearedFooter = false;
  }

  private void refreshSubReportFooterConfiguration( final LayouterLevel[] levels ) {
    subReportFooterTracker.clear();
    for ( final LayouterLevel level : levels ) {
      subReportFooterTracker.add( level.getReportDefinition().getObjectID() );
    }
  }

  private boolean isSubReportConfigurationChanged( final LayouterLevel[] levels ) {
    if ( levels.length != subReportFooterTracker.size() ) {
      return true;
    }

    for ( int i = 0; i < subReportFooterTracker.size(); i++ ) {
      InstanceID instanceID = subReportFooterTracker.get( i );
      if ( levels[i].getReportDefinition().getObjectID() != instanceID ) {
        return true;
      }
    }
    return false;
  }

  protected boolean updatePageFooter( final ReportEvent event, final LayouterLevel[] levels )
    throws ReportProcessingException {
    final ReportDefinition report = event.getReport();
    final int levelCount = levels.length;
    final DataRow dataRow = event.getDataRow();

    final PageFooter pageFooter = report.getPageFooter();
    boolean needPrinting = isPageFooterPrinting( levels, levelCount, dataRow, pageFooter );

    if ( needPrinting == false ) {
      avoidedFooter += 1;
      return false;
    }

    renderer.startSection( Renderer.SectionType.FOOTER );
    if ( isPageFooterPrintable( pageFooter, false ) ) {
      print( getRuntime(), pageFooter );
    } else {
      printEmptyRootLevelBand();
    }

    for ( int i = 0; i < levelCount; i++ ) {
      final LayouterLevel level = levels[i];
      final ReportDefinition def = level.getReportDefinition();
      final PageFooter b = def.getPageFooter();

      if ( isPageFooterPrintable( b, true ) ) {
        print( level.getRuntime(), b );
      } else {
        printEmptyRootLevelBand();
      }
    }
    addSubReportMarkers( renderer.endSection() );
    printedFooter += 1;
    return true;
  }

  private boolean isPageFooterPrinting( final LayouterLevel[] levels, final int levelCount, final DataRow dataRow,
      final PageFooter pageFooter ) {
    if ( isDesignTime() ) {
      return true;
    }

    if ( clearedFooter ) {
      return true;
    }

    if ( isPageFooterPrintable( pageFooter, false ) && elementChangeChecker.isBandChanged( pageFooter, dataRow ) ) {
      return true;
    }

    for ( int i = 0; i < levelCount; i++ ) {
      final LayouterLevel level = levels[i];
      final ReportDefinition def = level.getReportDefinition();
      final PageFooter b = def.getPageFooter();
      if ( isPageFooterPrintable( b, true ) && elementChangeChecker.isBandChanged( b, dataRow ) ) {
        return true;
      }
    }
    return false;
  }

  protected boolean updateRepeatingFooters( final ReportEvent event, final LayouterLevel[] levels )
    throws ReportProcessingException {
    final ReportDefinition report = event.getReport();
    final ReportState state = event.getState();
    final int groupsPrinted = state.getPresentationGroupIndex();
    final int levelCount = levels.length;

    final boolean needPrinting = isNeedPrintRepeatingFooter( event, levels );

    if ( needPrinting == false ) {
      avoidedRepeatingFooter += 1;
      return false;
    }

    renderer.startSection( Renderer.SectionType.REPEAT_FOOTER );

    if ( state.isInItemGroup() ) {
      final DetailsFooter footer = report.getDetailsFooter();
      if ( isGroupSectionPrintableInternal( footer, false, true ) ) {
        print( getRuntime(), footer );
      }
    }

    /**
     * Repeating group header are only printed while ItemElements are processed.
     */
    for ( int gidx = groupsPrinted; gidx >= 0; gidx -= 1 ) {
      final Group g = report.getGroup( gidx );
      if ( g instanceof RelationalGroup ) {
        final RelationalGroup rg = (RelationalGroup) g;
        final GroupFooter footer = rg.getFooter();
        if ( isGroupSectionPrintableInternal( footer, false, true ) ) {
          print( getRuntime(), footer );
        }
      }
    }

    for ( int i = 0; i < levelCount; i++ ) {
      final LayouterLevel level = levels[i];
      final ReportDefinition def = level.getReportDefinition();

      if ( level.isInItemGroup() ) {
        final DetailsFooter detailsFooter = def.getDetailsFooter();
        if ( detailsFooter != null ) {
          if ( isGroupSectionPrintableInternal( detailsFooter, true, true ) ) {
            print( level.getRuntime(), detailsFooter );
          }
        }
      }

      for ( int gidx = level.getGroupIndex(); gidx >= 0; gidx -= 1 ) {
        final Group g = def.getGroup( gidx );
        if ( g instanceof RelationalGroup ) {
          final RelationalGroup rg = (RelationalGroup) g;
          final GroupFooter footer = rg.getFooter();
          if ( isGroupSectionPrintableInternal( footer, true, true ) ) {
            print( level.getRuntime(), footer );
          }
        }
      }
    }

    addSubReportMarkers( renderer.endSection() );
    printedRepeatingFooter += 1;
    return true;
  }

  protected boolean isNeedPrintRepeatingFooter( final ReportEvent event, final LayouterLevel[] levels ) {
    final ReportDefinition report = event.getReport();
    final ReportState state = event.getState();
    final int groupsPrinted = state.getPresentationGroupIndex();
    final int levelCount = levels.length;
    final DataRow dataRow = event.getDataRow();

    if ( repeatingFooterValidator.isRepeatFooterValid( event, levels ) == false ) {
      return true;
    }

    boolean needPrinting = clearedFooter;
    if ( needPrinting == false && state.isInItemGroup() ) {
      final DetailsFooter footer = report.getDetailsFooter();
      if ( isGroupSectionPrintableInternal( footer, false, true )
          && elementChangeChecker.isBandChanged( footer, dataRow ) ) {
        needPrinting = true;
      }
    }

    /**
     * Repeating group header are only printed while ItemElements are processed.
     */
    if ( needPrinting == false ) {
      for ( int gidx = groupsPrinted; gidx >= 0; gidx -= 1 ) {
        final Group g = report.getGroup( gidx );
        if ( g instanceof RelationalGroup ) {
          final RelationalGroup rg = (RelationalGroup) g;
          final GroupFooter footer = rg.getFooter();
          if ( isGroupSectionPrintableInternal( footer, false, true )
              && elementChangeChecker.isBandChanged( footer, dataRow ) ) {
            needPrinting = true;
          }
        }
      }
    }

    if ( needPrinting == false ) {
      for ( int i = 0; i < levelCount; i++ ) {
        final LayouterLevel level = levels[i];
        final ReportDefinition def = level.getReportDefinition();

        if ( level.isInItemGroup() ) {
          final DetailsFooter detailsFooter = def.getDetailsFooter();
          if ( detailsFooter != null ) {
            if ( isGroupSectionPrintableInternal( detailsFooter, true, true )
                && elementChangeChecker.isBandChanged( detailsFooter, dataRow ) ) {
              needPrinting = true;
            }
          }
        }

        if ( needPrinting == false ) {
          for ( int gidx = level.getGroupIndex(); gidx >= 0; gidx -= 1 ) {
            final Group g = def.getGroup( gidx );
            if ( g instanceof RelationalGroup ) {
              final RelationalGroup rg = (RelationalGroup) g;
              final GroupFooter footer = rg.getFooter();
              if ( isGroupSectionPrintableInternal( footer, true, true )
                  && elementChangeChecker.isBandChanged( footer, dataRow ) ) {
                needPrinting = true;
              }
            }
          }
        }
      }
    }
    return needPrinting;
  }

  protected boolean isGroupSectionPrintableInternal( final Band b, final boolean testSticky, final boolean testRepeat ) {
    return isGroupSectionPrintable( b, testSticky, testRepeat );
  }

  public static boolean isGroupSectionPrintable( final Band b, final boolean testSticky, final boolean testRepeat ) {
    final StyleSheet resolverStyleSheet = b.getComputedStyle();
    if ( testSticky && resolverStyleSheet.getBooleanStyleProperty( BandStyleKeys.STICKY ) == false ) {
      return false;
    }

    if ( testRepeat && resolverStyleSheet.getBooleanStyleProperty( BandStyleKeys.REPEAT_HEADER ) == false ) {
      return false;
    }
    return true;
  }

  protected boolean isPageFooterPrintable( final Band b, final boolean testSticky ) {
    final StyleSheet resolverStyleSheet = b.getComputedStyle();
    if ( testSticky && resolverStyleSheet.getBooleanStyleProperty( BandStyleKeys.STICKY ) == false ) {
      return false;
    }

    if ( computeCurrentPage() == 1 ) {
      if ( resolverStyleSheet.getBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_FIRSTPAGE ) == true ) {
        return true;
      } else {
        return false;
      }
    } else if ( isLastPagebreak() ) {
      if ( resolverStyleSheet.getBooleanStyleProperty( BandStyleKeys.DISPLAY_ON_LASTPAGE ) == true ) {
        return true;
      } else {
        return false;
      }
    } else {
      return true;
    }
  }

  /**
   * Returns the current report event.
   *
   * @return the event.
   */
  protected ReportEvent getCurrentEvent() {
    return currentEvent;
  }

  /**
   * Sets the current event (also updates the report reference).
   *
   * @param currentEvent
   *          event.
   */
  protected void setCurrentEvent( final ReportEvent currentEvent ) {
    if ( currentEvent == null ) {
      throw new NullPointerException( "Event must not be null." );
    }
    this.currentEvent = currentEvent;
    this.pagebreakHandler.setReportState( currentEvent.getState() );
    this.renderer.setStateKey( currentEvent.getState().getProcessKey() );
  }

  /**
   * Clears the current event.
   */
  protected void clearCurrentEvent() {
    if ( currentEvent == null ) {
      throw new IllegalStateException( "ClearCurrentEvent called without Event set:" );
    }
    this.currentEvent = null;
    this.pagebreakHandler.setReportState( null );
    this.renderer.setStateKey( null );
  }

  /**
   * Clones the function.
   * <P>
   * Be aware, this does not create a deep copy. If you have complex strucures contained in objects, you have to
   * override this function.
   *
   * @return a clone of this function.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public final Object clone() throws CloneNotSupportedException {
    final DefaultOutputFunction sl = (DefaultOutputFunction) super.clone();
    sl.repeatingFooterValidator = repeatingFooterValidator.clone();
    sl.currentEvent = null;
    sl.inlineSubreports = (ArrayList<InlineSubreportMarker>) inlineSubreports.clone();
    sl.outputHandlers = outputHandlers.clone();
    sl.renderedCrosstabLayouts = renderedCrosstabLayouts.clone();
    sl.renderedCrosstabLayouts.clear();
    final int rSize = renderedCrosstabLayouts.size();
    for ( int i = 0; i < rSize; i++ ) {
      final RenderedCrosstabLayout o = renderedCrosstabLayouts.get( i );
      sl.renderedCrosstabLayouts.push( (RenderedCrosstabLayout) o.clone() );
    }
    return sl;
  }

  public Expression getInstance() {
    return deriveForStorage();
  }

  /**
   * Creates a storage-copy of the output function. A storage copy must create a deep clone of all referenced objects so
   * that it is guaranteed that changes to either the original or the clone do not affect the other instance.
   * <p/>
   * Any failure to implement this method correctly will be a great source of very subtle bugs.
   *
   * @return the deep clone.
   */
  public OutputFunction deriveForStorage() {
    try {
      final DefaultOutputFunction sl = (DefaultOutputFunction) super.clone();
      sl.repeatingFooterValidator = repeatingFooterValidator.clone();
      sl.renderer = renderer.deriveForStorage();
      sl.inlineSubreports = (ArrayList<InlineSubreportMarker>) inlineSubreports.clone();
      sl.currentEvent = null;
      sl.pagebreakHandler = (DefaultLayoutPagebreakHandler) pagebreakHandler.clone();
      sl.pagebreakHandler.setReportState( null );
      sl.outputHandlers = outputHandlers.clone();
      sl.renderedCrosstabLayouts = renderedCrosstabLayouts.clone();
      sl.renderedCrosstabLayouts.clear();
      final int rSize = renderedCrosstabLayouts.size();
      for ( int i = 0; i < rSize; i++ ) {
        final RenderedCrosstabLayout o = renderedCrosstabLayouts.get( i );
        sl.renderedCrosstabLayouts.push( o.derive() );
      }
      return sl;
    } catch ( final CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  /**
   * Creates a cheaper version of the deep-copy of the output function. A pagebreak-derivate is created on every
   * possible pagebreak position and must contain all undo/rollback information to restore the state of any shared
   * object when a roll-back is requested.
   * <p/>
   * Any failure to implement this method correctly will be a great source of very subtle bugs.
   *
   * @return the deep clone.
   */
  public OutputFunction deriveForPagebreak() {
    try {
      final DefaultOutputFunction sl = (DefaultOutputFunction) super.clone();
      sl.repeatingFooterValidator = repeatingFooterValidator.clone();
      sl.renderer = renderer.deriveForPagebreak();
      sl.inlineSubreports = (ArrayList<InlineSubreportMarker>) inlineSubreports.clone();
      sl.currentEvent = null;
      sl.pagebreakHandler = (DefaultLayoutPagebreakHandler) pagebreakHandler.clone();
      sl.outputHandlers = outputHandlers.clone();
      sl.renderedCrosstabLayouts = renderedCrosstabLayouts.clone();
      sl.renderedCrosstabLayouts.clear();
      final int rSize = renderedCrosstabLayouts.size();
      for ( int i = 0; i < rSize; i++ ) {
        final RenderedCrosstabLayout o = renderedCrosstabLayouts.get( i );
        sl.renderedCrosstabLayouts.push( o.derive() );
      }
      return sl;
    } catch ( final CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }

  public void setRenderer( final Renderer renderer ) {
    this.renderer = renderer;
  }

  protected boolean isDesignTime() {
    return false;
  }

  public Renderer getRenderer() {
    return renderer;
  }

  /**
   * Prints the given band at the current cursor position.
   *
   * @param dataRow
   *          the datarow for evaluating the band's value-expressions.
   * @param band
   *          the band to be printed.
   * @throws ReportProcessingException
   *           if an error occured during the layout computation.
   */
  public void print( final ExpressionRuntime dataRow, final Band band ) throws ReportProcessingException {
    renderer.add( band, dataRow );
  }

  protected void printEmptyRootLevelBand() throws ReportProcessingException {
    renderer.addEmptyRootLevelBand();
  }

  private void clearPendingPageStart( final ReportEvent event ) {
    clearPendingPageStart( event, false );
  }

  private void clearPendingPageStart( final ReportEvent event, final boolean force ) {
    pagebreakHandler.setReportState( event.getState() );
    try {
      if ( renderer.clearPendingPageStart( pagebreakHandler ) ) {
        // page started has been fired ...
        return;
      }

      if ( !force ) {
        final boolean currentPageEmpty = renderer.isCurrentPageEmpty();
        if ( currentPageEmpty == false ) {
          return;
        }

        final boolean validateResult = renderer.validatePages() != Renderer.LayoutResult.LAYOUT_UNVALIDATABLE;
        if ( validateResult == false ) {
          return;
        }
      }

      try {
        setCurrentEvent( event );
        renderer.newPageStarted();
        clearedFooter = true;
        updateHeaderArea( event.getState() );
      } finally {
        clearCurrentEvent();
      }
    } catch ( final ReportProcessingException e ) {
      throw new InvalidReportStateException( "Failed to update the page-header", e );
    } catch ( final ContentProcessingException e ) {
      throw new InvalidReportStateException( "Failed to update the page-header", e );
    } finally {
      pagebreakHandler.setReportState( null );
    }
  }

  public InlineSubreportMarker[] getInlineSubreports() {
    if ( inlineSubreports.isEmpty() ) {
      return EMPTY_INLINE_SUBREPORT_MARKERS;
    }
    return inlineSubreports.toArray( new InlineSubreportMarker[inlineSubreports.size()] );
  }

  public void clearInlineSubreports( final SubReportProcessType inlineExecution ) {
    final InlineSubreportMarker[] subreports = getInlineSubreports();
    for ( int i = subreports.length - 1; i >= 0; i-- ) {
      final InlineSubreportMarker subreport = subreports[i];
      if ( inlineExecution == subreport.getProcessType() ) {
        inlineSubreports.remove( i );
      }
    }
  }

  public RenderedCrosstabLayout startRenderedCrosstabLayout() {
    final RenderedCrosstabLayout layout = new RenderedCrosstabLayout();
    renderedCrosstabLayouts.push( layout );
    return layout;
  }

  public RenderedCrosstabLayout getCurrentRenderedCrosstabLayout() {
    return renderedCrosstabLayouts.peek();
  }

  public void endRenderedCrosstabLayout() {
    renderedCrosstabLayouts.pop();
  }

  public void restart( final ReportState state ) throws ReportProcessingException {
    final ReportEvent event = new ReportEvent( state, state.getEventCode() );
    clearPendingPageStart( event, true );
  }

  public boolean createRollbackInformation() {
    final Renderer commitableRenderer = getRenderer();
    commitableRenderer.createRollbackInformation();
    return true;
  }
}
