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

package org.pentaho.reporting.designer.core.actions.elements;

import java.util.ArrayList;
import javax.swing.Action;

import org.pentaho.reporting.designer.core.actions.ActionMessages;
import org.pentaho.reporting.designer.core.editor.groups.EditGroupsUndoEntry;
import org.pentaho.reporting.designer.core.editor.groups.GroupDataEntry;
import org.pentaho.reporting.designer.core.util.IconLoader;
import org.pentaho.reporting.designer.core.util.undo.BandedSubreportEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.CompoundUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.DataSourceEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ElementEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ExpressionAddedUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ExpressionRemoveUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.ParameterEditUndoEntry;
import org.pentaho.reporting.designer.core.util.undo.UndoEntry;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AbstractRootLevelBand;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.parameters.ModifiableReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;

public class LayerDownAction extends AbstractLayerAction
{
  public LayerDownAction()
  {
    putValue(Action.NAME, ActionMessages.getString("LayerDownAction.Text"));
    putValue(Action.SHORT_DESCRIPTION, ActionMessages.getString("LayerDownAction.Description"));
    putValue(Action.MNEMONIC_KEY, ActionMessages.getOptionalMnemonic("LayerDownAction.Mnemonic"));
    putValue(Action.ACCELERATOR_KEY, ActionMessages.getOptionalKeyStroke("LayerDownAction.Accelerator"));
    putValue(Action.SMALL_ICON, IconLoader.getInstance().getLayerDownIcon());
  }

  protected UndoEntry moveGroup(final RelationalGroup designerGroupElement)
      throws CloneNotSupportedException
  {
    if (isSingleElementSelection() == false)
    {
      return null;
    }

    final AbstractReportDefinition reportDefinition = getActiveContext().getReportDefinition();
    final GroupDataEntry[] entries = EditGroupsUndoEntry.buildGroupData(reportDefinition);
    final ArrayList<GroupDataEntry> list = new ArrayList<GroupDataEntry>(entries.length);
    int index = -1;
    for (int i = 0; i < entries.length; i++)
    {
      final GroupDataEntry entry = entries[i];
      list.add(entry);
      if (designerGroupElement.getObjectID() == entry.getInstanceID())
      {
        index = i;
      }
    }

    if (index <= 0)
    {
      return null;
    }

    final GroupDataEntry o = list.get(index);
    list.remove(index);
    list.add(index - 1, o);

    final GroupDataEntry[] changedEntries = list.toArray(new GroupDataEntry[list.size()]);
    EditGroupsUndoEntry.applyGroupData(reportDefinition, changedEntries);

    return new EditGroupsUndoEntry(entries, changedEntries);
  }

  protected UndoEntry moveVisualElement(final AbstractReportDefinition report, final Element element)
  {
    final ReportElement reportElement = element.getParentSection();
    if (reportElement instanceof Band == false)
    {
      return null;
    }

    if (element instanceof SubReport &&
        reportElement instanceof AbstractRootLevelBand)
    {
      final AbstractRootLevelBand re = (AbstractRootLevelBand) reportElement;
      final int count = re.getSubReportCount();
      for (int i = 1; i < count; i++)
      {
        final SubReport sr = re.getSubReport(i);
        if (sr == element)
        {
          re.removeSubreport(sr);
          re.addSubReport(i - 1, sr);

          return new CompoundUndoEntry
              (new BandedSubreportEditUndoEntry(re.getObjectID(), i, sr, null),
                  new BandedSubreportEditUndoEntry(re.getObjectID(), i - 1, null, sr));
        }
      }
    }

    final Band parentBand = (Band) reportElement;
    final int count = parentBand.getElementCount();
    for (int i = 1; i < count; i++)
    {
      final Element visualReportElement = parentBand.getElement(i);
      if (element == visualReportElement)
      {
        parentBand.removeElement(visualReportElement);
        parentBand.addElement(i - 1, visualReportElement);
        return new CompoundUndoEntry
            (new ElementEditUndoEntry(parentBand.getObjectID(), i, visualReportElement, null),
                new ElementEditUndoEntry(parentBand.getObjectID(), i - 1, null, visualReportElement));
      }
    }

    return null;
  }

  protected UndoEntry moveExpressions(final AbstractReportDefinition report, final Object element)
  {
    final ExpressionCollection expressionCollection = report.getExpressions();
    final Expression[] expressions = expressionCollection.getExpressions();
    for (int j = 0; j < expressions.length - 1; j++)
    {
      final Expression expression = expressions[j];
      if (element == expression)
      {
        if (expressions.length > j + 1)
        {
          expressionCollection.removeExpression(j);
          expressionCollection.add(j + 1, expression);
          report.fireModelLayoutChanged(report, ReportModelEvent.NODE_STRUCTURE_CHANGED, expression);
          return new CompoundUndoEntry
              (new ExpressionRemoveUndoEntry(j, expression), new ExpressionAddedUndoEntry(j + 1, expression));
        }
      }
    }
    return null;
  }


  protected UndoEntry moveDataFactories(final AbstractReportDefinition report, final Object element)
      throws ReportDataFactoryException
  {
    final CompoundDataFactory collection = (CompoundDataFactory) report.getDataFactory();
    final int dataFactoryCount = collection.size();
    for (int j = 0; j < dataFactoryCount; j++)
    {
      final DataFactory dataFactory = collection.getReference(j);
      if (element == dataFactory)
      {
        if (collection.size() > j + 1)
        {
          collection.remove(j);
          collection.add(j + 1, dataFactory);

          report.fireModelLayoutChanged(report, ReportModelEvent.NODE_STRUCTURE_CHANGED, dataFactory);
          return new CompoundUndoEntry
              (new DataSourceEditUndoEntry(j, dataFactory, null), new DataSourceEditUndoEntry(j + 1, null, dataFactory));
        }
      }
    }
    return null;
  }

  protected UndoEntry moveParameters(final AbstractReportDefinition report, final Object element)
  {
    if (report instanceof MasterReport == false)
    {
      return null;
    }
    final MasterReport mr = (MasterReport) report;
    final ReportParameterDefinition definition = mr.getParameterDefinition();
    if (definition instanceof ModifiableReportParameterDefinition == false)
    {
      return null;
    }

    final ModifiableReportParameterDefinition dpd = (ModifiableReportParameterDefinition) definition;
    final ParameterDefinitionEntry[] entries = dpd.getParameterDefinitions();
    for (int i = 0; i < entries.length; i++)
    {
      final ParameterDefinitionEntry entry = entries[i];
      if (element == entry)
      {
        if (dpd.getParameterCount() > i + 1)
        {
          dpd.removeParameterDefinition(i);
          dpd.addParameterDefinition(i + 1, entry);
          report.fireModelLayoutChanged(report, ReportModelEvent.NODE_STRUCTURE_CHANGED, entry);
          return new CompoundUndoEntry
              (new ParameterEditUndoEntry(i, entry, null), new ParameterEditUndoEntry(i + 1, null, entry));
        }
      }
    }
    return null;
  }


  protected boolean collectChange(final Object[] selectedElements,
                                  final AbstractReportDefinition report,
                                  final ArrayList<UndoEntry> undos)
  {
    for (int i = 0; i < selectedElements.length; i++)
    {
      final Object element = selectedElements[i];
      if (move(element, report, undos) == false)
      {
        return false;
      }
    }
    return true;
  }
}
