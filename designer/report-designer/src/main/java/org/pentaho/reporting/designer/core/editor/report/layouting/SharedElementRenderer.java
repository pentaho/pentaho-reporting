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

package org.pentaho.reporting.designer.core.editor.report.layouting;

import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.BreakPositionsList;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.IncompatibleFeatureException;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;
import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.SheetLayout;
import org.pentaho.reporting.engine.classic.core.modules.output.table.base.TableLayoutProducer;
import org.pentaho.reporting.engine.classic.core.util.InstanceID;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.EventListenerList;
import java.awt.geom.Rectangle2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author Thomas Morgner
 */
public class SharedElementRenderer {
  private final ReportLayouter reportLayouter;
  private final MasterReport masterReport;
  private long minimumVersionNeeded;
  private EventListenerList listenerList;
  private LogicalPageBox pageBox;
  private TransferGlobalLayoutProcessStep transferGlobalLayoutProcessor;
  private TransferLayoutProcessStep transferLayoutProcessor;
  private Map<InstanceID, Set<InstanceID>> conflicts;
  private long layoutAge;
  private boolean lastResult;
  private boolean warnMigration;

  private TableLayoutProducer tableLayoutProducer;
  private DesignerTableContentProducer tableContentProducer;

  public SharedElementRenderer( final ReportRenderContext reportRenderContext ) {
    if ( reportRenderContext == null ) {
      throw new NullPointerException();
    }

    this.masterReport = reportRenderContext.getMasterReportElement();
    if ( this.masterReport == null ) {
      throw new NullPointerException();
    }

    this.warnMigration = true;
    this.reportLayouter = new ReportLayouter( reportRenderContext );
    this.listenerList = new EventListenerList();
    this.transferGlobalLayoutProcessor = new TransferGlobalLayoutProcessStep();
    this.transferLayoutProcessor = new TransferLayoutProcessStep();
    this.conflicts = new HashMap<InstanceID, Set<InstanceID>>();
    this.minimumVersionNeeded = -1;
  }

  public void addChangeListener( final ChangeListener changeListener ) {
    listenerList.add( ChangeListener.class, changeListener );
  }

  public void removeChangeListener( final ChangeListener changeListener ) {
    listenerList.remove( ChangeListener.class, changeListener );
  }

  public void fireChangeEvent() {
    final ChangeEvent ce = new ChangeEvent( this );
    final ChangeListener[] changeListeners = listenerList.getListeners( ChangeListener.class );
    for ( int i = 0; i < changeListeners.length; i++ ) {
      final ChangeListener listener = changeListeners[ i ];
      listener.stateChanged( ce );
    }
  }

  protected OutputProcessorMetaData getOutputProcessorMetaData() {
    return reportLayouter.getOutputProcessorMetaData();
  }

  public boolean isLayoutValid() {
    return ( this.layoutAge == masterReport.getChangeTracker() );
  }

  public boolean performLayouting() {
    if ( this.isLayoutValid() ) {
      return lastResult;
    }
    try {
      this.pageBox = reportLayouter.layout();
    } catch ( final Exception e ) {
      //noinspection ThrowableInstanceNeverThrown
      UncaughtExceptionsModel.getInstance().addException( new ReportProcessingException
        ( "Fatal Layouter Error: This report cannot be processed due to a unrecoverable error in the reporting-engine. "
          +
          "Please file a bug-report.", e ) );

      if ( warnMigration ) {
        @SuppressWarnings( "ThrowableResultOfMethodCallIgnored" )
        final IncompatibleFeatureException ife = extractIncompatibleFeatureException( e );
        if ( ife != null ) {
          minimumVersionNeeded = ife.getMinimumVersionNeeded();
        }
        warnMigration = false;
      }
      pageBox = null;
    }

    transferGlobalLayoutProcessor.reset();

    try {
      if ( pageBox != null ) {
        final OutputProcessorMetaData outputProcessorMetaData = getOutputProcessorMetaData();
        if ( tableLayoutProducer == null ) {
          tableLayoutProducer = new TableLayoutProducer( outputProcessorMetaData );
          tableLayoutProducer.setProcessWatermark( false );
        } else {
          tableLayoutProducer.clear();
        }
        // we need to work on a copy here, as the layout computation marks boxes as finished to keep track
        // of the progress.
        tableLayoutProducer.update( pageBox, false );
        if ( tableContentProducer == null ) {
          tableContentProducer =
            new DesignerTableContentProducer( tableLayoutProducer.getLayout(), outputProcessorMetaData );
        } else {
          tableContentProducer.reset( tableLayoutProducer.getLayout() );
        }

        conflicts.clear();
        conflicts = tableContentProducer.computeConflicts( pageBox, conflicts );

        // watermark needs extra pass, or it will produce bogus warnings.
        tableLayoutProducer.computeDesigntimeConflicts( pageBox.getWatermarkArea() );
        final SheetLayout watermarkLayout = tableLayoutProducer.getLayout();
        tableContentProducer.reset( watermarkLayout );
        conflicts = tableContentProducer.computeWatermarkConflics( pageBox, conflicts );

        transferGlobalLayoutProcessor.performTransfer( pageBox, conflicts, masterReport );
      }
      lastResult = true;
      layoutAge = masterReport.getChangeTracker();
      fireChangeEvent();
      return true;
    } catch ( Exception e ) {
      UncaughtExceptionsModel.getInstance().addException( e );
      lastResult = false;
      layoutAge = masterReport.getChangeTracker();
      fireChangeEvent();
      return false;
    }
  }

  public long getMinimumVersionNeeded() {
    return minimumVersionNeeded;
  }

  public boolean isMigrationError() {
    return minimumVersionNeeded != -1;
  }

  public void clearMigrationError() {
    minimumVersionNeeded = -1;
  }

  private IncompatibleFeatureException extractIncompatibleFeatureException( Throwable e ) {
    while ( e != null ) {
      if ( e instanceof IncompatibleFeatureException ) {
        return (IncompatibleFeatureException) e;
      }
      if ( e == e.getCause() ) {
        return null;
      }
      e = e.getCause();
    }
    return null;
  }

  public void transferLocalLayout( final Section section,
                                   final Map<InstanceID, Element> elementsById,
                                   final BreakPositionsList verticalEdgePositions ) {
    elementsById.clear();
    verticalEdgePositions.clear();
    if ( pageBox != null ) {
      transferLayoutProcessor.performTransfer( section, pageBox, elementsById, verticalEdgePositions );
    }
  }

  public Map<InstanceID, Element> getElementsById() {
    return transferGlobalLayoutProcessor.getElementsById();
  }

  public Map<InstanceID, Set<InstanceID>> getConflicts() {
    return conflicts;
  }

  public BreakPositionsList getHorizontalEdgePositions() {
    return transferGlobalLayoutProcessor.getHorizontalEdgePositions();
  }

  public ReportLayouter getLayouter() {
    return reportLayouter;
  }

  public LogicalPageBox getPageBox() {
    return pageBox;
  }

  public Rectangle2D getFallbackBounds() {
    return new Rectangle2D.Float( 0, 0, masterReport.getPageDefinition().getWidth(), 0 );
  }
}
