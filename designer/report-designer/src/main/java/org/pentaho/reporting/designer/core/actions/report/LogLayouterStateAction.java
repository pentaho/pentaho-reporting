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

import org.pentaho.reporting.designer.core.actions.AbstractReportContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.layout.ModelPrinter;
import org.pentaho.reporting.engine.classic.core.layout.model.LogicalPageBox;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class LogLayouterStateAction extends AbstractReportContextAction implements SettingsListener {
  public LogLayouterStateAction() {
    putValue( Action.NAME, ActionMessages.getString( "LogLayouterStateAction.Text" ) );
    putValue( Action.SHORT_DESCRIPTION, ActionMessages.getString( "LogLayouterStateAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "LogLayouterStateAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke( "LogLayouterStateAction.Accelerator" ) );

    setVisible( WorkspaceSettings.getInstance().isDebugFeaturesVisible() );
    WorkspaceSettings.getInstance().addSettingsListener( this );
  }

  public void settingsChanged() {
    setVisible( WorkspaceSettings.getInstance().isDebugFeaturesVisible() );
  }

  public void actionPerformed( final ActionEvent e ) {
    try {
      final LogicalPageBox layout = getActiveContext().getSharedRenderer().getLayouter().layout();
      ModelPrinter.INSTANCE.print( layout );
    } catch ( Exception ex ) {
      UncaughtExceptionsModel.getInstance().addException( ex );
    }
  }
}
