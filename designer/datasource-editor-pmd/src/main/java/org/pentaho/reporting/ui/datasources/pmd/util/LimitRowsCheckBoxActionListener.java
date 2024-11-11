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
