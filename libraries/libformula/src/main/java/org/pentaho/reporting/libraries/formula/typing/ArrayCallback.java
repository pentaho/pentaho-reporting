/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.formula.typing;

import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;

/**
 * @author Cedric Pronzato
 */
public interface ArrayCallback {
  public LValue getRaw( int row, int column ) throws EvaluationException;

  public Object getValue( int row, int column ) throws EvaluationException;

  public Type getType( int row, int column ) throws EvaluationException;

  public int getColumnCount();

  public int getRowCount();
}
