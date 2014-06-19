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

package org.pentaho.reporting.designer.core.actions.elements.move;

import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.List;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.report.drag.MoveDragOperation;
import org.pentaho.reporting.designer.core.editor.report.snapping.EmptySnapModel;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntryBuilder;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;

public final class MoveDownAction extends AbstractElementSelectionAction
{
  public MoveDownAction()
  {
    putValue(Action.NAME, ActionMessages.getString("MoveDownAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("MoveDownAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("MoveDownAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("MoveDownAction.Accelerator"));
  }

  protected void selectedElementPropertiesChanged(final ReportModelEvent event)
  {
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    final DocumentContextSelectionModel model = getSelectionModel();
    if (model == null)
    {
      return;
    }
    final List<Element> visualElements = model.getSelectedElementsOfType(Element.class);
    if (visualElements.isEmpty())
    {
      return;
    }

    final MassElementStyleUndoEntryBuilder builder = new MassElementStyleUndoEntryBuilder(visualElements);
    final MoveDragOperation mop = new MoveDragOperation
        (visualElements, new Point(), EmptySnapModel.INSTANCE, EmptySnapModel.INSTANCE);
    mop.update(new Point(0, 5), 1);
    mop.finish();
    final MassElementStyleUndoEntry massElementStyleUndoEntry = builder.finish();
    getActiveContext().getUndo().addChange(ActionMessages.getString("MoveDownAction.UndoName"),
        massElementStyleUndoEntry);
  }
}
