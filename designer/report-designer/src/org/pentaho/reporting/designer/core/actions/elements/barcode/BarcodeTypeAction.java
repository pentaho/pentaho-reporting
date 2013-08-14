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

package org.pentaho.reporting.designer.core.actions.elements.barcode;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.actions.ToggleStateAction;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.designer.core.util.undo.AttributeEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.designtime.ReportModelEventFilter;
import org.pentaho.reporting.engine.classic.core.designtime.ReportModelEventFilterFactory;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.extensions.modules.sbarcodes.SimpleBarcodesAttributeNames;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class BarcodeTypeAction extends AbstractElementSelectionAction implements ToggleStateAction
{
  private ReportModelEventFilter eventFilter;
  private String type;

  public BarcodeTypeAction(final String type)
  {
    this.type = type;
    putValue(Action.SELECTED_KEY, Boolean.FALSE);
    putValue(Action.NAME, type);
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("BarcodeTypeAction.Description", type));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("BarcodeTypeAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("BarcodeTypeAction.Accelerator"));

    eventFilter = new ReportModelEventFilterFactory().createAttributeFilter
        (SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE);
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

    final ReportSelectionModel selectionModel = getSelectionModel();
    if (selectionModel == null)
    {
      return;
    }
    final Element[] visualElements = filterBarcodeElements(selectionModel.getSelectedVisualElements());

    boolean selected;
    if (visualElements.length == 0)
    {
      selected = false;
    }
    else
    {
      selected = true;
      for (int i = 0; i < visualElements.length; i++)
      {
        final Element visualElement = visualElements[i];
        final Object oldValue = visualElement.getAttribute
            (SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE);
        selected &= ObjectUtilities.equal(oldValue, type);
      }
    }
    setSelected(selected);
  }

  /**
   * Invoked when an action occurs.
   */
  public void actionPerformed(final ActionEvent e)
  {
    final ReportSelectionModel selectionModel = getSelectionModel();
    if (selectionModel == null)
    {
      return;
    }
    final Element[] visualElements = filterBarcodeElements(selectionModel.getSelectedVisualElements());
    if (visualElements.length == 0)
    {
      return;
    }
    final ReportRenderContext activeContext = getActiveContext();
    if (activeContext == null)
    {
      return;
    }

    final ArrayList<UndoEntry> undos = new ArrayList<UndoEntry>();
    for (int i = 0; i < visualElements.length; i++)
    {
      final Element element = visualElements[i];
      final Object oldValue = element.getAttribute
          (SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE);
      final UndoEntry entry = new AttributeEditUndoEntry(element.getObjectID(),
          SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE,
          oldValue, type);
      undos.add(entry);
      element.setAttribute(SimpleBarcodesAttributeNames.NAMESPACE, SimpleBarcodesAttributeNames.TYPE_ATTRIBUTE, type);
    }
    getActiveContext().getUndo().addChange(ActionMessages.getString("BarcodeTypeAction.UndoName", type),
        new CompoundUndoEntry(undos.toArray(new UndoEntry[undos.size()])));
  }


  private Element[] filterBarcodeElements(final Element[] elements)
  {
    final ArrayList<Element> retval = new ArrayList<Element>();
    for (int i = 0; i < elements.length; i++)
    {
      final Element element = elements[i];
      if ("simple-barcodes".equals(element.getElementTypeName()))
      {
        retval.add(element);
      }
    }
    return retval.toArray(new Element[retval.size()]);
  }
}
