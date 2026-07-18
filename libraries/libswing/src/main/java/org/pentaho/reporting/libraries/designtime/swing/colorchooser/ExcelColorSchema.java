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



package org.pentaho.reporting.libraries.designtime.swing.colorchooser;

import org.pentaho.reporting.libraries.designtime.swing.ColorUtility;

import java.awt.*;

public class ExcelColorSchema implements ColorSchema {
  public ExcelColorSchema() {
  }

  public Color[] getColors() {
    return ColorUtility.getPredefinedExcelColors();
  }

  public String getName() {
    return ColorChooserMessages.getInstance().getString( "ColorSchema.Excel" );
  }
}
