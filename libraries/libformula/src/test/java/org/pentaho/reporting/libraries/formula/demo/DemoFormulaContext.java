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
