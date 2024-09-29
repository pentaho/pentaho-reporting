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


package org.pentaho.reporting.libraries.formula.typing;

import org.pentaho.reporting.libraries.formula.EvaluationException;

/**
 * A comparator, that offers type support. Unlike the plain Java-Comparator, this class is able to compare
 *
 * @author Thomas Morgner
 */
public interface ExtendedComparator {
  public boolean isEqual( final Type type1,
                          final Object value1,
                          final Type type2,
                          final Object value2 );

  /**
   * Returns null, if the types are not comparable and are not convertible at all.
   *
   * @param type1
   * @param value1
   * @param type2
   * @param value2
   * @return
   */
  public int compare( final Type type1,
                      final Object value1,
                      final Type type2,
                      final Object value2 ) throws EvaluationException;
}
