package org.pentaho.reporting.engine.classic.core.parameters;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class ParameterContextWrapper implements ParameterContext
{
  private DataRow parameters;
  private ParameterContext backend;

  public ParameterContextWrapper(final ParameterContext backend,
                                  final DataRow parameters)
  {
    this.backend = backend;
    this.parameters = parameters;
  }

  public DocumentMetaData getDocumentMetaData()
  {
    return backend.getDocumentMetaData();
  }

  public ReportEnvironment getReportEnvironment()
  {
    return backend.getReportEnvironment();
  }

  public DataRow getParameterData()
  {
    return parameters;
  }

  public DataFactory getDataFactory()
  {
    return backend.getDataFactory();
  }

  public ResourceBundleFactory getResourceBundleFactory()
  {
    return backend.getResourceBundleFactory();
  }

  public ResourceKey getContentBase()
  {
    return backend.getContentBase();
  }

  public ResourceManager getResourceManager()
  {
    return backend.getResourceManager();
  }

  public Configuration getConfiguration()
  {
    return backend.getConfiguration();
  }

  public void close()
      throws ReportDataFactoryException
  {
    backend.close();
  }
}
