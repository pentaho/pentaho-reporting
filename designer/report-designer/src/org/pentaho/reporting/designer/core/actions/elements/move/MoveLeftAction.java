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

package org.pentaho.reporting.designer.core.actions.elements.move;

import java.awt.Point;
import java.awt.event.ActionEvent;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.report.drag.MoveDragOperation;
import org.pentaho.reporting.designer.core.editor.report.snapping.EmptySnapModel;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntryBuilder;
import org.pentaho.reporting.engine.classic.core.Element;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public final class MoveLeftAction extends AbstractElementSelectionAction
{
  public MoveLeftAction()
  {
    putValue(Action.NAME, ActionMessages.getString("MoveLeftAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("MoveLeftAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("MoveLeftAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("MoveLeftAction.Accelerator"));
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    final Element[] visualElements = getSelectionModel().getSelectedVisualElements();
    if (visualElements.length == 0)
    {
      return;
    }

    final MassElementStyleUndoEntryBuilder builder = new MassElementStyleUndoEntryBuilder(visualElements);
    final MoveDragOperation mop = new MoveDragOperation
        (visualElements, new Point(), EmptySnapModel.INSTANCE, EmptySnapModel.INSTANCE);
    mop.update(new Point(-5, 0), 1);
    mop.finish();

    final MassElementStyleUndoEntry massElementStyleUndoEntry = builder.finish();
    getActiveContext().getUndo().addChange(ActionMessages.getString("MoveLeftAction.UndoName"), massElementStyleUndoEntry);
  }
}
