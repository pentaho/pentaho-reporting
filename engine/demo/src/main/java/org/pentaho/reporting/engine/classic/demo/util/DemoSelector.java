/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.demo.util;

/**
 * A demo selector holds information about a set of demos using the DemoHandler interface and child demo selectors
 * holding other releated demos.
 *
 * @author Thomas Morgner
 */
public interface DemoSelector
{
  public String getName();

  public DemoSelector[] getChilds();

  public int getChildCount();

  public DemoHandler[] getDemos();

  public int getDemoCount();
}
