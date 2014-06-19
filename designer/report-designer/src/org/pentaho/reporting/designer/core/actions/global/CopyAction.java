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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.designer.core.actions.global;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.ReportDocumentContext;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.dnd.ClipboardManager;
import org.pentaho.reporting.designer.core.util.dnd.InsertationUtil;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;

public class CopyAction extends AbstractElementSelectionAction
{
  public CopyAction()
  {
    putValue(Action.NAME, ActionMessages.getString("CopyAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("CopyAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("CopyAction.Mnemonic"));
    putValue(Action.SMALL_ICON, IconLoader.getInstance().getCopyIcon());
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("CopyAction.Accelerator"));
  }

  protected void selectedElementPropertiesChanged(final ReportModelEvent event)
  {
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    final DocumentContextSelectionModel selectionModel1 = getSelectionModel();
    if (selectionModel1 == null)
    {
      return;
    }

    final ReportDocumentContext activeContext = getActiveContext();
    final Object[] selectedElements = selectionModel1.getSelectedElements();
    if (selectedElements.length == 0)
    {
      return;
    }

    final ArrayList<Object> preparedElements = new ArrayList<Object>(selectedElements.length);
    for (int i = 0; i < selectedElements.length; i++)
    {
      final Object selectedElement = selectedElements[i];
      final Object preparedElement = InsertationUtil.prepareForCopy(activeContext, selectedElement);
      if (preparedElement != null)
      {
        preparedElements.add(preparedElement);
      }
    }

    ClipboardManager.getManager().setContents(preparedElements.toArray());
  }


}
