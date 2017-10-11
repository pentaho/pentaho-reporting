/*
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
 * Copyright (c) 2005-2017 Hitachi Vantara.  All rights reserved.
 */

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
