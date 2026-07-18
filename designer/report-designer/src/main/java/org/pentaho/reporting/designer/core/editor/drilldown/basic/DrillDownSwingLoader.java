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



package org.pentaho.reporting.designer.core.editor.drilldown.basic;

import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.swing.SwingXulLoader;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public class DrillDownSwingLoader extends SwingXulLoader {
  public DrillDownSwingLoader()
    throws XulException {
    parser.registerHandler( "parameter-table", XulDrillDownParameterTable.class.getName() ); // NON-NLS
    parser.registerHandler( "formula-field", XulFormulaTextField.class.getName() ); // NON-NLS
  }
}
