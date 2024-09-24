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

package org.pentaho.reporting.engine.classic.core.designtime;

/**
 * Simple bean-like class for holding all the information about an attribute change.
 *
 * @author Thomas Morgner.
 */
public class AttributeChange implements Change {
  private String namespace;
  private String name;
  private Object oldValue;
  private Object newValue;

  public AttributeChange( final String namespace, final String name, final Object oldValue, final Object newValue ) {
    this.namespace = namespace;
    this.name = name;
    this.oldValue = oldValue;
    this.newValue = newValue;
  }

  public String getNamespace() {
    return namespace;
  }

  public String getName() {
    return name;
  }

  public Object getOldValue() {
    return oldValue;
  }

  public Object getNewValue() {
    return newValue;
  }
}
