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
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.migration.MigrationDialog;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.designtime.AttributeChange;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MigrateReportAction extends AbstractReportContextAction {
  private class MigrationUpdateListener implements ReportModelListener {
    private MigrationUpdateListener() {
    }

    public void nodeChanged( final ReportModelEvent event ) {
      if ( event.getType() != ReportModelEvent.NODE_PROPERTIES_CHANGED ) {
        return;
      }
      if ( ( event.getElement() instanceof MasterReport ) == false ) {
        return;
      }

      final MasterReport masterReport = (MasterReport) event.getElement();
      final Object parameter = event.getParameter();
      if ( parameter instanceof AttributeChange ) {
        final AttributeChange change = (AttributeChange) parameter;
        if ( AttributeNames.Internal.NAMESPACE.equals( change.getNamespace() ) &&
          AttributeNames.Internal.COMAPTIBILITY_LEVEL.equals( change.getName() ) ) {
          updateCompatibilityLevel( masterReport );
        }
      }
    }
  }

  private MigrationUpdateListener updateListener;

  public MigrateReportAction() {
    putValue( Action.NAME, ActionMessages.getString( "MigrateReportAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "MigrateReportAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "MigrateReportAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "MigrateReportAction.Accelerator" ) );
    updateListener = new MigrationUpdateListener();
  }

  protected void updateActiveContext( final ReportRenderContext oldContext, final ReportRenderContext newContext ) {
    if ( oldContext != null ) {
      oldContext.getMasterReportElement().removeReportModelListener( updateListener );
    }

    if ( newContext == null ) {
      setEnabled( false );
      return;
    }

    newContext.getMasterReportElement().addReportModelListener( updateListener );

    final AbstractReportDefinition reportDefinition = newContext.getReportDefinition();
    if ( reportDefinition instanceof MasterReport == false ) {
      setEnabled( false );
      return;
    }

    updateCompatibilityLevel( (MasterReport) reportDefinition );

  }

  protected void updateCompatibilityLevel( final MasterReport reportDefinition ) {
    final Integer compatibilityLevel = reportDefinition.getCompatibilityLevel();
    if ( compatibilityLevel == null ) {
      setEnabled( false );
      return;
    }
    if ( compatibilityLevel <= 0 ) {
      setEnabled( false );
      return;
    }

    setEnabled( compatibilityLevel < ClassicEngineBoot.computeCurrentVersionId() );
  }

  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext context = getReportDesignerContext();
    migrateReport( context );
  }

  public static void migrateReport( final ReportDesignerContext context ) {
    final Component parent = context.getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor( parent );
    final MigrationDialog dialog;
    if ( window instanceof JDialog ) {
      dialog = new MigrationDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new MigrationDialog( (JFrame) window );
    } else {
      dialog = new MigrationDialog();
    }

    dialog.performMigration( context, context.getActiveContext() );
  }
}
