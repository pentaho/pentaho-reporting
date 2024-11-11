/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
