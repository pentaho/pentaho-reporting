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


package org.pentaho.reporting.engine.classic.core.wizard;

import java.io.Serializable;

public class MetaSelector implements Serializable {
  private String domain;
  private String name;
  private Object value;

  public MetaSelector( final String namespace, final String name, final Object value ) {
    if ( namespace == null ) {
      throw new NullPointerException();
    }
    if ( name == null ) {
      throw new NullPointerException();
    }

    this.domain = namespace;
    this.name = name;
    this.value = value;
  }

  public String getDomain() {
    return domain;
  }

  public String getName() {
    return name;
  }

  public Object getValue() {
    return value;
  }
}
