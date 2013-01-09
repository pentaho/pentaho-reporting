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
 * Copyright (c) 2000 - 2009 Pentaho Corporation, Simba Management Limited and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.swing.UIManager;

/**
 * Prints a list of all defined Swing-UI properties.
 *
 * @author Thomas Morgner
 */
public final class SwingResources
{
  /**
   * DefaultConstructor.
   */
  private SwingResources()
  {
  }

  /**
   * Starts the program.
   *
   * @param args ignored
   */
  public static void main(final String[] args)
  {
    final Hashtable table = UIManager.getDefaults();
    final Enumeration keys = table.keys();
    final ArrayList list = new ArrayList();

    final char[] pad = new char[40];
    Arrays.fill(pad, ' ');

    while (keys.hasMoreElements())
    {
      final Object key = keys.nextElement();
      final Object value = table.get(key);
      final StringBuffer b = new StringBuffer(key.toString());
      if (b.length() < 40)
      {
        b.append(pad, 0, 40 - b.length());
      }
      b.append(" = ");
      b.append(value);
      list.add(b.toString());
    }

    Collections.sort(list);
    for (int i = 0; i < list.size(); i++)
    {
      System.out.println(list.get(i));
    }
    System.exit(0);
  }
}
