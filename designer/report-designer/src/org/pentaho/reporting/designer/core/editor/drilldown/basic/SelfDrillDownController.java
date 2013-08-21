package org.pentaho.reporting.designer.core.editor.drilldown.basic;

import java.util.HashMap;

import org.pentaho.openformula.ui.FieldDefinition;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.editor.ReportRenderContext;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterRefreshEvent;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterRefreshListener;
import org.pentaho.reporting.designer.core.editor.drilldown.DrillDownParameterTable;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.ReportParameterDefinition;

/**
 * Todo: Document me!
 *
 * @author Thomas Morgner.
 */
public class SelfDrillDownController extends DefaultXulDrillDownController
{
  private class UpdateParametersHandler implements DrillDownParameterRefreshListener
  {
    private UpdateParametersHandler()
    {
    }

    public void requestParameterRefresh(final DrillDownParameterRefreshEvent event)
    {
      final HashMap<String,DrillDownParameter> entries = new HashMap<String,DrillDownParameter>();
      final DrillDownParameter[] originalParams = event.getParameter();
      for (int i = 0; i < originalParams.length; i++)
      {
        final DrillDownParameter param = originalParams[i];
        param.setType(DrillDownParameter.Type.MANUAL);
        entries.put (param.getName(), param);
      }

      final ReportRenderContext activeContext = getReportDesignerContext().getActiveContext();
      final MasterReport masterReportElement = activeContext.getMasterReportElement();
      final ReportParameterDefinition reportParams = masterReportElement.getParameterDefinition();
      final ParameterDefinitionEntry[] parameterDefinitionEntries = reportParams.getParameterDefinitions();

      for (int i = 0; i < parameterDefinitionEntries.length; i++)
      {
        final ParameterDefinitionEntry entry = parameterDefinitionEntries[i];
        if (entries.containsKey(entry.getName()) == false)
        {
          entries.put(entry.getName(),
              new DrillDownParameter(entry.getName(), null, DrillDownParameter.Type.PREDEFINED, false, false));
        }
        else
        {
          final DrillDownParameter parameter = entries.get(entry.getName());
          parameter.setType(DrillDownParameter.Type.PREDEFINED);
        }
      }

      final DrillDownParameter[] parameters = entries.values().toArray(new DrillDownParameter[entries.size()]);
      getWrapper().setDrillDownParameter(parameters);
      getTable().setDrillDownParameter(parameters);
    }
  }

  public SelfDrillDownController()
  {
  }

  public void init(final ReportDesignerContext reportDesignerContext,
                   final DrillDownModel model,
                   final String[] fields)
  {
    super.init(reportDesignerContext, model, fields);
    final DrillDownParameterTable drillDownTable = getTable();
    if (drillDownTable != null)
    {
      drillDownTable.addDrillDownParameterRefreshListener(new UpdateParametersHandler());
    }
    
    getWrapper().setDrillDownConfig("self");
    getWrapper().setDrillDownPath(null);
  }

  public void deactivate()
  {

  }
}
