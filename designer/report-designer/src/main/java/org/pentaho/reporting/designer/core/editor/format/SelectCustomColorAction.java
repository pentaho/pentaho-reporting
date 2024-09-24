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

package org.pentaho.reporting.designer.core.editor.format;

import org.pentaho.reporting.designer.core.Messages;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;
import org.pentaho.reporting.libraries.designtime.swing.colorchooser.ColorChooserDialog;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class SelectCustomColorAction extends AbstractAction {
  private JComboBox comboBox;

  public SelectCustomColorAction( final JComboBox comboBox ) {
    this.comboBox = comboBox;
    putValue( Action.NAME, ".." );
  }

  public void actionPerformed( final ActionEvent e ) {
    final Color initialColor = (Color) comboBox.getSelectedItem();
    final Window window = LibSwingUtil.getWindowAncestor( comboBox );
    final ColorChooserDialog dialog;
    if ( window instanceof Dialog ) {
      dialog = new ColorChooserDialog( (Dialog) window );
    } else if ( window instanceof Frame ) {
      dialog = new ColorChooserDialog( (Frame) window );
    } else {
      dialog = new ColorChooserDialog();
    }
    dialog.setTitle( Messages.getString( "SelectCustomColorAction.Text" ) );
    final Color color = dialog.performEdit( initialColor, null );
    if ( color != null ) {
      comboBox.setSelectedItem( color );
    }
  }
}
