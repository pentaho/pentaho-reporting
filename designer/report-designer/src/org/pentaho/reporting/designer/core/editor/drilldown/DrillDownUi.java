package org.pentaho.reporting.designer.core.editor.drilldown;

import java.awt.Component;

import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;

/**
 * Todo: Document me!
 * <p/>
 * Date: 05.08.2010
 * Time: 13:32:48
 *
 * @author Thomas Morgner.
 */
public interface DrillDownUi
{
  public Component getEditorPanel();
  public DrillDownModel getModel();
  public void init(final Component parent,
                   final ReportDesignerContext reportDesignerContext,
                   final DrillDownModel model) throws DrillDownUiException;
  public void deactivate();
}
