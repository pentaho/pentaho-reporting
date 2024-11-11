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


package org.pentaho.reporting.engine.classic.core.modules.misc.datafactory;

public interface ScriptValueConverter {
  /**
   * Attempts to convert a script-engine internal value to a pure java value. It is important to return
   * <code>null</code> for all values that cannot be converted by this implementation to let other implementations have
   * its turn.
   *
   * @param o
   *          the value to be converted.
   * @return the converted value or null if the value is not convertible.
   */
  public Object convert( Object o );
}
