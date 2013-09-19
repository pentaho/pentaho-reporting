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
 * Copyright (c) 2009 Pentaho Corporation.  All rights reserved.
 */

package org.pentaho.reporting.designer.core.actions.global;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.pentaho.reporting.designer.core.actions.AbstractDesignerContextAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.util.SystemInformationDialog;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

public final class SystemInformationAction extends AbstractDesignerContextAction
{
  public SystemInformationAction()
  {
    putValue(Action.NAME, ActionMessages.getString("SystemInformationAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("SystemInformationAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("SystemInformationAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("SystemInformationAction.Accelerator"));
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    final Component parent = getReportDesignerContext().getParent();
    final Window window = LibSwingUtil.getWindowAncestor(parent);
    final SystemInformationDialog dialog;
    if (window instanceof JDialog)
    {
      dialog = new SystemInformationDialog((JDialog) window);
    }
    else if (window instanceof JFrame)
    {
      dialog = new SystemInformationDialog((JFrame) window);
    }
    else
    {
      dialog = new SystemInformationDialog();
    }
    dialog.performShow();
  }
}
