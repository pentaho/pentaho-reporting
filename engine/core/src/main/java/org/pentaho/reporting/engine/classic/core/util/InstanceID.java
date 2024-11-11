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


package org.pentaho.reporting.engine.classic.core.util;

import java.io.Serializable;

/**
 * This class can be used as ID to mark instances of objects. This allows to track and identify objects and their
 * clones.
 *
 * @author Thomas Morgner
 */
public final class InstanceID implements Serializable {
  /**
   * DefaultConstructor.
   */
  public InstanceID() {
  }

  /**
   * Returns a simple string representation of this object to make is identifiable by human users.
   *
   * @return the string representation.
   */
  public String toString() {
    return "InstanceID[" + hashCode() + ']';
  }
}
