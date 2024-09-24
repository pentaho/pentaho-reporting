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

package org.pentaho.reporting.designer.extensions.pentaho.repository.actions;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.extensions.pentaho.repository.Messages;

import java.awt.event.ActionEvent;

import javax.swing.Action;
import javax.swing.SwingUtilities;

public final class LoadReportFromRepositoryAction extends AbstractDesignerContextAction {
  public LoadReportFromRepositoryAction() {
    putValue( Action.NAME, Messages.getInstance().getString( "LoadReportFromRepositoryAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, Messages.getInstance().getString( "LoadReportFromRepositoryAction.Description" ) );
    putValue( Action.SMALL_ICON, IconLoader.getInstance().getOpenIcon() );
    putValue( Action.ACCELERATOR_KEY, Messages.getInstance().getOptionalKeyStroke(
        "LoadReportFromRepositoryAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDesignerContext reportDesignerContext = getReportDesignerContext();
    final OpenFileFromRepositoryTask openFileFromRepositoryTask =
        new OpenFileFromRepositoryTask( reportDesignerContext, reportDesignerContext.getView().getParent() );
    final LoginTask loginTask =
        new LoginTask( reportDesignerContext, reportDesignerContext.getView().getParent(), openFileFromRepositoryTask );

    SwingUtilities.invokeLater( loginTask );
  }
}
