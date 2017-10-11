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

package org.pentaho.reporting.libraries.formula.demo;

import java.util.HashMap;
import javax.swing.JOptionPane;

import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

/**
 * A formula context that asks the user for input.
 *
 * @author Thomas Morgner
 */
public class DemoFormulaContext extends DefaultFormulaContext
{
  private HashMap values;

  public DemoFormulaContext()
  {
    values = new HashMap();
  }

  public Object resolveReference(final Object name)
  {
    final Object fromCache = values.get(name);
    if (fromCache != null)
    {
      return fromCache;
    }

    final String input = JOptionPane.showInputDialog("Please enter a value for '" + name + '\'');
    if (input != null)
    {
      values.put(name, input);
    }
    return input;
  }

  public Type resolveReferenceType(Object name)
  {
    // by returning the correct type of the reference, you can speed up
    // the formula computation a little bit, as we dont have to guess the
    // type from scratch.

    // If you dont know the type, return ANYTYPE. We will start looking at
    // the referenced object in that case.
    return AnyType.TYPE;
  }
}
