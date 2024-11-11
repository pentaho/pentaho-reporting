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


package org.pentaho.reporting.libraries.base.config;

import java.util.Enumeration;

public class StringEnumeration implements Enumeration<String> {
  private Enumeration parent;

  public StringEnumeration( final Enumeration parent ) {
    this.parent = parent;
  }

  public boolean hasMoreElements() {
    return parent.hasMoreElements();
  }

  public String nextElement() {
    return (String) parent.nextElement();
  }
}
