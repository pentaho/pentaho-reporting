package org.pentaho.reporting.designer.core.editor.drilldown;

import java.util.EventListener;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public interface DrillDownParameterRefreshListener extends EventListener
{
  public void requestParameterRefresh(final DrillDownParameterRefreshEvent model);
}
