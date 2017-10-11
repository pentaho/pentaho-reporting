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

package org.pentaho.reporting.ui.datasources.pmd.util;

import org.pentaho.reporting.ui.datasources.pmd.Messages;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class LimitRowsCheckBoxActionListener extends AbstractAction implements ItemListener {
  private JSpinner maxPreviewRowsSpinner;

  public LimitRowsCheckBoxActionListener( final JSpinner maxPreviewRowsSpinner ) {
    this.maxPreviewRowsSpinner = maxPreviewRowsSpinner;
    putValue( Action.NAME, Messages.getString( "PmdDataSourceEditor.LimitRowsCheckBox" ) );
    putValue( Action.MNEMONIC_KEY, Messages.getMnemonic( "PmdDataSourceEditor.LimitRowsCheckBox.Mnemonic" ) );
    maxPreviewRowsSpinner.setEnabled( false );
  }

  public void itemStateChanged( final ItemEvent e ) {
    final Object source = e.getSource();
    if ( source instanceof AbstractButton ) {
      final AbstractButton b = (AbstractButton) source;
      maxPreviewRowsSpinner.setEnabled( b.isSelected() );
    }
  }

  public void actionPerformed( final ActionEvent e ) {
    final Object source = e.getSource();
    if ( source instanceof AbstractButton ) {
      final AbstractButton b = (AbstractButton) source;
      maxPreviewRowsSpinner.setEnabled( b.isSelected() );
    }
  }
}
