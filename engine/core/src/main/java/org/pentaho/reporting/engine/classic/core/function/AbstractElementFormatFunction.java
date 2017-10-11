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

package org.pentaho.reporting.engine.classic.core.function;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CrosstabCell;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroupBody;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.GroupBody;
import org.pentaho.reporting.engine.classic.core.ItemBand;
import org.pentaho.reporting.engine.classic.core.ReportDefinition;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubGroupBody;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.event.PageEventListener;
import org.pentaho.reporting.engine.classic.core.event.ReportEvent;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorFeature;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.states.LayoutProcess;
import org.pentaho.reporting.engine.classic.core.states.ReportState;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * The AbstractElementFormatFunction provides a common base implementation for all functions that need to modify the
 * report definition or the style of an report element or band during the report processing.
 * <p/>
 * The Expression retrieves the next root-level band that will be printed and uses this band as parameter for the
 * {@link AbstractElementFormatFunction#processRootBand(org.pentaho.reporting.engine.classic.core.Section)} method.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractElementFormatFunction extends AbstractFunction implements PageEventListener,
    LayoutProcessorFunction {
  private static class NeedEvalResult {
    private boolean needToRun;
    private long changeTracker;

    private NeedEvalResult( final boolean needToRun, final long changeTracker ) {
      this.needToRun = needToRun;
      this.changeTracker = changeTracker;
    }

    public boolean isNeedToRun() {
      return needToRun;
    }

    public long getChangeTracker() {
      return changeTracker;
    }

    public String toString() {
      return "NeedEvalResult{" + "needToRun=" + needToRun + ", changeTracker=" + changeTracker + '}';
    }
  }

  private static class PerformanceCollector implements Serializable {
    public int totalEvaluations;
    public int evaluations;
    public int skippedEvaluations;
  }

  private PerformanceCollector performanceCollector;
  private final Log performanceLogger = LogFactory.getLog( getClass() );
  /**
   * The name of the element that should be formatted.
   */
  private String element;
  private transient String attrName;

  /**
   * Creates an unnamed function. Make sure the name of the function is set using {@link #setName} before the function
   * is added to the report's function collection.
   */
  protected AbstractElementFormatFunction() {
    attrName = computeUniqueIdentifier();
  }

  /**
   * Sets the element name. The name denotes an element or band within the root-band or the root-band itself. It is
   * possible to define multiple elements with the same name to apply the modification to all of these elements.
   *
   * @param name
   *          The element name.
   * @see org.pentaho.reporting.engine.classic.core.function.FunctionUtilities#findAllElements(org.pentaho.reporting
   *      .engine.classic.core.Band, String)
   */
  public void setElement( final String name ) {
    this.element = name;
  }

  /**
   * Returns the element name.
   *
   * @return The element name.
   * @see #setElement(String)
   */
  public String getElement() {
    return element;
  }

  /**
   * Receives notification that report generation initializes the current run.
   * <P>
   * The event carries a ReportState.Started state. Use this to initialize the report.
   *
   * @param event
   *          The event.
   */
  public void reportInitialized( final ReportEvent event ) {
    if ( FunctionUtilities.isLayoutLevel( event ) == false ) {
      // dont do anything if there is no printing done ...
      return;
    }

    performanceCollector = new PerformanceCollector();

    if ( isExecutable() == false ) {
      return;
    }

    if ( event.getState().isSubReportEvent() == false ) {
      evaluateElement( event.getReport() );
    }
    processRootBand( event.getReport().getPageHeader() );
    processRootBand( event.getReport().getWatermark() );
  }

  /**
   * Processes the Report-Header.
   *
   * @param event
   *          the event.
   */
  public void reportStarted( final ReportEvent event ) {
    if ( FunctionUtilities.isLayoutLevel( event ) == false ) {
      // dont do anything if there is no printing done ...
      return;
    }
    if ( isExecutable() == false ) {
      return;
    }

    final Band b = event.getReport().getReportHeader();
    processRootBand( b );

    processFooterBands( event.getState() );
  }

  /**
   * Processes the group header of the current group.
   *
   * @param event
   *          the event.
   */
  public void groupStarted( final ReportEvent event ) {
    if ( FunctionUtilities.isLayoutLevel( event ) == false ) {
      // dont do anything if there is no printing done ...
      return;
    }
    if ( isExecutable() == false ) {
      return;
    }

    final Group group = FunctionUtilities.getCurrentGroup( event );
    evaluateElement( group );
    processGroupHeaders( group );
    evaluateElement( group.getBody() );

    processFooterBands( event.getState() );
  }

  /**
   * Processes the No-Data-Band.
   *
   * @param event
   *          the report event.
   */
  public void itemsStarted( final ReportEvent event ) {
    if ( FunctionUtilities.isLayoutLevel( event ) == false ) {
      // dont do anything if there is no printing done ...
      return;
    }

    if ( isExecutable() == false ) {
      return;
    }

    if ( event.getState().isCrosstabActive() ) {
      final CrosstabCellBody crosstabCellBody = event.getReport().getCrosstabCellBody();
      processRootBand( crosstabCellBody.getHeader() );
      processRootBand( crosstabCellBody.findElement( null, null ) );
    } else {
      final ReportDefinition reportDefinition = event.getReport();
      processRootBand( reportDefinition.getDetailsHeader() );
      processRootBand( reportDefinition.getNoDataBand() );
    }
    processFooterBands( event.getState() );
  }

  /**
   * Processes the ItemBand.
   *
   * @param event
   *          the event.
   */
  public void itemsAdvanced( final ReportEvent event ) {
    if ( FunctionUtilities.isLayoutLevel( event ) == false ) {
      // dont do anything if there is no printing done ...
      return;
    }
    if ( isExecutable() == false ) {
      return;
    }

    if ( event.getState().isCrosstabActive() ) {
      final CrosstabCellBody crosstabCellBody = event.getReport().getCrosstabCellBody();
      processRootBand( crosstabCellBody.findElement( null, null ) );
    } else {
      final ItemBand itemBand = event.getReport().getItemBand();
      processRootBand( itemBand );
    }
    processFooterBands( event.getState() );
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
    if ( FunctionUtilities.isLayoutLevel( event ) == false ) {
      // dont do anything if there is no printing done ...
      return;
    }
    if ( isExecutable() == false ) {
      return;
    }

    if ( event.getState().isCrosstabActive() ) {
      final CrosstabCellBody crosstabCellBody = event.getReport().getCrosstabCellBody();
      processRootBand( crosstabCellBody.findElement( null, null ) );
    } else {
      processRootBand( event.getReport().getDetailsFooter() );
    }
    processFooterBands( event.getState() );
  }

  /**
   * Processes the group footer of the current group.
   *
   * @param event
   *          the event.
   */
  public void groupFinished( final ReportEvent event ) {
    if ( FunctionUtilities.isLayoutLevel( event ) == false ) {
      // dont do anything if there is no printing done ...
      return;
    }
    if ( isExecutable() == false ) {
      return;
    }

    final Group group = FunctionUtilities.getCurrentGroup( event );
    if ( group instanceof CrosstabColumnGroup ) {
      final CrosstabCellBody crosstabCellBody = event.getReport().getCrosstabCellBody();
      final int elementCount = crosstabCellBody.getElementCount();
      for ( int i = 1; i < elementCount; i += 1 ) {
        final CrosstabCell cell = (CrosstabCell) crosstabCellBody.getElement( i );
        if ( cell.getRowField() == null ) {
          processRootBand( cell );
        }
      }
    } else if ( group instanceof CrosstabRowGroup ) {
      final CrosstabRowGroup rowGroup = (CrosstabRowGroup) group;
      final CrosstabCellBody crosstabCellBody = event.getReport().getCrosstabCellBody();
      final int elementCount = crosstabCellBody.getElementCount();
      for ( int i = 1; i < elementCount; i += 1 ) {
        final CrosstabCell cell = (CrosstabCell) crosstabCellBody.getElement( i );
        if ( ObjectUtilities.equal( cell.getRowField(), rowGroup.getField() ) ) {
          processRootBand( cell );
        }
      }
    } else {
      processAllGroupFooterBands( group );
    }

    processFooterBands( event.getState() );
  }

  /**
   * Processes the Report-Footer.
   *
   * @param event
   *          the event.
   */
  public void reportFinished( final ReportEvent event ) {
    if ( FunctionUtilities.isLayoutLevel( event ) == false ) {
      // dont do anything if there is no printing done ...
      return;
    }
    if ( isExecutable() == false ) {
      return;
    }

    final Band b = event.getReport().getReportFooter();
    processRootBand( b );
    processFooterBands( event.getState() );
  }

  public void reportDone( final ReportEvent event ) {
    if ( FunctionUtilities.isLayoutLevel( event ) == false ) {
      // dont do anything if there is no printing done ...
      return;
    }

    final OutputProcessorMetaData outputProcessorMetaData =
        getRuntime().getProcessingContext().getOutputProcessorMetaData();
    if ( outputProcessorMetaData.isFeatureSupported( OutputProcessorFeature.DESIGNTIME ) == false ) {
      reportCachePerformance();
    }
  }

  protected void reportCachePerformance() {
    if ( performanceLogger.isInfoEnabled() ) {
      performanceLogger.info( String.format( "Performance: %s => total=%d, evaluated=%d (%f%%), avoided=%d (%f%%)",
          getClass(), performanceCollector.totalEvaluations, performanceCollector.evaluations, 100f
              * performanceCollector.evaluations / Math.max( 1.0f, performanceCollector.totalEvaluations ),
          performanceCollector.skippedEvaluations, 100f * performanceCollector.skippedEvaluations
              / Math.max( 1.0f, performanceCollector.totalEvaluations ) ) );
    }
  }

  protected void processGroupHeaders( final Group group ) {
    final int elementCount = group.getElementCount();
    for ( int i = 0; i < elementCount; i += 1 ) {
      final Element e = group.getElement( i );
      final ElementMetaData.TypeClassification reportElementType = e.getMetaData().getReportElementType();
      if ( ( reportElementType != ElementMetaData.TypeClassification.RELATIONAL_HEADER )
          && ( reportElementType != ElementMetaData.TypeClassification.HEADER ) ) {
        continue;
      }

      final Band b = (Band) e;
      processRootBand( b );
    }
  }

  private void processAllGroupFooterBands( final Group group ) {
    final int elementCount = group.getElementCount();
    for ( int i = 0; i < elementCount; i += 1 ) {
      final Element e = group.getElement( i );
      final ElementMetaData.TypeClassification reportElementType = e.getMetaData().getReportElementType();
      if ( ( reportElementType != ElementMetaData.TypeClassification.RELATIONAL_FOOTER )
          && ( reportElementType != ElementMetaData.TypeClassification.FOOTER ) ) {
        continue;
      }

      final Band b = (Band) e;
      processRootBand( b );
    }
  }

  /**
   * Processes the page footer.
   *
   * @param event
   *          the event.
   */
  public void pageFinished( final ReportEvent event ) {
    if ( FunctionUtilities.isLayoutLevel( event ) == false ) {
      // dont do anything if there is no printing done ...
      return;
    }
    if ( isExecutable() == false ) {
      return;
    }

    final Band b = event.getReport().getPageFooter();
    processRootBand( b );
  }

  /**
   * Processes the page header.
   *
   * @param event
   *          the event.
   */
  public void pageStarted( final ReportEvent event ) {
    if ( FunctionUtilities.isLayoutLevel( event ) == false ) {
      // dont do anything if there is no printing done ...
      return;
    }

    if ( isExecutable() == false ) {
      return;
    }

    if ( performanceCollector == null ) {
      // this is part of a bug. LayoutProcessorFunctions seem to get informed of page-started before they receive
      // a reportInitialized event.
      performanceCollector = new PerformanceCollector();
    }

    final Band w = event.getReport().getWatermark();
    processRootBand( w );

    processHeaderBands( event.getState() );
    processFooterBands( event.getState() );
  }

  protected void processFooterBands( ReportState state ) {
    while ( state != null ) {
      final ReportDefinition reportDefinition = state.getReport();
      processRootBand( reportDefinition.getPageFooter() );
      if ( state.isInItemGroup() ) {
        processRootBand( reportDefinition.getDetailsFooter() );
      }
      Group g = reportDefinition.getRootGroup();
      int groupCounter = 0;
      while ( g != null && groupCounter <= state.getCurrentGroupIndex() ) {
        processAllGroupFooterBands( g );

        final GroupBody body = g.getBody();
        if ( body instanceof SubGroupBody ) {
          groupCounter += 1;
          final SubGroupBody sgb = (SubGroupBody) body;
          g = sgb.getGroup();
        } else if ( body instanceof CrosstabOtherGroupBody ) {
          groupCounter += 1;
          final CrosstabOtherGroupBody sgb = (CrosstabOtherGroupBody) body;
          g = sgb.getGroup();
        } else {
          break;
        }
      }

      state = state.getParentSubReportState();
    }

  }

  protected void processHeaderBands( ReportState state ) {
    while ( state != null ) {
      final ReportDefinition reportDefinition = state.getReport();
      processRootBand( reportDefinition.getPageHeader() );
      if ( state.isInItemGroup() ) {
        processRootBand( reportDefinition.getDetailsHeader() );
      }
      Group g = reportDefinition.getRootGroup();
      int groupCounter = 0;
      while ( g != null && groupCounter <= state.getCurrentGroupIndex() ) {
        processGroupHeaders( g );

        final GroupBody body = g.getBody();
        if ( body instanceof SubGroupBody ) {
          groupCounter += 1;
          final SubGroupBody sgb = (SubGroupBody) body;
          g = sgb.getGroup();
        } else if ( body instanceof CrosstabOtherGroupBody ) {
          groupCounter += 1;
          final CrosstabOtherGroupBody sgb = (CrosstabOtherGroupBody) body;
          g = sgb.getGroup();
        } else {
          break;
        }
      }

      state = state.getParentSubReportState();
    }

  }

  /**
   * Format-Functions usually are not expected to return anything.
   *
   * @return null, as format functions do not compute values.
   */
  public Object getValue() {
    return null;
  }

  protected boolean isExecutable() {
    return true;
  }

  /**
   * Evaluates all style expressions from all elements and updates the style-sheet if needed.
   *
   * @param b
   *          the band.
   */
  protected final void processRootBand( final Section b ) {
    if ( b == null ) {
      return;
    }

    final NeedEvalResult needToRun = (NeedEvalResult) b.getAttribute( AttributeNames.Internal.NAMESPACE, attrName );
    if ( needToRun != null ) {
      if ( needToRun.isNeedToRun() == false ) {
        if ( b.getChangeTracker() == needToRun.getChangeTracker() ) {
          recordCacheHit( b );
          return;
        }
      }
    }

    recordCacheMiss( b );

    final boolean needToRunVal = processBand( b );
    b.setAttribute( AttributeNames.Internal.NAMESPACE, attrName,
        new NeedEvalResult( needToRunVal, b.getChangeTracker() ), false );
  }

  protected void recordCacheHit( final ReportElement e ) {
    performanceCollector.totalEvaluations += 1;
    performanceCollector.skippedEvaluations += 1;
  }

  protected void recordCacheMiss( final ReportElement e ) {
    performanceCollector.totalEvaluations += 1;
    performanceCollector.evaluations += 1;
  }

  protected abstract boolean evaluateElement( final ReportElement e );

  protected final boolean processBand( final Section b ) {
    boolean hasAttrExpressions = evaluateElement( b );

    final int length = b.getElementCount();
    for ( int i = 0; i < length; i++ ) {
      final Element element = b.getElement( i );

      final ElementMetaData.TypeClassification reportElementType = element.getMetaData().getReportElementType();
      if ( reportElementType == ElementMetaData.TypeClassification.DATA
          || reportElementType == ElementMetaData.TypeClassification.CONTROL
          || reportElementType == ElementMetaData.TypeClassification.SUBREPORT || element instanceof Section == false ) {
        if ( evaluateElement( element ) ) {
          hasAttrExpressions = true;
        }
      } else {
        final Section section = (Section) element;
        if ( processBand( section ) ) {
          hasAttrExpressions = true;
        }
      }
    }

    if ( b instanceof RootLevelBand ) {
      final RootLevelBand rlb = (RootLevelBand) b;
      final SubReport[] reports = rlb.getSubReports();
      for ( int i = 0; i < reports.length; i++ ) {
        final SubReport subReport = reports[i];
        if ( evaluateElement( subReport ) ) {
          hasAttrExpressions = true;
        }
      }
    }
    return hasAttrExpressions;
  }

  public final int getDependencyLevel() {
    return LayoutProcess.LEVEL_PAGINATE;
  }

  /**
   * Helper method for serialization.
   *
   * @param in
   *          the input stream from where to read the serialized object.
   * @throws java.io.IOException
   *           when reading the stream fails.
   * @throws ClassNotFoundException
   *           if a class definition for a serialized object could not be found.
   */
  private void readObject( final ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    attrName = computeUniqueIdentifier();
  }

  public AbstractElementFormatFunction getInstance() {
    final AbstractElementFormatFunction expression = (AbstractElementFormatFunction) super.getInstance();
    expression.attrName = computeUniqueIdentifier();
    return expression;
  }

  private String computeUniqueIdentifier() {
    return "need-eval-result:" + getClass().getName() + '@' + System.identityHashCode( this );
  }

}
