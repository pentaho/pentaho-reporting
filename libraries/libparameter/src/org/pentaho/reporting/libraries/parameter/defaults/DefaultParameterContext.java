package org.pentaho.reporting.libraries.parameter.defaults;

import org.pentaho.reporting.libraries.base.boot.ObjectFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.parameter.LibParameterBoot;
import org.pentaho.reporting.libraries.parameter.ParameterContext;
import org.pentaho.reporting.libraries.parameter.ParameterData;
import org.pentaho.reporting.libraries.parameter.ParameterException;
import org.pentaho.reporting.libraries.parameter.ParameterQuery;

public class DefaultParameterContext implements ParameterContext
{
  private ParameterData parameterData;
  private ParameterQuery dataFactory;
  private Configuration configuration;
  private ObjectFactory objectFactory;
  private DefaultFormulaContext formulaContext;

  public DefaultParameterContext()
  {
    parameterData = new DefaultParameterData();
    dataFactory = new EmptyParameterQuery();
    configuration = LibParameterBoot.getInstance().getGlobalConfig();
    objectFactory = LibParameterBoot.getInstance().getObjectFactory();
    formulaContext = new DefaultFormulaContext();
  }

  public ParameterData getParameterData()
  {
    return parameterData;
  }

  public ParameterQuery getDataFactory()
  {
    return dataFactory;
  }

  public Configuration getConfiguration()
  {
    return configuration;
  }

  public void close() throws ParameterException
  {

  }

  public ObjectFactory getObjectFactory()
  {
    return objectFactory;
  }

  public FormulaContext getFormulaContext()
  {
    return formulaContext;
  }

  public void setParameterData(final ParameterData parameterData)
  {
    this.parameterData = parameterData;
  }

  public void setDataFactory(final ParameterQuery dataFactory)
  {
    this.dataFactory = dataFactory;
  }

  public void setConfiguration(final Configuration configuration)
  {
    this.configuration = configuration;
  }

  public void setObjectFactory(final ObjectFactory objectFactory)
  {
    this.objectFactory = objectFactory;
  }

  public void setFormulaContext(final DefaultFormulaContext formulaContext)
  {
    this.formulaContext = formulaContext;
  }
}
