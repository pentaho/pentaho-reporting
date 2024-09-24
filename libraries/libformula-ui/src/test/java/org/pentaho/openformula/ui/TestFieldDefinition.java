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

package org.pentaho.openformula.ui;

import javax.swing.*;

public class TestFieldDefinition implements FieldDefinition {
  private String name;

  public TestFieldDefinition( final String name ) {
    this.name = name;
  }

  public String getName() {
    return name;
  }

  public String getDisplayName() {
    return name;
  }

  public Icon getIcon() {
    return null;
  }

  public Class getFieldType() {
    return String.class;
  }
}
