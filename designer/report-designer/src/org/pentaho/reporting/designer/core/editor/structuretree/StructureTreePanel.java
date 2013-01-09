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
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.structuretree;

import java.awt.BorderLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreePath;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.actions.global.CopyAction;
import org.pentaho.reporting.designer.core.actions.global.CutAction;
import org.pentaho.reporting.designer.core.actions.global.PasteAction;
import org.pentaho.reporting.designer.core.actions.report.EditParametersAction;
import org.pentaho.reporting.designer.core.actions.report.EditQueryAction;
import org.pentaho.reporting.designer.core.editor.ContextMenuUtility;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.model.selection.ReportSelectionModel;
import org.pentaho.reporting.designer.core.util.SidePanel;
import org.pentaho.reporting.designer.core.util.exceptions.UncaughtExceptionsModel;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.ParameterMapping;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;

/**
 * By listening for update events, we could keep track of the trees and use a separate JTree for each report which
 * enables us to preserve the tree state on context switches.
 *
 * @author Thomas Morgner
 */
public class StructureTreePanel extends SidePanel
{
  private class TreeLeadSelectionListener implements TreeSelectionListener
  {
    /**
     * Called whenever the value of the selection changes.
     *
     * @param e the event that characterizes the change.
     */
    public void valueChanged(final TreeSelectionEvent e)
    {
      final JTree tree = getTree();
      final TreePath selectionPath = tree.getLeadSelectionPath();
      if (selectionPath == null)
      {
        setLeadSelection(null);
        return;
      }

      setLeadSelection(selectionPath.getLastPathComponent());
    }
  }


  private class ReportTreeContextMenuHandler extends MouseAdapter
  {
    private ReportTreeContextMenuHandler()
    {
    }


    private void createPopupMenu(final MouseEvent e)
    {
      final JTree tree = getTree();
      final ReportDesignerContext context = getReportDesignerContext();
      if (context.getActiveContext() == null)
      {
        return;
      }

      final TreePath path = tree.getPathForLocation(e.getX(), e.getY());
      if (path == null)
      {
        return;
      }
      if (tree.getSelectionModel().isPathSelected(path) == false)
      {
        tree.getSelectionModel().addSelectionPath(path);
      }

      final Object o = path.getLastPathComponent();
      final JPopupMenu pop = ContextMenuUtility.getMenu(context, o);
      if (pop == null)
      {
        return;
      }
      pop.show(tree, e.getX(), e.getY());
    }

    public void mouseClicked(final MouseEvent e)
    {
      if (e.getClickCount() == 2 && e.getButton() == MouseEvent.BUTTON1)
      {
        final JTree tree = getTree();
        final TreePath selectionPath = tree.getLeadSelectionPath();
        if (selectionPath == null)
        {
          return;
        }
        final Object selection = selectionPath.getLastPathComponent();
        try
        {
          if (selection instanceof ParameterDefinitionEntry)
          {
            final ParameterDefinitionEntry parameterDefinitionEntry = (ParameterDefinitionEntry) selection;
            EditParametersAction.performEditMasterReportParameters(getReportDesignerContext(), parameterDefinitionEntry);
          }
          else if (selection instanceof ParameterMapping)
          {
            EditParametersAction.performEditSubReportParameters(getReportDesignerContext());
          }
          if (selection instanceof DataFactory)
          {
            final EditQueryAction action = new EditQueryAction();
            action.setReportDesignerContext(getReportDesignerContext());
            action.performEdit((DataFactory) selection, null);
            return;
          }
          if (selection instanceof ReportQueryNode == false)
          {
            return;
          }
          final ReportQueryNode theQuery = (ReportQueryNode) selection;
          if (theQuery.isAllowEdit() == false)
          {
            return;
          }
          final EditQueryAction action = new EditQueryAction();
          action.setReportDesignerContext(getReportDesignerContext());
          action.performEdit(theQuery.getDataFactory(), theQuery.getQueryName());
        }
        catch (ReportDataFactoryException e1)
        {
          UncaughtExceptionsModel.getInstance().addException(e1);
        }
        return;
      }
      if (e.isPopupTrigger())
      {
        createPopupMenu(e);
      }
    }

    public void mousePressed(final MouseEvent e)
    {
      if (e.isPopupTrigger())
      {
        createPopupMenu(e);
      }
    }

    public void mouseReleased(final MouseEvent e)
    {
      if (e.isPopupTrigger())
      {
        createPopupMenu(e);
      }
    }
  }

  private ReportTree tree;
  private Object leadSelection;
  private CutAction cutAction;
  private CopyAction copyAction;
  private PasteAction pasteAction;
  public static final String LEAD_SELECTION_PROPERTY = "leadSelection";

  public StructureTreePanel(final ReportTree.RENDER_TYPE renderType)
  {
    cutAction = new CutAction();
    copyAction = new CopyAction();
    pasteAction = new PasteAction();

    tree = new ReportTree(renderType);
    tree.getSelectionModel().addTreeSelectionListener(new TreeLeadSelectionListener());
    tree.addMouseListener(new ReportTreeContextMenuHandler());

    final ActionMap map = tree.getActionMap();
    map.put(TransferHandler.getCutAction().getValue(Action.NAME), cutAction);
    map.put(TransferHandler.getCopyAction().getValue(Action.NAME), copyAction);
    map.put(TransferHandler.getPasteAction().getValue(Action.NAME), pasteAction);

    setLayout(new BorderLayout());
    add(new JScrollPane(tree), BorderLayout.CENTER);
  }

  protected void updateDesignerContext(final ReportDesignerContext oldContext, final ReportDesignerContext newContext)
  {
    super.updateDesignerContext(oldContext, newContext);
    cutAction.setReportDesignerContext(newContext);
    copyAction.setReportDesignerContext(newContext);
    pasteAction.setReportDesignerContext(newContext);
  }

  protected void updateSelection(final ReportSelectionModel model)
  {
    // do nothing. We *do* define the selection, we dont really listen to it..
  }

  protected void updateActiveContext(final ReportRenderContext oldContext, final ReportRenderContext newContext)
  {
    super.updateActiveContext(oldContext, newContext);
    tree.setRenderContext(newContext);
  }

  /**
   * Sets whether or not this component is enabled. A component that is enabled may respond to user input, while a
   * component that is not enabled cannot respond to user input.  Some components may alter their visual representation
   * when they are disabled in order to provide feedback to the user that they cannot take input. <p>Note: Disabling a
   * component does not disable it's children.
   * <p/>
   * <p>Note: Disabling a lightweight component does not prevent it from receiving MouseEvents.
   *
   * @param enabled true if this component should be enabled, false otherwise
   */
  public void setEnabled(final boolean enabled)
  {
    super.setEnabled(enabled);
    tree.setEnabled(enabled);
  }

  public Object getLeadSelection()
  {
    return leadSelection;
  }

  protected void setLeadSelection(final Object leadSelection)
  {
    final Object oldvalue = this.leadSelection;
    this.leadSelection = leadSelection;
    firePropertyChange(LEAD_SELECTION_PROPERTY, oldvalue, leadSelection);
  }

  protected ReportTree getTree()
  {
    return tree;
  }
}
