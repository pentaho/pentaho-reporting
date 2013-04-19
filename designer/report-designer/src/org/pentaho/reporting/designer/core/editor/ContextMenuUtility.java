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

package org.pentaho.reporting.designer.core.editor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.global.PasteAction;
import org.pentaho.reporting.designer.core.actions.report.AddDataFactoryAction;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportFunctionNode;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportParametersNode;
import org.pentaho.reporting.designer.core.editor.structuretree.ReportQueryNode;
import org.pentaho.reporting.designer.core.editor.structuretree.SubReportParametersNode;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.CrosstabCellBody;
import org.pentaho.reporting.engine.classic.core.CrosstabColumnGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabOtherGroup;
import org.pentaho.reporting.engine.classic.core.CrosstabRowGroup;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.Group;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.RelationalGroup;
import org.pentaho.reporting.engine.classic.core.RootLevelBand;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.GroupedMetaDataComparator;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class ContextMenuUtility
{
  private ContextMenuUtility()
  {
  }

  public static JPopupMenu getMenu(final ReportDesignerContext designerContext, final Object context)
  {
    if (context == null || context instanceof MasterReport)  // This check assumes that we've click on a report band see JIRA PRD-1076
    {
      return designerContext.getPopupMenu("popup-ReportDefinition"); // NON-NLS
    }
    final ReportRenderContext activeContext = designerContext.getActiveContext();
    if (activeContext != null)
    {
      if (context == activeContext.getReportDefinition())
      {
        return designerContext.getPopupMenu("popup-ReportDefinition");// NON-NLS
      }
    }
    if (context instanceof SubReport)
    {
      return designerContext.getPopupMenu("popup-SubReport");// NON-NLS
    }
    if (context instanceof CompoundDataFactory)
    {
      return createDataSourcePopup(designerContext);
    }
    if (context instanceof DataFactory)
    {
      return designerContext.getPopupMenu("popup-DataSource");// NON-NLS
    }
    if (context instanceof ReportFunctionNode)
    {
      return designerContext.getPopupMenu("popup-Expressions");// NON-NLS
    }
    if (context instanceof ReportQueryNode)
    {
      final ReportQueryNode rqn = (ReportQueryNode) context;
      if (rqn.isAllowEdit())
      {
        return designerContext.getPopupMenu("popup-Query");// NON-NLS
      }
      return designerContext.getPopupMenu("popup-Inherited-Query");// NON-NLS
    }
    if (context instanceof Expression)
    {
      return designerContext.getPopupMenu("popup-Expression");// NON-NLS
    }
    if (context instanceof RootLevelBand)
    {
      return designerContext.getPopupMenu("popup-RootLevelBand");// NON-NLS
    }
    if (context instanceof RelationalGroup)
    {
      return designerContext.getPopupMenu("popup-RelationalGroup");// NON-NLS
    }
    if (context instanceof CrosstabGroup)
    {
      return designerContext.getPopupMenu("popup-CrosstabGroup");// NON-NLS
    }
    if (context instanceof CrosstabOtherGroup)
    {
      return designerContext.getPopupMenu("popup-CrosstabOtherGroup");// NON-NLS
    }
    if (context instanceof CrosstabRowGroup)
    {
      return designerContext.getPopupMenu("popup-CrosstabRowGroup");// NON-NLS
    }
    if (context instanceof CrosstabColumnGroup)
    {
      return designerContext.getPopupMenu("popup-CrosstabColumnGroup");// NON-NLS
    }
    if (context instanceof CrosstabCellBody)
    {
      return designerContext.getPopupMenu("popup-CrosstabCellBody");// NON-NLS
    }
    if (context instanceof Group)
    {
      return designerContext.getPopupMenu("popup-Group");// NON-NLS
    }
    if (context instanceof Band)
    {
      return designerContext.getPopupMenu("popup-Band");// NON-NLS
    }
    if (context instanceof Element)
    {
      final Element element = (Element) context;
      final JPopupMenu popup = designerContext.getPopupMenu("popup-" + element.getElementTypeName());// NON-NLS
      if (popup != null)
      {
        return popup;
      }
      return designerContext.getPopupMenu("popup-Element");// NON-NLS
    }
    if (context instanceof ReportParameterDefinition)
    {
      return designerContext.getPopupMenu("popup-Parameters");// NON-NLS
    }
    if (context instanceof ParameterDefinitionEntry)
    {
      return designerContext.getPopupMenu("popup-Parameter");// NON-NLS
    }
    if (context instanceof ReportParametersNode)
    {
      return designerContext.getPopupMenu("popup-Parameters");// NON-NLS
    }
    if (context instanceof SubReportParametersNode)
    {
      return designerContext.getPopupMenu("popup-SubReportParameters");// NON-NLS
    }
    return null;
  }

  public static JPopupMenu createDataSourcePopup(final ReportDesignerContext designerContext)
  {
    final JPopupMenu insertDataSourcesMenu = new JPopupMenu();
    final PasteAction action = new PasteAction();
    action.setReportDesignerContext(designerContext);
    insertDataSourcesMenu.add(action);
    insertDataSourcesMenu.addSeparator();
    createDataSourceMenu(designerContext, insertDataSourcesMenu);
    return insertDataSourcesMenu;
  }

  public static void createDataSourceMenu(final ReportDesignerContext designerContext,
                                          final JComponent insertDataSourcesMenu)
  {
    JMenu subMenu = null;

    final Map<String, Boolean> groupingMap = new HashMap<String, Boolean>();
    final DataFactoryMetaData[] datas = DataFactoryRegistry.getInstance().getAll();
    for (int i = 0; i < datas.length; i++)
    {
      final DataFactoryMetaData data = datas[i];
      if (data.isHidden())
      {
        continue;
      }
      if (WorkspaceSettings.getInstance().isShowExpertItems() == false && data.isExpert())
      {
        continue;
      }
      if (WorkspaceSettings.getInstance().isShowDeprecatedItems() == false && data.isDeprecated())
      {
        continue;
      }
      if (WorkspaceSettings.getInstance().isExperimentalFeaturesVisible() == false && data.isExperimental())
      {
        continue;
      }
      if (data.isEditorAvailable() == false)
      {
        continue;
      }

      final String currentGrouping = data.getGrouping(Locale.getDefault());
      groupingMap.put(currentGrouping, groupingMap.containsKey(currentGrouping));
    }

    Arrays.sort(datas, new GroupedMetaDataComparator());
    Object grouping = null;
    boolean firstElement = true;
    for (int i = 0; i < datas.length; i++)
    {
      final DataFactoryMetaData data = datas[i];
      if (data.isHidden())
      {
        continue;
      }
      if (WorkspaceSettings.getInstance().isShowExpertItems() == false && data.isExpert())
      {
        continue;
      }
      if (WorkspaceSettings.getInstance().isShowDeprecatedItems() == false && data.isDeprecated())
      {
        continue;
      }
      if (WorkspaceSettings.getInstance().isExperimentalFeaturesVisible() == false && data.isExperimental())
      {
        continue;
      }

      if (data.isEditorAvailable() == false)
      {
        continue;
      }

      final String currentGrouping = data.getGrouping(Locale.getDefault());
      final boolean isMultiGrouping = groupingMap.get(currentGrouping);
      if (firstElement == false)
      {
        if (ObjectUtilities.equal(currentGrouping, grouping) == false)
        {
          grouping = currentGrouping;
          if (isMultiGrouping)
          {
            subMenu = new JMenu(currentGrouping);
            insertDataSourcesMenu.add(subMenu);
          }
        }
      }
      else
      {
        firstElement = false;
        grouping = currentGrouping;
        if (isMultiGrouping)
        {
          subMenu = new JMenu(currentGrouping);
          insertDataSourcesMenu.add(subMenu);
        }
      }
      final AddDataFactoryAction action = new AddDataFactoryAction(data);
      action.setReportDesignerContext(designerContext);
      if (isMultiGrouping)
      {
        //noinspection ConstantConditions
        subMenu.add(new JMenuItem(action));
      }
      else
      {
        insertDataSourcesMenu.add(new JMenuItem(action));
      }
    }
  }
}
