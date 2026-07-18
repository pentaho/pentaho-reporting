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



package org.pentaho.reporting.engine.classic.extensions.legacy.charts.propertyeditor;

import org.pentaho.plugin.jfreereport.reportcharts.ThermometerUnit;
import org.pentaho.reporting.engine.classic.core.metadata.propertyeditors.EnumPropertyEditor;

public class ThermometerUnitsPropertyEditor extends EnumPropertyEditor {
  public ThermometerUnitsPropertyEditor() {
    super( ThermometerUnit.class, true );
  }
}
