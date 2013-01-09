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

package org.pentaho.reporting.designer.core.editor.structuretree;

import java.awt.datatransfer.Transferable;
import java.util.ArrayList;
import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionEvent;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionListener;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.designer.core.settings.SettingsListener;
import org.pentaho.reporting.designer.core.settings.WorkspaceSettings;
import org.pentaho.reporting.designer.core.util.dnd.FieldDescriptionTransferable;
import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.AttributeNames;
import org.pentaho.reporting.engine.classic.core.Band;
import org.pentaho.reporting.engine.classic.core.CompoundDataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.SubReport;
import org.pentaho.reporting.engine.classic.core.designtime.AttributeChange;
import org.pentaho.reporting.engine.classic.core.designtime.AttributeExpressionChange;
import org.pentaho.reporting.engine.classic.core.designtime.StyleChange;
import org.pentaho.reporting.engine.classic.core.designtime.StyleExpressionChange;
import org.pentaho.reporting.engine.classic.core.event.ReportModelEvent;
import org.pentaho.reporting.engine.classic.core.event.ReportModelListener;
import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;
import org.pentaho.reporting.engine.classic.core.style.BandStyleKeys;

public class ReportTree extends JTree
{
  private class ReportUpdateHandler implements ReportSelectionListener, ReportModelListener
  {
    private ReportUpdateHandler()
    {
    }

    public void nodeChanged(final ReportModelEvent event)
    {
      if (event.isNodeStructureChanged() || event.isNodeAddedEvent() || event.isNodeDeleteEvent())
      {
        final TreeModel model = getModel();
        if (model instanceof AbstractReportDataTreeModel)
        {
          if (event.getElement() == renderContext.getReportDefinition())
          {
            final AbstractReportDataTreeModel realModel = (AbstractReportDataTreeModel) model;
            realModel.fireTreeDataChanged();
          }
        }
        else if (model instanceof ReportStructureTreeModel)
        {
          final ReportStructureTreeModel realModel = (ReportStructureTreeModel) model;
          realModel.fireTreeDataChanged(event.getSource());
        }
      }
      else if (event.getType() == ReportModelEvent.NODE_PROPERTIES_CHANGED)
      {
        final TreeModel model = getModel();
        if (model instanceof AbstractReportDataTreeModel)
        {
          final AbstractReportDataTreeModel realModel = (AbstractReportDataTreeModel) model;
          if (event.getElement() == model.getRoot())
          {
            final Object eventParameter = event.getParameter();
            if (eventParameter instanceof AttributeChange)
            {
              final AttributeChange attributeChange = (AttributeChange) eventParameter;
              if (AttributeNames.Internal.NAMESPACE.equals(attributeChange.getNamespace()) ||
                  AttributeNames.Internal.QUERY.equals(attributeChange.getNamespace()))
              {
                realModel.fireTreeNodeChanged(realModel.getDataFactoryElement());
              }

              // else do nothing, as style-changes and other attribute-changes have no effect on the datamodel.
            }
            else if (eventParameter instanceof AttributeExpressionChange ||
                     eventParameter instanceof StyleChange ||
                     eventParameter instanceof StyleExpressionChange)
            {
              // these things have no effect on the data ..
            }
            else if (eventParameter instanceof Expression ||
                     eventParameter instanceof ReportParameterDefinition)
            {
              realModel.fireTreeNodeChanged(eventParameter);
            }
            else
            {
              realModel.fireTreeDataChanged();
            }
          }
          else
          {
            realModel.fireTreeNodeChanged(event.getElement());
          }
        }
        else if (model instanceof ReportStructureTreeModel)
        {
          final ReportStructureTreeModel realModel = (ReportStructureTreeModel) model;
          final Object eventParameter = event.getParameter();
          if (eventParameter instanceof AttributeChange)
          {
            final AttributeChange attributeChange = (AttributeChange) eventParameter;
            if (AttributeNames.Core.NAMESPACE.equals(attributeChange.getNamespace()))
            {
              if (AttributeNames.Core.NAME.equals(attributeChange.getName()) ||
                  AttributeNames.Core.FIELD.equals(attributeChange.getName()) ||
                  AttributeNames.Core.VALUE.equals(attributeChange.getName())||
                  AttributeNames.Core.RESOURCE_IDENTIFIER.equals(attributeChange.getName()))
              {
                invalidateLayoutCache();
              }
            }
          }
          else if (event.getElement() instanceof Band)
          {
            if (eventParameter instanceof StyleChange)
            {
              final StyleChange change = (StyleChange) eventParameter;
              if (BandStyleKeys.LAYOUT.equals(change.getStyleKey()))
              {
                invalidateLayoutCache();
                realModel.fireTreeStructureChanged(event.getElement());
              }
              else
              {
                realModel.fireTreeNodeChanged(event.getElement());
              }
            }
            else
            {
              realModel.fireTreeNodeChanged(event.getElement());
            }
          }

          realModel.fireTreeNodeChanged(event.getSource());
        }
      }
      restoreState();
      expandAfterDataSourceEdit(event);
    }

    private void expandAfterDataSourceEdit(final ReportModelEvent event)
    {
      final Object element = event.getElement();
      if (event.isNodeStructureChanged() == false)
      {
        return;
      }
      if (element instanceof AbstractReportDefinition == false)
      {
        return;
      }
      if (treeModel instanceof AbstractReportDataTreeModel == false)
      {
        return;
      }
      final AbstractReportDataTreeModel dataTreeModel = (AbstractReportDataTreeModel) treeModel;

      final Object parameter = event.getParameter();
      if (parameter instanceof DataFactory)
      {
        SwingUtilities.invokeLater(new ExpandDataFactoryNodesTask(dataTreeModel));
      }
      else if (parameter instanceof Expression)
      {
        SwingUtilities.invokeLater(new ExpandExpressionNodesTask(dataTreeModel));
      }
      else if (parameter instanceof ReportParameterDefinition)
      {
        SwingUtilities.invokeLater(new ExpandParameterDataSourceTask(dataTreeModel));
      }

    }

    public void selectionAdded(final ReportSelectionEvent event)
    {
      if (updateFromInternalSource)
      {
        return;
      }
      try
      {
        updateFromExternalSource = true;

        final TreeModel model = getModel();
        if (model instanceof AbstractReportDataTreeModel)
        {
          final TreePath path = TreeSelectionHelper.getPathForNode((AbstractReportDataTreeModel) model,
              event.getElement());
          if (path != null)
          {
            addSelectionPath(path);
          }
        }
        else if (model instanceof ReportStructureTreeModel)
        {
          final TreePath path = TreeSelectionHelper.getPathForNode((ReportStructureTreeModel) model,
              event.getElement());
          if (path != null)
          {
            addSelectionPath(path);
          }
        }
      }
      finally
      {
        updateFromExternalSource = false;
      }
    }

    public void selectionRemoved(final ReportSelectionEvent event)
    {
      if (updateFromInternalSource)
      {
        return;
      }

      try
      {
        updateFromExternalSource = true;

        final TreeModel model = getModel();
        if (model instanceof AbstractReportDataTreeModel)
        {
          final TreePath path = TreeSelectionHelper.getPathForNode((AbstractReportDataTreeModel) model,
              event.getElement());
          if (path != null)
          {
            removeSelectionPath(path);
          }
        }
        else if (model instanceof ReportStructureTreeModel)
        {
          final TreePath path = TreeSelectionHelper.getPathForNode((ReportStructureTreeModel) model,
              event.getElement());
          if (path != null)
          {
            removeSelectionPath(path);
          }
        }

      }
      finally
      {
        updateFromExternalSource = false;
      }
    }

    public void leadSelectionChanged(final ReportSelectionEvent event)
    {
    }
  }

  private class TreeSelectionHandler implements TreeSelectionListener
  {
    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(final TreeSelectionEvent e)
    {
      if (renderContext != null)
      {
        renderContext.addExpandedNode(getRowForPath(e.getPath()));
      }
      if (updateFromExternalSource)
      {
        return;
      }
      updateFromInternalSource = true;
      try
      {
        final ReportRenderContext renderContext = getRenderContext();
        if (renderContext == null)
        {
          return;
        }

        final TreePath[] treePaths = getSelectionPaths();
        if (treePaths == null)
        {
          selectionModel.clearSelection();
          renderContext.getSelectionModel().clearSelection();
          return;
        }

        final ReportSelectionModel selectionModel = renderContext.getSelectionModel();
        final Object[] data = new Object[treePaths.length];
        for (int i = 0; i < treePaths.length; i++)
        {
          final TreePath path = treePaths[i];
          data[i] = path.getLastPathComponent();
        }
        selectionModel.setSelectedElements(data);
      }
      finally
      {
        updateFromInternalSource = false;
      }
    }
  }

  private class SettingsChangeHandler implements SettingsListener
  {
    private boolean showIndexColumns;

    private SettingsChangeHandler()
    {
      showIndexColumns = WorkspaceSettings.getInstance().isShowIndexColumns();
    }

    public void settingsChanged()
    {
      // revalidate the data model ..
      if (showIndexColumns != WorkspaceSettings.getInstance().isShowIndexColumns())
      {
        showIndexColumns = WorkspaceSettings.getInstance().isShowIndexColumns();

        final TreeModel model = getModel();
        if (model instanceof AbstractReportDataTreeModel)
        {
          final AbstractReportDataTreeModel realModel = (AbstractReportDataTreeModel) model;
          realModel.fireTreeDataChanged();
          restoreState();
        }
      }
      invalidateLayoutCache();
    }
  }

  public static enum RENDER_TYPE
  {
    REPORT, DATA
  }

  private class ExpandDataFactoryNodesTask implements Runnable
  {
    private AbstractReportDataTreeModel treeModel;

    private ExpandDataFactoryNodesTask(final AbstractReportDataTreeModel treeModel)
    {
      this.treeModel = treeModel;
    }

    public void run()
    {
      if (getModel() != treeModel)
      {
        return;
      }

      final CompoundDataFactory compoundDataFactory = treeModel.getDataFactoryElement();
      final int size = compoundDataFactory.size();
      for (int i = 0; i < size; i++)
      {
        final DataFactory df = compoundDataFactory.getReference(i);
        final TreePath path = TreeSelectionHelper.getPathForNode(treeModel, df);
        if (path == null)
        {
          return;
        }

        expandPath(path);
        final int count = treeModel.getChildCount(df);
        for (int x = 0; x < count; x++)
        {
          final Object child = treeModel.getChild(df, x);
          if (child == null)
          {
            continue;
          }
          final TreePath childPath = path.pathByAddingChild(child);
          expandPath(childPath);
        }
      }
    }
  }


  private class ExpandExpressionNodesTask implements Runnable
  {
    private AbstractReportDataTreeModel treeModel;

    private ExpandExpressionNodesTask(final AbstractReportDataTreeModel treeModel)
    {
      this.treeModel = treeModel;
    }

    public void run()
    {
      if (getModel() != treeModel)
      {
        return;
      }

      final AbstractReportDataTreeModel dataTreeModel = treeModel;
      expandPath(new TreePath(new Object[]{dataTreeModel.getRoot(), dataTreeModel.getReportFunctionNode()}));
    }
  }

  private class ExpandParameterDataSourceTask implements Runnable
  {
    private AbstractReportDataTreeModel treeModel;

    private ExpandParameterDataSourceTask(final AbstractReportDataTreeModel treeModel)
    {
      this.treeModel = treeModel;
    }

    public void run()
    {
      if (getModel() != treeModel)
      {
        return;
      }

      if (treeModel instanceof MasterReportDataTreeModel)
      {
        final MasterReportDataTreeModel dataTreeModel = (MasterReportDataTreeModel) treeModel;
        expandPath(new TreePath(new Object[]{dataTreeModel.getRoot(), dataTreeModel.getReportParametersNode()}));
      }

      if (treeModel instanceof SubReportDataTreeModel)
      {
        final SubReportDataTreeModel dataTreeModel = (SubReportDataTreeModel) treeModel;
        expandPath(new TreePath(new Object[]{dataTreeModel.getRoot(), dataTreeModel.getReportParametersNode()}));
      }
    }
  }


  private class ExpandEnvironmentDataSourceTask implements Runnable
  {
    private AbstractReportDataTreeModel treeModel;

    private ExpandEnvironmentDataSourceTask(final AbstractReportDataTreeModel treeModel)
    {
      this.treeModel = treeModel;
    }

    public void run()
    {
      if (getModel() != treeModel)
      {
        return;
      }

      expandPath(new TreePath(new Object[]{treeModel.getRoot(), treeModel.getReportEnvironmentDataRow()}));
    }
  }


  private class ColumnTransferHandler extends TransferHandler
  {
    /**
     * Creates a <code>Transferable</code> to use as the source for a data transfer. Returns the representation of the
     * data to be transferred, or <code>null</code> if the component's property is <code>null</code>
     *
     * @param c the component holding the data to be transferred; this argument is provided to enable sharing of
     *          <code>TransferHandler</code>s by multiple components
     * @return the representation of the data to be transferred, or <code>null</code> if the property associated with
     *         <code>c</code> is <code>null</code>
     */
    protected Transferable createTransferable(final JComponent c)
    {
      if (c != ReportTree.this)
      {
        return null;
      }

      final Object node = getSelectionPath().getLastPathComponent();
      if (node instanceof ReportFieldNode)
      {
        final ReportFieldNode field = (ReportFieldNode) node;
        return new FieldDescriptionTransferable(field.getFieldName());
      }
      if (node instanceof ParameterMapping)
      {
        final Object o = getSelectionPath().getParentPath().getLastPathComponent();
        if (o instanceof SubReportParametersNode.ImportParametersNode)
        {
          final ParameterMapping field = (ParameterMapping) node;
          return new FieldDescriptionTransferable(field.getAlias());
        }
      }
      if (node instanceof ParameterDefinitionEntry)
      {
        final ParameterDefinitionEntry field = (ParameterDefinitionEntry) node;
        return new FieldDescriptionTransferable(field.getName());
      }
      if (node instanceof Expression)
      {
        final Expression expression = (Expression) node;
        if (expression.getName() != null)
        {
          return new FieldDescriptionTransferable(expression.getName());
        }
      }
      return null;
    }

    public int getSourceActions(final JComponent c)
    {
      return COPY;
    }
  }

  private class RestoreStateTask implements Runnable
  {
    private RestoreStateTask()
    {
    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p/>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run()
    {
      if (renderContext != null)
      {
        final Object[] expandedNodesArray = renderContext.getExpandedNodes();
        for (int i = 0; i < expandedNodesArray.length; i++)
        {
          final Integer path = (Integer) expandedNodesArray[i];
          expandRow(path.intValue());
        }
      }
    }
  }

  private class ReportTreeExpansionListener implements TreeWillExpandListener
  {
    public void treeWillExpand(final TreeExpansionEvent event) throws ExpandVetoException
    {
      renderContext.addExpandedNode(getRowForPath(event.getPath()));
    }

    public void treeWillCollapse(final TreeExpansionEvent event) throws ExpandVetoException
    {
      renderContext.removeExpandedNode(getRowForPath(event.getPath()));
    }
  }
  
  private ReportRenderContext renderContext;
  private ReportUpdateHandler updateHandler;

  private boolean updateFromExternalSource;
  private boolean updateFromInternalSource;

  private RENDER_TYPE renderType;

  private static final DefaultTreeModel EMPTY_MODEL = new DefaultTreeModel(null, false);
  private SettingsChangeHandler settingsChangeHandler;

  public ReportTree(final RENDER_TYPE renderType)
  {
    super(EMPTY_MODEL);

    if (renderType == null)
    {
      throw new NullPointerException();
    }

    setRenderType(renderType);
    updateHandler = new ReportUpdateHandler();
    settingsChangeHandler = new SettingsChangeHandler();
    WorkspaceSettings.getInstance().addSettingsListener(settingsChangeHandler);
    
    addTreeWillExpandListener(new ReportTreeExpansionListener());

    setCellRenderer(new StructureTreeCellRenderer());
    getSelectionModel().addTreeSelectionListener(new TreeSelectionHandler());
    setTransferHandler(new ColumnTransferHandler());
    setDragEnabled(true);
    setEditable(false);
    if (renderType == RENDER_TYPE.DATA)
    {
      setRootVisible(false);
    }
  }


  protected void invalidateLayoutCache()
  {
    // this bit of magic invalidates the layout cache
    setCellRenderer(new StructureTreeCellRenderer());
  }

  public ReportRenderContext getRenderContext()
  {
    return renderContext;
  }

  public void setRenderContext(final ReportRenderContext renderContext)
  {
    if (this.renderContext != null)
    {
      this.renderContext.getSelectionModel().removeReportSelectionListener(updateHandler);
      this.renderContext.getReportDefinition().removeReportModelListener(updateHandler);
    }
    this.renderContext = renderContext;
    if (this.renderContext != null)
    {
      this.renderContext.getSelectionModel().addReportSelectionListener(updateHandler);
      this.renderContext.getReportDefinition().addReportModelListener(updateHandler);
    }
    updateFromRenderContext();
    restoreState();
  }

  private void restoreState()
  {
    SwingUtilities.invokeLater(new RestoreStateTask());
  }


  protected void updateFromRenderContext()
  {
    try
    {
      updateFromExternalSource = true;

      if (this.renderContext == null)
      {
        setModel(EMPTY_MODEL);
        return;
      }

      final AbstractReportDefinition report = this.renderContext.getReportDefinition();
      if (report instanceof MasterReport)
      {
        if (getRenderType() == RENDER_TYPE.REPORT)
        {
          setModel(new ReportStructureTreeModel(report));
        }
        else
        {
          setModel(new MasterReportDataTreeModel(renderContext));
        }
      }
      else if (report instanceof SubReport)
      {
        if (getRenderType() == RENDER_TYPE.REPORT)
        {
          setModel(new ReportStructureTreeModel(report));
        }
        else
        {
          setModel(new SubReportDataTreeModel(renderContext));
        }
      }
      else
      {
        setModel(EMPTY_MODEL);
      }

      if (getModel() instanceof AbstractReportDataTreeModel)
      {
        final AbstractReportDataTreeModel model = (AbstractReportDataTreeModel) getModel();
        final ReportSelectionModel selectionModel = renderContext.getSelectionModel();
        final Object[] selectedElements = selectionModel.getSelectedElements();
        final ArrayList<TreePath> selectionPaths = new ArrayList<TreePath>();
        for (int i = 0; i < selectedElements.length; i++)
        {
          final Object o = selectedElements[i];
          final TreePath path = TreeSelectionHelper.getPathForNode(model, o);
          if (path != null)
          {
            selectionPaths.add(path);
          }
        }
        getSelectionModel().setSelectionPaths(selectionPaths.toArray(new TreePath[selectionPaths.size()]));

        SwingUtilities.invokeLater(new ExpandDataFactoryNodesTask(model));
        SwingUtilities.invokeLater(new ExpandExpressionNodesTask(model));
        SwingUtilities.invokeLater(new ExpandParameterDataSourceTask(model));
        SwingUtilities.invokeLater(new ExpandEnvironmentDataSourceTask(model));
      }
    }
    finally
    {
      updateFromExternalSource = false;
    }
  }

  public RENDER_TYPE getRenderType()
  {
    return renderType;
  }

  public void setRenderType(final RENDER_TYPE renderType)
  {
    if (renderType == null)
    {
      throw new NullPointerException();
    }
    this.renderType = renderType;
  }

}
