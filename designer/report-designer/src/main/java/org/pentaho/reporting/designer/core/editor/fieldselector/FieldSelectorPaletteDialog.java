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

package org.pentaho.reporting.designer.core.editor.fieldselector;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.MacOSXIntegration;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class FieldSelectorPaletteDialog extends JDialog {
  private class FrameSizeMonitor extends WindowAdapter {
    private FrameSizeMonitor() {
    }

    /**
     * Invoked when a window is in the process of being closed. The close operation can be overridden at this point.
     */
    public void windowClosing( final WindowEvent e ) {
      WorkspaceSettings.getInstance().setFieldPaletteBounds( FieldSelectorPaletteDialog.this.getBounds() );
      WorkspaceSettings.getInstance().setFieldSelectorVisible( false );
    }
  }


  private FieldSelectorPanel fieldSelectorPanel;

  public FieldSelectorPaletteDialog( final Frame parent, final ReportDesignerContext designerContext ) {
    super( parent );
    init( designerContext );
  }

  protected void init( final ReportDesignerContext designerContext ) {
    setResizable( true );
    addWindowListener( new FrameSizeMonitor() );

    if ( MacOSXIntegration.MAC_OS_X ) {
      getRootPane().putClientProperty( "Window.style", "small" ); // NON-NLS
    } else if ( UIManager.getLookAndFeel().getSupportsWindowDecorations() ) {
      setUndecorated( true );
      getRootPane().setWindowDecorationStyle( JRootPane.PLAIN_DIALOG );
    }

    setTitle( Messages.getString( "FieldSelectorPaletteDialog.Title" ) );
    setDefaultCloseOperation( HIDE_ON_CLOSE );

    this.fieldSelectorPanel = new FieldSelectorPanel();
    this.fieldSelectorPanel.setReportDesignerContext( designerContext );

    setContentPane( fieldSelectorPanel );
  }

  public void initWindowLocation() {
    final Rectangle rectangle = WorkspaceSettings.getInstance().getFieldPaletteBounds();
    if ( rectangle != null ) {
      final Rectangle bounds = fieldSelectorPanel.getReportDesignerContext().getView().getParent().getBounds();
      if ( rectangle.contains( bounds ) || rectangle.equals( bounds ) ) {
        DebugLog.log( "Found a usable screen-configuration: Restoring frame to " + bounds );// NON-NLS
        setBounds( bounds );
        setVisible( true );
        return;
      }
    }

    pack();
    LibSwingUtil.centerDialogInParent( this );
  }
}
