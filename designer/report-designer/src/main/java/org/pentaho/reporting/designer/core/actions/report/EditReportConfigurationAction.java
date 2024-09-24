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
import org.pentaho.reporting.designer.core.editor.ConfigurationEditorDialog;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.util.undo.EditReportConfigUndoEntry;
import org.pentaho.reporting.libraries.base.config.HierarchicalConfiguration;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.Enumeration;
import java.util.HashMap;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class EditReportConfigurationAction extends AbstractReportContextAction {
  public EditReportConfigurationAction() {
    putValue( Action.NAME, ActionMessages.getString( "EditReportConfigurationAction.Text" ) );
    putValue( Action.DEFAULT, ActionMessages.getString( "EditReportConfigurationAction.Description" ) );
    putValue( Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic( "EditReportConfigurationAction.Mnemonic" ) );
    putValue( Action.ACCELERATOR_KEY,
      ActionMessages.getOptionalKeyStroke( "EditReportConfigurationAction.Accelerator" ) );
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed( final ActionEvent e ) {
    final ReportDocumentContext activeContext = getReportDesignerContext().getActiveContext();
    if ( activeContext == null ) {
      // has no effect
      return;
    }

    final ConfigurationEditorDialog dialog;

    final Window window = LibSwingUtil.getWindowAncestor( getReportDesignerContext().getView().getParent() );
    if ( window instanceof JDialog ) {
      dialog = new ConfigurationEditorDialog( (JDialog) window );
    } else if ( window instanceof JFrame ) {
      dialog = new ConfigurationEditorDialog( (JFrame) window );
    } else {
      dialog = new ConfigurationEditorDialog();
    }

    final HierarchicalConfiguration config =
      (HierarchicalConfiguration) activeContext.getContextRoot().getReportConfiguration();
    final HashMap oldConfig = copyConfig( config );

    if ( dialog.performEdit( config ) ) {
      final HashMap newConfig = copyConfig( config );
      activeContext.getUndo().addChange( ActionMessages.getString( "EditReportConfigurationAction.Text" ),
        new EditReportConfigUndoEntry( oldConfig, newConfig ) );
      activeContext.getContextRoot().notifyNodeStructureChanged();
    }


  }

  private HashMap copyConfig( final HierarchicalConfiguration config ) {
    final Enumeration configProperties = config.getConfigProperties();
    final HashMap oldConfig = new HashMap();
    while ( configProperties.hasMoreElements() ) {
      final String key = (String) configProperties.nextElement();
      if ( config.isLocallyDefined( key ) ) {
        oldConfig.put( key, config.getConfigProperty( key ) );
      }
    }
    return oldConfig;
  }
}
