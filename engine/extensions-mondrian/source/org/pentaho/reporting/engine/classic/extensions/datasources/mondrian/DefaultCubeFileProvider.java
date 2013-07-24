package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.io.File;
import java.util.ArrayList;

import mondrian.spi.CatalogLocator;
import org.apache.commons.vfs.FileSystemException;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

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
    return getMondrianCubeFile();
  }

  public void setDesignTimeFile(final String name)
  {
    setMondrianCubeFile(name);
  }

  public String getMondrianCubeFile()
  {
    return mondrianCubeFile;
  }

  public void setMondrianCubeFile(final String mondrianCubeFile)
  {
    this.mondrianCubeFile = mondrianCubeFile;
  }

  public String getCubeFile(final ResourceManager resourceManager,
                            final ResourceKey contextKey) throws ReportDataFactoryException
  {
    if (mondrianCubeFile == null)
    {
      throw new ReportDataFactoryException("No schema file defined.");
    }

    final CatalogLocator locator = ClassicEngineBoot.getInstance().getObjectFactory().get(CatalogLocator.class);
    if (locator != null)
    {
      final String mappedCatalog = locator.locate(mondrianCubeFile);
      if (StringUtils.isEmpty(mappedCatalog) == false)
      {
        return mappedCatalog;
      }
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
