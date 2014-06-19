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

package org.pentaho.reporting.designer.core.actions.elements.format;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Action;
import javax.swing.JComboBox;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.StyleEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.TextStyleKeys;

public final class ApplyFontSizeAction extends AbstractElementSelectionAction
{
  private JComboBox comboBox;

  public ApplyFontSizeAction(final JComboBox comboBox)
  {
    this.comboBox = comboBox;
    putValue(Action.NAME, ActionMessages.getString("ApplyFontSizeAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("ApplyFontSizeAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("ApplyFontSizeAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("ApplyFontSizeAction.Accelerator"));
  }

  protected void selectedElementPropertiesChanged(final ReportModelEvent event)
  {
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    final Object o = comboBox.getSelectedItem();
    if (o instanceof Integer == false)
    {
      return;
    }
    final Integer fontSize = (Integer) o;
    final DocumentContextSelectionModel model = getSelectionModel();
    if (model == null)
    {
      return;
    }
    final List<Element> visualElements = model.getSelectedElementsOfType(Element.class);
    final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();
    for (Element visualElement : visualElements)
    {
      final ElementStyleSheet styleSheet = visualElement.getStyle();
      undos.add(StyleEditUndoEntry.createConditional(visualElement, TextStyleKeys.FONTSIZE, fontSize));
      styleSheet.setStyleProperty(TextStyleKeys.FONTSIZE, fontSize);
      visualElement.notifyNodePropertiesChanged();
    }
    getActiveContext().getUndo().addChange(ActionMessages.getString("ApplyFontSizeAction.UndoName"),
        new CompoundUndoEntry(undos.toArray(new UndoEntry[undos.size()])));
  }
}
