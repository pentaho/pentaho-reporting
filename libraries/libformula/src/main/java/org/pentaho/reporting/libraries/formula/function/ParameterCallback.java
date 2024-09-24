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

package org.pentaho.reporting.libraries.formula.function;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.typing.Type;

/**
 * Creation-Date: 13.11.2006, 13:52:21
 *
 * @author Thomas Morgner
 */
public interface ParameterCallback {
  public LValue getRaw( int position );

  public Object getValue( int position ) throws EvaluationException;

  public Type getType( int position ) throws EvaluationException;

  public int getParameterCount();
}
