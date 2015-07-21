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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

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

import javax.swing.*;
import java.awt.*;
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
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final EmbeddedWizard dialog;
    dialog = new EmbeddedWizard( window, new ReportDesignerDesignTimeContext( reportDesignerContext1 ) );

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

        final Section parentSection = realOriginal.getParentSection();

        if ( parentSection instanceof AbstractRootLevelBand ) {
          final AbstractRootLevelBand rlb = (AbstractRootLevelBand) parentSection;
          final SubReport[] reports = rlb.getSubReports();
          for ( int i = 0; i < reports.length; i++ ) {
            final SubReport report = reports[ i ];
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
            final Element report = elementArray[ i ];
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
}
