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
package org.pentaho.reporting.ui.datasources.jdbc.ui;

import java.util.ArrayList;
import javax.swing.ListModel;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JndiConnectionDefinition;

public class ConnectionsTreeModel implements TreeModel
{
  public static class MetaNode
  {
    public static final Object ROOT = new MetaNode();
    public static final Object SHARED = new MetaNode();
    public static final Object PRIVATE = new MetaNode();
  }

  private class ChangeHandler implements ListDataListener
  {
    private ChangeHandler()
    {
    }

    public void intervalAdded(final ListDataEvent e)
    {
      final ArrayList<JdbcConnectionDefinition> list = new ArrayList<JdbcConnectionDefinition>();
      for (int i = e.getIndex0(); i <= e.getIndex1(); i += 1)
      {
        list.add((JdbcConnectionDefinition) model.getElementAt(i));
      }
      fireDataAdded(list.toArray(new JdbcConnectionDefinition[list.size()]));
    }

    public void intervalRemoved(final ListDataEvent e)
    {
      final ArrayList<JdbcConnectionDefinition> list = new ArrayList<JdbcConnectionDefinition>();
      for (int i = e.getIndex0(); i <= e.getIndex1(); i += 1)
      {
        list.add((JdbcConnectionDefinition) model.getElementAt(i));
      }
      fireDataRemoved(list.toArray(new JdbcConnectionDefinition[list.size()]));
    }

    public void contentsChanged(final ListDataEvent e)
    {
//      rebuildLocalData();
    }
  }

  private final Object rootNode = MetaNode.ROOT;
  private final Object sharedNode = MetaNode.SHARED;
  private final Object privateNode = MetaNode.PRIVATE;

  private EventListenerList listenerList;
  private ArrayList<JdbcConnectionDefinition> sharedConnections;
  private ArrayList<JdbcConnectionDefinition> privateConnections;
  private final ListModel model;

  public ConnectionsTreeModel(final ListModel model)
  {
    if (model == null)
    {
      throw new NullPointerException();
    }
    listenerList = new EventListenerList();
    sharedConnections = new ArrayList<JdbcConnectionDefinition>();
    privateConnections = new ArrayList<JdbcConnectionDefinition>();
    this.model = model;
    this.model.addListDataListener(new ChangeHandler());
    rebuildLocalData();
  }

  private void rebuildLocalData()
  {
    sharedConnections.clear();
    privateConnections.clear();

    for (int i = 0; i < model.getSize(); i += 1)
    {
      final Object elementAt = model.getElementAt(i);
      if (elementAt instanceof JndiConnectionDefinition)
      {
        final JndiConnectionDefinition c = (JndiConnectionDefinition) elementAt;
        if (c.isShared())
        {
          sharedConnections.add(c);
          continue;
        }
      }
      privateConnections.add((JdbcConnectionDefinition) elementAt);
    }
  }

  private void fireDataRemoved(final JdbcConnectionDefinition[] args)
  {
    // collect data for events later ..
    final int[] indices = new int[args.length];
    final TreeModelListener[] treeModelListeners = getListeners();
    for (int a = 0; a < args.length; a += 1)
    {
      final JdbcConnectionDefinition connectionDefinition = args[a];
      indices[a] = privateConnections.indexOf(connectionDefinition);
    }

    rebuildLocalData();

    final TreePath treePath = new TreePath(new Object[]{getRoot(), privateNode});
    for (int a = 0; a < args.length; a += 1)
    {
      final int index = indices[a];
      if (index == -1)
      {
        continue;
      }

      final JdbcConnectionDefinition connectionDefinition = args[a];
      final TreeModelEvent treeEvent = new TreeModelEvent(this, treePath,
          new int[]{index}, new Object[]{connectionDefinition});
      for (int i = 0; i < treeModelListeners.length; i++)
      {
        final TreeModelListener listener = treeModelListeners[i];
        listener.treeNodesRemoved(treeEvent);
      }
    }
  }

  private void fireDataAdded(final JdbcConnectionDefinition[] args)
  {
    rebuildLocalData();

    // collect data
    final TreeModelListener[] treeModelListeners = getListeners();
    final TreePath treePath = new TreePath(new Object[]{getRoot(), privateNode});
    for (int a = 0; a < args.length; a += 1)
    {
      final JdbcConnectionDefinition connectionDefinition = args[a];
      final int index = privateConnections.indexOf(connectionDefinition);
      if (index == -1)
      {
        continue;
      }

      final TreeModelEvent treeEvent = new TreeModelEvent(this, treePath,
          new int[]{index}, new Object[]{connectionDefinition});
      for (int i = 0; i < treeModelListeners.length; i++)
      {
        final TreeModelListener listener = treeModelListeners[i];
        listener.treeNodesInserted(treeEvent);
      }
    }
  }

  public void fireTreeDataChanged(final TreePath treePath)
  {
    final TreeModelListener[] treeModelListeners = getListeners();
    final TreeModelEvent treeEvent = new TreeModelEvent(this, treePath);
    for (int i = 0; i < treeModelListeners.length; i++)
    {
      final TreeModelListener listener = treeModelListeners[i];
      listener.treeStructureChanged(treeEvent);
    }
  }

  protected TreeModelListener[] getListeners()
  {
    return listenerList.getListeners(TreeModelListener.class);
  }

  public void addTreeModelListener(final TreeModelListener l)
  {
    listenerList.add(TreeModelListener.class, l);
  }

  public void removeTreeModelListener(final TreeModelListener l)
  {
    listenerList.remove(TreeModelListener.class, l);
  }

  public Object getRoot()
  {
    return this.rootNode;
  }

  public int getChildCount(final Object parent)
  {
    if (this.rootNode.equals(parent))
    {
      return 2;
    }
    if (this.sharedNode.equals(parent))
    {
      return sharedConnections.size();
    }
    if (this.privateNode.equals(parent))
    {
      return privateConnections.size();
    }
    return 0;
  }

  public Object getChild(final Object parent, final int index)
  {
    if (this.rootNode.equals(parent))
    {
      switch (index)
      {
        case 0:
          return this.privateNode;
        case 1:
          return this.sharedNode;
        default:
          throw new IndexOutOfBoundsException();
      }
    }

    if (this.privateNode.equals(parent))
    {
      return privateConnections.get(index);
    }
    if (this.sharedNode.equals(parent))
    {
      return sharedConnections.get(index);
    }
    return null;
  }

  public boolean isLeaf(final Object node)
  {
    if (node instanceof MetaNode)
    {
      return false;
    }
    return true;
  }

  public void valueForPathChanged(final TreePath path, final Object newValue)
  {
    fireTreeDataChanged(path);
  }

  public int getIndexOfChild(final Object parent, final Object child)
  {
    if (this.rootNode.equals(parent))
    {
      if (this.privateNode.equals(child))
      {
        return 0;
      }
      if (this.sharedNode.equals(child))
      {
        return 1;
      }
      return -1;
    }

    if (child instanceof JdbcConnectionDefinition == false)
    {
      return -1;
    }

    final JdbcConnectionDefinition def = (JdbcConnectionDefinition) child;
    if (this.privateNode.equals(parent))
    {
      return privateConnections.indexOf(def);
    }
    if (this.sharedNode.equals(parent))
    {
      return sharedConnections.indexOf(def);
    }
    return -1;
  }

  public TreePath getPath(final JdbcConnectionDefinition def)
  {
    if (def instanceof JndiConnectionDefinition)
    {
      final JndiConnectionDefinition jndi = (JndiConnectionDefinition) def;
      if (jndi.isShared())
      {
        return new TreePath(new Object[]{this.rootNode, this.sharedNode, def});
      }
    }
    return new TreePath(new Object[]{this.rootNode, this.privateNode, def});
  }
}
