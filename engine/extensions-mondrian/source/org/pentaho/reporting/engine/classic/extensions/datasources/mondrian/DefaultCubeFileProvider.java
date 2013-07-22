package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.vfs.FileSystemException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

public class DefaultCubeFileProvider implements CubeFileProvider
{
  private String mondrianCubeFile;
  private String cubeConnectionName;

  public DefaultCubeFileProvider()
  {
  }

  public DefaultCubeFileProvider(final String mondrianCubeFile)
  {
    this(mondrianCubeFile, null);
  }

  public DefaultCubeFileProvider(final String mondrianCubeFile,
                                 final String cubeConnectionName)
  {
    this.mondrianCubeFile = mondrianCubeFile;
    this.cubeConnectionName = cubeConnectionName;
  }

  public String getDesignTimeFile()
  {
    return mondrianCubeFile;
  }

  public String getMondrianCubeFile()
  {
    return mondrianCubeFile;
  }

  public void setMondrianCubeFile(final String mondrianCubeFile)
  {
    this.mondrianCubeFile = mondrianCubeFile;
  }

  public String getCubeFile(final ResourceManager resourceManager, final ResourceKey contextKey) throws ReportDataFactoryException
  {
    if (mondrianCubeFile == null)
    {
      throw new ReportDataFactoryException("No schema file defined.");
    }
    final File cubeAsFile = new File(mondrianCubeFile);
    if (cubeAsFile.isFile())
    {
      return mondrianCubeFile;
    }

    try
    {
      return SchemaResolver.resolveSchema(resourceManager, contextKey, mondrianCubeFile);
    }
    catch (FileSystemException e)
    {
      return mondrianCubeFile;
    }
  }

  public String getCubeConnectionName()
  {
    return cubeConnectionName;
  }

  public void setCubeConnectionName(final String cubeConnectionName)
  {
    this.cubeConnectionName = cubeConnectionName;
  }

  public Object getConnectionHash()
  {
    final ArrayList<Object> hash = new ArrayList<Object>();
    hash.add(getClass().getName());
    hash.add(getMondrianCubeFile());
    hash.add(getCubeConnectionName());
    return hash;
  }
}
