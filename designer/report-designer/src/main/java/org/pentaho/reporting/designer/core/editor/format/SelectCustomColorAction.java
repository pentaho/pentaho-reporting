/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
