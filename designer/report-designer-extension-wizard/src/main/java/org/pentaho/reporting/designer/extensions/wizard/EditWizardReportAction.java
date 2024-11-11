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


package org.pentaho.reporting.designer.extensions.wizard;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.ReportDesignerDesignTimeContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.Section;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.wizard.ui.xul.EmbeddedWizard;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.Action;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;

public class EditWizardReportAction extends AbstractReportContextAction {
  public EditWizardReportAction() {
    putValue( Action.NAME, Messages.getString( "EditWizardReportAction.MenuText" ) ); //$NON-NLS-1$
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext reportDesignerContext1 = getReportDesignerContext();
    if ( reportDesignerContext1 == null ) {
      return;
    }
    final ReportDocumentContext activeContext = reportDesignerContext1.getActiveContext();
    if ( activeContext == null ) {
      return;
    }
    final Component parent = reportDesignerContext1.getView().getParent();
    final Window window = getWindowAncestor( parent );
    final EmbeddedWizard dialog;
    dialog = createDialog( window, new ReportDesignerDesignTimeContext( reportDesignerContext1 ) );

    try {
      final AbstractReportDefinition realOriginal = activeContext.getReportDefinition();
      final AbstractReportDefinition original = (AbstractReportDefinition) realOriginal.derive();
      final AbstractReportDefinition def = dialog.run( original );
      if ( def == null ) {
        return;
      }

      if ( def instanceof MasterReport ) {
        reportDesignerContext1.addMasterReport( (MasterReport) def );
      } else if ( def instanceof SubReport ) {
        // todo: Undo entries ...

        final Section parentSection = getParentSection( realOriginal );

        if ( parentSection instanceof AbstractRootLevelBand ) {
          final AbstractRootLevelBand rlb = (AbstractRootLevelBand) parentSection;
          final SubReport[] reports = rlb.getSubReports();
          for ( int i = 0; i < reports.length; i++ ) {
            final SubReport report = reports[i];
            if ( report == realOriginal ) {
              rlb.removeSubreport( report );
              rlb.addSubReport( i, (SubReport) def );
              return;
            }
          }
        }

        if ( parentSection instanceof Band ) {
          final Band b = (Band) parentSection;
          final Element[] elementArray = b.getElementArray();
          for ( int i = 0; i < elementArray.length; i++ ) {
            final Element report = elementArray[i];
            if ( report == realOriginal ) {
              b.removeElement( report );
              b.addElement( i, def );
              return;
            }
          }
        }
      }

    } catch ( ReportProcessingException e1 ) {
      UncaughtExceptionsModel.getInstance().addException( e1 );
    }
  }

  /**
   * @param parent
   *          - a Component
   * @return Window
   * 
   *         Method replaced inline code to facilitate testing.
   */
  Window getWindowAncestor( Component parent ) {
    return LibSwingUtil.getWindowAncestor( parent );
  }

  /**
   * @param window
   * @param rddtc
   *          - ReportDesignerDesignTimeContext
   * @return EmbeddedWizard
   * 
   *         Method replaced inline code to facilitate testing.
   */
  EmbeddedWizard createDialog( Window window, ReportDesignerDesignTimeContext rddtc ) {
    return new EmbeddedWizard( window, rddtc );
  }

  /**
   * @param reportDefinition
   * @return Section
   * 
   *         Method replaced inline code to facilitate testing.
   */
  Section getParentSection( AbstractReportDefinition reportDefinition ) {
    return reportDefinition.getParentSection();
  }
}
