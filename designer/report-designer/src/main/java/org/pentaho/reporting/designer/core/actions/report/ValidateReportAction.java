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

package org.pentaho.reporting.designer.core.actions.report;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.inspections.Inspection;
import org.pentaho.reporting.designer.core.inspections.InspectionResult;
import org.pentaho.reporting.designer.core.inspections.InspectionResultListener;
import org.pentaho.reporting.designer.core.inspections.InspectionsMessageDialog;
import org.pentaho.reporting.designer.core.inspections.InspectionsRegistry;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.background.BackgroundCancellableProcessHelper;
import org.pentaho.reporting.libraries.designtime.swing.background.CancelEvent;
import org.pentaho.reporting.libraries.designtime.swing.background.CancelListener;
import org.pentaho.reporting.libraries.designtime.swing.background.ProgressFeed;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;

public class ValidateReportAction extends AbstractReportContextAction {
  private static class InspectionResultCollector implements InspectionResultListener {
    private ArrayList<InspectionResult> results;

    private InspectionResultCollector() {
      results = new ArrayList<InspectionResult>();
    }

    public void notifyInspectionStarted() {

    }

    public void notifyInspectionResult( final InspectionResult result ) {
      results.add( result );
    }

    public InspectionResult[] toArray() {
      return results.toArray( new InspectionResult[ results.size() ] );
    }
  }

  private static class RunInspectionTask implements Runnable, ProgressFeed, CancelListener {
    private ReportDesignerContext reportDesignerContext;
    private InspectionResultCollector collector;
    private volatile double progress;
    private volatile boolean cancelled;

    private RunInspectionTask( final ReportDesignerContext reportDesignerContext ) {
      this.collector = new InspectionResultCollector();
      this.reportDesignerContext = reportDesignerContext;
    }

    public void cancelProcessing( final CancelEvent event ) {
      cancelled = true;
    }

    public double queryProgress() {
      return progress;
    }

    public void run() {
      final ReportDocumentContext activeContext = reportDesignerContext.getActiveContext();
      final MasterReport report = activeContext.getContextRoot();
      final int numberReports = countReports( report );

      runInspection( report, report, reportDesignerContext, null, collector, 1, numberReports );

      if ( cancelled == false ) {
        SwingUtilities.invokeLater( new ShowResultTask( reportDesignerContext, collector ) );
      }
    }

    private int runInspection( final AbstractReportDefinition def,
                               final MasterReport master,
                               final ReportDesignerContext reportDesignerContext,
                               final ReportRenderContext parentContext,
                               final InspectionResultCollector collector,
                               final int currentReport,
                               final int maxReports ) {
      final ReportRenderContext r = new ReportRenderContext
        ( master, def, parentContext, reportDesignerContext.getGlobalAuthenticationStore(), true );
      final Inspection[] inspections = InspectionsRegistry.getInstance().getInspections();

      if ( cancelled ) {
        return currentReport;
      }

      int counter = currentReport;
      progress = ( (double) counter / (double) maxReports );
      for ( int i = 0; i < inspections.length; i++ ) {
        final Inspection inspection = inspections[ i ];
        try {
          inspection.inspect( reportDesignerContext, r, collector );
        } catch ( Exception e ) {
          UncaughtExceptionsModel.getInstance().addException( e );
        }
      }


      final ArrayList<SubReport> subreports = new ArrayList<SubReport>();
      findLocalSubreports( def, subreports );

      for ( final SubReport subreport : subreports ) {
        counter = runInspection( subreport, master, reportDesignerContext, r, collector, counter + 1, maxReports );
        if ( cancelled ) {
          return counter;
        }
      }

      r.dispose();
      return counter;
    }

  }

  private static class ShowResultTask implements Runnable {
    private InspectionResultCollector collector;
    private ReportDesignerContext reportDesignerContext;

    private ShowResultTask( final ReportDesignerContext reportDesignerContext,
                            final InspectionResultCollector collector ) {
      this.reportDesignerContext = reportDesignerContext;
      this.collector = collector;
    }

    public void run() {
      if ( collector.results.isEmpty() ) {
        reportDesignerContext
          .setStatusText( ActionMessages.getString( "ValidateReportAction.ValidationRunNoResults" ) );
      } else {
        final Window window = LibSwingUtil.getWindowAncestor( reportDesignerContext.getView().getParent() );
        final InspectionsMessageDialog dialog;
        if ( window instanceof JDialog ) {
          dialog = new InspectionsMessageDialog( (JDialog) window );
        } else if ( window instanceof JFrame ) {
          dialog = new InspectionsMessageDialog( (JFrame) window );
        } else {
          dialog = new InspectionsMessageDialog();
        }

        dialog.performShowResult( reportDesignerContext, collector.toArray() );
      }
    }
  }

  public ValidateReportAction() {
    putValue( Action.NAME, ActionMessages.getString( "ValidateReportAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "ValidateReportAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "ValidateReportAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "ValidateReportAction.Accelerator" ) );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext reportDesignerContext = getReportDesignerContext();
    final RunInspectionTask task = new RunInspectionTask( reportDesignerContext );
    final Thread t = new Thread( task );
    t.setDaemon( true );
    BackgroundCancellableProcessHelper
      .executeProcessWithCancelDialog( t, task, reportDesignerContext.getView().getParent(),
        "Running Inspections ..", task );
  }

  private static void findLocalSubreports( final Section section, final ArrayList<SubReport> result ) {
    final int elementCount = section.getElementCount();
    for ( int i = 0; i < elementCount; i += 1 ) {
      final Element element = section.getElement( i );
      if ( element instanceof SubReport ) {
        result.add( (SubReport) element );
        continue;
      }

      if ( element instanceof RootLevelBand ) {
        final RootLevelBand rlb = (RootLevelBand) element;
        final SubReport[] subReports = rlb.getSubReports();
        result.addAll( Arrays.asList( subReports ) );
      }

      if ( element instanceof Section ) {
        findLocalSubreports( (Section) element, result );
      }
    }
  }

  private static int countReports( final AbstractReportDefinition def ) {
    final ArrayList<SubReport> result = new ArrayList<SubReport>();
    findLocalSubreports( def, result );
    int count = 1;
    for ( final SubReport subReport : result ) {
      count += countReports( subReport );
    }
    return count;
  }
}
