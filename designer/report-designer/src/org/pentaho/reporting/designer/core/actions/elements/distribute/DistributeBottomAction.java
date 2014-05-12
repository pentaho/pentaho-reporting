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

package org.pentaho.reporting.designer.core.actions.elements.distribute;

import java.awt.event.ActionEvent;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.actions.AbstractElementSelectionAction;
import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.report.drag.MoveDragOperation;
import org.pentaho.reporting.designer.core.editor.report.snapping.EmptySnapModel;
import org.pentaho.reporting.designer.core.model.CachedLayoutData;
import org.pentaho.reporting.designer.core.model.ModelUtility;
import org.pentaho.reporting.designer.core.model.selection.DocumentContextSelectionModel;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.MassElementStyleUndoEntryBuilder;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.util.geom.StrictGeomUtility;

public final class DistributeBottomAction extends AbstractElementSelectionAction
{
  private static class ElementPositionComparator implements Comparator<Element>
  {
    public int compare(final Element o1, final Element o2)
    {
      final CachedLayoutData data1 = ModelUtility.getCachedLayoutData(o1);
      final long x1 = data1.getY() + data1.getHeight();
      final CachedLayoutData data2 = ModelUtility.getCachedLayoutData(o2);
      final long x2 = data2.getY() + data2.getHeight();
      if (x1 < x2)
      {
        return -1;
      }
      if (x1 > x2)
      {
        return +1;
      }
      return 0;
    }
  }

  public DistributeBottomAction()
  {
    putValue(Action.NAME, ActionMessages.getString("DistributeBottomAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("DistributeBottomAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("DistributeBottomAction.Mnemonic"));
    putValue(Action.SMALL_ICON, IconLoader.getInstance().getDistributeBottomIcon());
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getString("DistributeBottomAction.Accelerator"));
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
    if (visualElements.size() <= 2)
    {
      return;
    }

    final List<Element> reportElements = ModelUtility.filterParents(visualElements);
    if (reportElements.size() <= 2)
    {
      return;
    }

    Collections.sort(reportElements, new ElementPositionComparator());
    final MassElementStyleUndoEntryBuilder builder = new MassElementStyleUndoEntryBuilder(reportElements);
    final Element[] carrier = new Element[1];

    final int lastElementIdx = reportElements.size() - 1;
    final Element lastElement = reportElements.get(lastElementIdx);
    final Element firstElement = reportElements.get(0);

    final CachedLayoutData firstLayoutData = ModelUtility.getCachedLayoutData(firstElement);
    final CachedLayoutData lastLayoutData = ModelUtility.getCachedLayoutData(lastElement);

    final long height = (lastLayoutData.getY() + lastLayoutData.getHeight()) -
        (firstLayoutData.getY() + firstLayoutData.getHeight());

    final long incr = height / lastElementIdx;
    long currentY = firstLayoutData.getY() + firstLayoutData.getHeight();
    currentY += incr;//start from second element

    for (int i = 1; i < lastElementIdx; i++)
    {
      final Element reportElement = reportElements.get(i);
      final CachedLayoutData layoutData = ModelUtility.getCachedLayoutData(reportElement);
      final long delta = currentY - layoutData.getHeight();
      if (delta == 0)
      {
        continue;
      }

      carrier[0] = reportElement;
      final Point2D.Double originPoint = new Point2D.Double(0, StrictGeomUtility.toExternalValue(layoutData.getY()));
      final MoveDragOperation mop = new MoveDragOperation
          (Arrays.asList(carrier), originPoint, EmptySnapModel.INSTANCE, EmptySnapModel.INSTANCE);
      mop.update(new Point2D.Double(0, StrictGeomUtility.toExternalValue(delta)), 1);
      mop.finish();

      currentY += incr;
    }
    final MassElementStyleUndoEntry massElementStyleUndoEntry = builder.finish();
    getActiveContext().getUndo().addChange(ActionMessages.getString("DistributeBottomAction.UndoName"), massElementStyleUndoEntry);
  }
}
