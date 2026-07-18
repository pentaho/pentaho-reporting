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



package org.pentaho.reporting.engine.classic.core.metadata.propertyeditors;

import org.pentaho.reporting.engine.classic.core.CrosstabDetailMode;

public class CrosstabDetailModePropertyEditor extends EnumPropertyEditor {
  public CrosstabDetailModePropertyEditor() {
    super( CrosstabDetailMode.class, true );
  }
}
