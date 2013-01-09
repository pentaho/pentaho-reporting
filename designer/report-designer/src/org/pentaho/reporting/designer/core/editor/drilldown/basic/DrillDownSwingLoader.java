package org.pentaho.reporting.designer.core.editor.drilldown.basic;

import org.pentaho.ui.xul.XulException;
import org.pentaho.ui.xul.swing.SwingXulLoader;

/**
* Todo: Document me!
*
* @author Thomas Morgner.
*/
public class DrillDownSwingLoader extends SwingXulLoader
{
  public DrillDownSwingLoader()
      throws XulException
  {
    parser.registerHandler("parameter-table", XulDrillDownParameterTable.class.getName()); // NON-NLS
    parser.registerHandler("formula-field", XulFormulaTextField.class.getName()); // NON-NLS
  }
}
