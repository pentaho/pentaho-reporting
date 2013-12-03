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

package org.pentaho.reporting.designer.core.actions.elements;

import java.awt.Component;
import java.awt.Window;
import java.awt.event.ActionEvent;
import javax.swing.Action;
import javax.swing.JDialog;
import javax.swing.JFrame;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.format.ConditionalVisibilityDialog;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.libraries.designtime.swing.LibSwingUtil;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class AddConditionalVisibilityAction extends AbstractElementSelectionAction
{
  public AddConditionalVisibilityAction()
  {
    putValue(Action.NAME, ActionMessages.getString("AddConditionalVisibilityAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("AddConditionalVisibilityAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("AddConditionalVisibilityAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("AddConditionalVisibilityAction.Accelerator"));
  }

  protected void selectedElementPropertiesChanged(final ReportModelEvent event)
  {
  }

  protected void updateSelection()
  {
    if (isSingleElementSelection() == false)
    {
      setEnabled(false);
    }
    else
    {
      setEnabled(getSelectionModel().getSelectedElement(0) instanceof Element);
    }
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    final ReportSelectionModel selectionModel1 = getSelectionModel();
    if (selectionModel1 == null)
    {
      return;
    }

    final Element[] visualElements = selectionModel1.getSelectedVisualElements();
    if (visualElements.length != 1)
    {
      return;
    }


    final Component parent = getReportDesignerContext().getView().getParent();
    final Window window = LibSwingUtil.getWindowAncestor(parent);
    final ConditionalVisibilityDialog dialog;
    if (window instanceof JDialog)
    {
      dialog = new ConditionalVisibilityDialog((JDialog) window);
    }
    else if (window instanceof JFrame)
    {
      dialog = new ConditionalVisibilityDialog((JFrame) window);
    }
    else
    {
      dialog = new ConditionalVisibilityDialog();
    }

    final Expression oldExpression = visualElements[0].getStyleExpression(ElementStyleKeys.VISIBLE);
    dialog.setReportDesignerContext(getReportDesignerContext());
    final Expression expression = dialog.performEdit(oldExpression);
    if (expression != null)
    {
      visualElements[0].setStyleExpression(ElementStyleKeys.VISIBLE, expression.getInstance());
    }
  }
}
