package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import java.util.ArrayList;

import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.reporting.libraries.formula.util.FormulaUtil;

public class PentahoRemoteDrillDownController extends PentahoDrillDownController
{
  public PentahoRemoteDrillDownController()
  {
  }

  protected DrillDownParameter[] filterParameter(final DrillDownParameter[] parameter)
  {
    final PentahoPathModel pentahoPathWrapper = getPentahoPathWrapper();
    // modify the parameter model.
    final ArrayList<DrillDownParameter> list = new ArrayList<DrillDownParameter>();
    boolean solutionAdded = false;
    boolean pathAdded = false;
    boolean nameAdded = false;
    for (int i = 0; i < parameter.length; i++)
    {
      final DrillDownParameter drillDownParameter = parameter[i];
      if ("solution".equals(drillDownParameter.getName()))
      {
        list.add(new DrillDownParameter("solution", FormulaUtil.quoteString(pentahoPathWrapper.getSolution())));
        solutionAdded = true;
      }
      else if ("path".equals(drillDownParameter.getName()))
      {
        list.add(new DrillDownParameter("path", FormulaUtil.quoteString(pentahoPathWrapper.getPath())));
        pathAdded = true;
      }
      else if ("name".equals(drillDownParameter.getName()))
      {
        list.add(new DrillDownParameter("name", FormulaUtil.quoteString(pentahoPathWrapper.getName())));
        nameAdded = true;
      }
      else if ("::pentaho-path".equals(drillDownParameter.getName()))
      {
        // ignore
      }
      else
      {
        list.add(drillDownParameter);
      }
    }
    if (nameAdded == false)
    {
      list.add(0, new DrillDownParameter("name", FormulaUtil.quoteString(pentahoPathWrapper.getName())));
    }
    if (pathAdded == false)
    {
      list.add(0, new DrillDownParameter("path", FormulaUtil.quoteString(pentahoPathWrapper.getPath())));
    }
    if (solutionAdded == false)
    {
      list.add(0, new DrillDownParameter("solution", FormulaUtil.quoteString(pentahoPathWrapper.getSolution())));
    }

    return super.filterParameter(list.toArray(new DrillDownParameter[list.size()]));
  }

  protected String getProfileName()
  {
    return "pentaho";
  }
}
