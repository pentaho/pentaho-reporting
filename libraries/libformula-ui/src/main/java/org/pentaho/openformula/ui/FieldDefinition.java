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



package org.pentaho.openformula.ui;

import javax.swing.*;
import java.io.Serializable;

public interface FieldDefinition extends Serializable {
  public String getName();

  public String getDisplayName();

  public Icon getIcon();

  public Class getFieldType();
}
