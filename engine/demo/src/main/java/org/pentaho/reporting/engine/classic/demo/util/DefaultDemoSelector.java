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

import java.util.ArrayList;

/**
 * A simple demo selector implementation. A demo selector is used by the CompoundFrame to collect and display all
 * available demos.
 *
 * @author Thomas Morgner
 */
public class DefaultDemoSelector implements DemoSelector
{
  private ArrayList demos;
  private ArrayList childs;
  private String name;

  public DefaultDemoSelector(final String name)
  {
    if (name == null)
    {
      throw new NullPointerException();
    }
    this.name = name;
    this.demos = new ArrayList();
    this.childs = new ArrayList();
  }

  public String getName()
  {
    return name;
  }

  public void addChild(DemoSelector selector)
  {
    if (selector == null)
    {
      throw new NullPointerException();
    }
    childs.add(selector);
  }

  public DemoSelector[] getChilds()
  {
    return (DemoSelector[]) childs.toArray(new DemoSelector[childs.size()]);
  }

  public void addDemo(DemoHandler handler)
  {
    if (handler == null)
    {
      throw new NullPointerException();
    }
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
}
