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



package org.pentaho.reporting.engine.classic.core.states.datarow;

import org.pentaho.reporting.engine.classic.core.function.Expression;
import org.pentaho.reporting.engine.classic.core.function.Function;

import java.util.Iterator;

public interface LevelStorage {
  public int getLevelNumber();

  public Iterator<Function> getFunctions();

  public Iterator<Function> getPageFunctions();

  public Iterator<Expression> getActiveExpressions();
}
