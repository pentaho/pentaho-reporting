package org.pentaho.reporting.designer.core.editor.drilldown;

import java.util.EventObject;

import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public class DrillDownParameterRefreshEvent extends EventObject
{
  private DrillDownParameter[] parameter;

  public DrillDownParameterRefreshEvent(final Object source,
                                        final DrillDownParameter[] parameter)
  {
    super(source);
    this.parameter = parameter.clone();
  }

  public DrillDownParameter[] getParameter()
  {
    return parameter.clone();
  }
}
