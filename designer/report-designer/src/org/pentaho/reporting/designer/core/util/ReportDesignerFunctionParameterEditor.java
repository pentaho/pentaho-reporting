package org.pentaho.reporting.designer.core.util;

import org.pentaho.openformula.ui.FunctionParameterEditor;
import org.pentaho.reporting.designer.core.ReportDesignerContext;

/**
 * Todo: Document me!
 * <p/>
 * Date: 07.10.2010
 * Time: 18:52:50
 *
 * @author Thomas Morgner.
 */
public interface ReportDesignerFunctionParameterEditor extends FunctionParameterEditor
{
  public void setReportDesignerContext(final ReportDesignerContext reportDesignerContext);
}
