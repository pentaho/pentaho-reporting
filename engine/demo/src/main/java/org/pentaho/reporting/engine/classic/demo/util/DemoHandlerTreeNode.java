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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.demo.util;

import java.util.Enumeration;
import javax.swing.tree.TreeNode;

/**
 * The DemoHandlerTreeNode is used to build the tree component to select a single demo from within a CompoundDemoFrame.
 *
 * @author Thomas Morgner
 */
public class DemoHandlerTreeNode implements TreeNode
{
  private TreeNode parent;
  private DemoHandler handler;

  public DemoHandlerTreeNode(final TreeNode parent, final DemoHandler handler)
  {
    this.parent = parent;
    this.handler = handler;
  }

  public DemoHandler getHandler()
  {
    return handler;
  }

  public TreeNode getChildAt(int childIndex)
  {
    return null;
  }

  public int getChildCount()
  {
    return 0;
  }

  public TreeNode getParent()
  {
    return parent;
  }

  public int getIndex(TreeNode node)
  {
    return -1;
  }

  public boolean getAllowsChildren()
  {
    return false;
  }

  public boolean isLeaf()
  {
    return true;
  }

  public Enumeration children()
  {
    return new ArrayEnumeration(new Object[0]);
  }

  public String toString()
  {
    return handler.getDemoName();
  }
}
