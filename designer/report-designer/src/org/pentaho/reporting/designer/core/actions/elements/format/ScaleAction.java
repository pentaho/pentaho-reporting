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

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.StyleEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.designtime.ReportModelEventFilter;
import org.pentaho.reporting.engine.classic.core.designtime.ReportModelEventFilterFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleKeys;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;

public final class ScaleAction extends AbstractElementSelectionAction implements ToggleStateAction
{
  private ReportModelEventFilter eventFilter;

  public ScaleAction()
  {
    putValue(Action.SELECTED_KEY, Boolean.FALSE);
    putValue(Action.NAME, ActionMessages.getString("ScaleAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("ScaleAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("ScaleAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("ScaleAction.Accelerator"));

    eventFilter = new ReportModelEventFilterFactory().createStyleFilter(ElementStyleKeys.SCALE);
  }

  protected void selectedElementPropertiesChanged(final ReportModelEvent event)
  {
    if (eventFilter.isFilteredEvent(event))
    {
      updateSelection();
    }
  }

  public boolean isSelected()
  {
    return Boolean.TRUE.equals(getValue(Action.SELECTED_KEY));
  }

  public void setSelected(final boolean selected)
  {
    putValue(Action.SELECTED_KEY, selected);
  }

  protected void updateSelection()
  {
    super.updateSelection();

    final DocumentContextSelectionModel model = getSelectionModel();
    if (model == null)
    {
      return;
    }
    final List<Element> visualElements = model.getSelectedElementsOfType(Element.class);
    if (visualElements.isEmpty())
    {
      setSelected(false);
      return;
    }

    final Element element = visualElements.get(0);
    final ElementStyleSheet styleSheet = element.getStyle();
    setSelected(styleSheet.getBooleanStyleProperty(ElementStyleKeys.SCALE));
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
    final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();

    Boolean value = null;
    for (Element element : visualElements)
    {
      final ElementStyleSheet styleSheet = element.getStyle();
      if (value == null)
      {
        if (styleSheet.getBooleanStyleProperty(ElementStyleKeys.SCALE))
        {
          value = Boolean.FALSE;
        }
        else
        {
          value = Boolean.TRUE;
        }
      }
      undos.add(StyleEditUndoEntry.createConditional(element, ElementStyleKeys.SCALE, value));
      styleSheet.setStyleProperty(ElementStyleKeys.SCALE, value);
    }
    getActiveContext().getUndo().addChange(ActionMessages.getString("ScaleAction.UndoName"),
        new CompoundUndoEntry(undos.toArray(new UndoEntry[undos.size()])));
  }
}
