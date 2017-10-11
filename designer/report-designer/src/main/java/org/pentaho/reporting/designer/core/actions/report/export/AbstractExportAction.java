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

package org.pentaho.reporting.designer.core.actions.report.export;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.AbstractGuiContext;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.ExportActionPlugin;
import org.pentaho.reporting.engine.classic.core.modules.gui.commonswing.SwingCommonModule;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public abstract class AbstractExportAction extends AbstractReportContextAction {
  private class DefaultExportContext extends AbstractGuiContext {
    public Window getWindow() {
      final ReportDesignerContext context = getReportDesignerContext();
      if ( context == null ) {
        return null;
      }
      final Component parent = context.getView().getParent();
      return LibSwingUtil.getWindowAncestor( parent );
    }

    public MasterReport getReportJob() {
      final ReportDocumentContext activeContext = getActiveContext();
      if ( activeContext == null ) {
        return null;
      }
      return activeContext.getContextRoot();
    }

    public void fireReportChange( final MasterReport oldReport, final MasterReport newReport ) {
      firePropertyChange( "reportJob", oldReport, newReport );//$NON-NLS-1$
    }
  }

  private class EnableChangeListener implements PropertyChangeListener {
    protected EnableChangeListener() {
    }

    public void propertyChange( final PropertyChangeEvent evt ) {
      setEnabled( getActiveContext() != null && actionPlugin.isEnabled() );
    }
  }

  private ExportActionPlugin actionPlugin;
  private DefaultExportContext exportContext;

  public AbstractExportAction( final ExportActionPlugin actionPlugin ) {
    this.actionPlugin = actionPlugin;
    this.exportContext = new DefaultExportContext();
    this.actionPlugin.addPropertyChangeListener( "enabled", new EnableChangeListener() ); //$NON-NLS-1$
    this.actionPlugin.initialize( exportContext );

    putValue( Action.NAME, actionPlugin.getDisplayName() );
    putValue( Action.SHORT_DESCRIPTION, actionPlugin.getShortDescription() );
    putValue( Action.MNEMONIC_KEY, actionPlugin.getMnemonicKey() );
    putValue( Action.SMALL_ICON, actionPlugin.getSmallIcon() );
    putValue( SwingCommonModule.LARGE_ICON_PROPERTY, actionPlugin.getLargeIcon() );

    setEnabled( actionPlugin.isEnabled() );
  }

  protected void updateActiveContext( final ReportRenderContext oldContext, final ReportRenderContext newContext ) {
    super.updateActiveContext( oldContext, newContext );

    final MasterReport oldReport;
    if ( oldContext != null ) {
      oldReport = oldContext.getMasterReportElement();
    } else {
      oldReport = null;
    }
    final MasterReport newReport;
    if ( newContext != null ) {
      newReport = newContext.getMasterReportElement();
    } else {
      newReport = null;
    }

    exportContext.fireReportChange( oldReport, newReport );
    setEnabled( newContext != null && actionPlugin.isEnabled() );
  }

  public void actionPerformed( final ActionEvent e ) {
    if ( getActiveContext() == null ) {
      return;
    }

    if ( actionPlugin.isEnabled() == false ) {
      return;
    }

    final MasterReport reportJob = getActiveContext().getContextRoot();
    if ( reportJob == null ) {
      return;
    }

    try {
      actionPlugin.performExport( reportJob.clone() );
    } catch ( Exception e1 ) {
      UncaughtExceptionsModel.getInstance().addException( e1 );
    }
  }
}
