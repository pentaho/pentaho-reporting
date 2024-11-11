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


package org.pentaho.reporting.ui.datasources.jdbc.ui;

import java.awt.event.ActionEvent;
import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JSpinner;

import org.pentaho.reporting.ui.datasources.jdbc.Messages;

public class LimitRowsCheckBoxActionListener extends AbstractAction
{
  private JSpinner maxPreviewRowsSpinner;

  public LimitRowsCheckBoxActionListener(final JSpinner maxPreviewRowsSpinner)
  {
    this.maxPreviewRowsSpinner = maxPreviewRowsSpinner;
    putValue(Action.NAME, Messages.getString("QueryPanel.LimitRowsCheckBox"));
    putValue(Action.MNEMONIC_KEY, Messages.getMnemonic("QueryPanel.LimitRowsCheckBox.Mnemonic"));
    maxPreviewRowsSpinner.setEnabled(false);
  }

  public void actionPerformed(final ActionEvent e)
  {
    final Object source = e.getSource();
    if (source instanceof AbstractButton)
    {
      final AbstractButton b = (AbstractButton) source;
      maxPreviewRowsSpinner.setEnabled(b.isSelected());
    }
  }
}
