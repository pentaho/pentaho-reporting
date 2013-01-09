package org.pentaho.reporting.designer.core.editor.drilldown.basic;

import java.beans.PropertyChangeListener;

import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownModel;
import org.pentaho.reporting.designer.core.editor.drilldown.model.DrillDownParameter;
import org.pentaho.ui.xul.XulEventSource;

public class DrillDownModelWrapper implements XulEventSource
{
  private DrillDownModel model;

  public DrillDownModelWrapper(final DrillDownModel model)
  {
    if (model == null)
    {
      throw new NullPointerException();
    }
    this.model = model;
  }

  public void addPropertyChangeListener(final PropertyChangeListener listener)
  {
    model.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(final PropertyChangeListener listener)
  {
    model.removePropertyChangeListener(listener);
  }

  public void setDrillDownConfig(final String drillDownConfig)
  {
    model.setDrillDownConfig(drillDownConfig);
    model.firePropertyChange("preview", null, getPreview());
  }

  public String getDrillDownConfig()
  {
    return model.getDrillDownConfig();
  }

  public String getDrillDownPath()
  {
    return model.getDrillDownPath();
  }

  public String getTooltipFormula()
  {
    return model.getTooltipFormula();
  }

  public void setTooltipFormula(final String tooltipFormula)
  {
    model.setTooltipFormula(tooltipFormula);
  }

  public String getTargetFormula()
  {
    return model.getTargetFormula();
  }

  public void setTargetFormula(final String targetFormula)
  {
    model.setTargetFormula(targetFormula);
  }

  public void setDrillDownPath(final String drillDownPath)
  {
    model.setDrillDownPath(drillDownPath);
    model.firePropertyChange("preview", null, getPreview());
  }

  public DrillDownParameter[] getDrillDownParameter()
  {
    return model.getDrillDownParameter();
  }

  public void setDrillDownParameter(final DrillDownParameter[] drillDownParameters)
  {
    model.setDrillDownParameter(drillDownParameters);
    model.firePropertyChange("preview", null, getPreview());
  }

  public String getPreview()
  {
    return model.getDrillDownFormula();
  }

  public void refresh()
  {
    model.refresh();
    model.firePropertyChange("preview", null, getPreview());
  }

  public void clear()
  {
    model.clear();
    model.firePropertyChange("preview", null, getPreview());
  }

  public DrillDownModel getModel()
  {
    return model;
  }


}
