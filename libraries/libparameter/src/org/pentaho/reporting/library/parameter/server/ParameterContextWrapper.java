package org.pentaho.reporting.library.parameter.server;

import org.pentaho.reporting.libraries.base.boot.ObjectFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.library.parameter.ParameterContext;
import org.pentaho.reporting.library.parameter.ParameterData;
import org.pentaho.reporting.library.parameter.ParameterException;
import org.pentaho.reporting.library.parameter.ParameterQuery;

public class ParameterContextWrapper implements ParameterContext
{
  private ParameterContext context;
  private ParameterData parameterData;

  public ParameterContextWrapper(final ParameterContext context,
                                 final ParameterData parameterData)
  {
    this.context = context;
    this.parameterData = parameterData;
  }

  public ParameterData getParameterData()
  {
    return parameterData;
  }

  public ParameterQuery getDataFactory()
  {
    return context.getDataFactory();
  }

  public Configuration getConfiguration()
  {
    return context.getConfiguration();
  }

  public void close() throws ParameterException
  {
    context.close();
  }

  public ObjectFactory getObjectFactory()
  {
    return context.getObjectFactory();
  }

  public FormulaContext getFormulaContext()
  {
    return context.getFormulaContext();
  }
}
