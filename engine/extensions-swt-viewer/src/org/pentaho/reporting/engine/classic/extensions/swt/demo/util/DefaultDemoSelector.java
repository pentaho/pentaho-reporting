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

package org.pentaho.reporting.engine.classic.extensions.swt.demo.util;

import java.util.ArrayList;

/**
 * A simple demo selector implementation. A demo selector is used by the
 * CompoundFrame to collect and display all available demos.
 *
 * Creation-Date: 8/17/2008
 *
 * @author Baochuan Lu
 */
public class DefaultDemoSelector implements DemoSelector
{
  private ArrayList<DemoHandler> demos;
  private ArrayList<DemoSelector> childs;
  private String name;
  private DemoSelector parent;

  public DefaultDemoSelector(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    this.name = name;
    this.demos = new ArrayList<DemoHandler>();
    this.childs = new ArrayList<DemoSelector>();
  }

  public String getName()
  {
    return name;
  }

  public void addChild (final DemoSelector selector)
  {
    if (selector == null)
    {
      throw new NullPointerException();
    }
    selector.setParent(this);
    childs.add(selector);
  }

  public DemoSelector[] getChilds()
  {
    return (DemoSelector[]) childs.toArray(new DemoSelector[childs.size()]);
  }

  public void addDemo (final DemoHandler handler)
  {
    if (handler == null)
    {
      throw new NullPointerException();
    }
    handler.setParent(this);
    demos.add(handler);
  }

  public DemoHandler[] getDemos()
  {
    return (DemoHandler[]) demos.toArray(new DemoHandler[demos.size()]);
  }

  public int getChildCount()
  {
    return childs.size();
  }

  public int getDemoCount()
  {
    return demos.size();
  }

  public DemoSelector getParent()
  {
    return parent;
  }

  public void setParent(final DemoSelector parent)
  {
    this.parent = parent;
  }
}
