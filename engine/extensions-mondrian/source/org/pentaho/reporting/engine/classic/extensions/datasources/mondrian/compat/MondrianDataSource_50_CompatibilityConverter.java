package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.compat;

import org.pentaho.reporting.engine.classic.core.AbstractReportDefinition;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.designtime.compat.AbstractCompatibilityConverter;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.CubeFileProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.MondrianUtil;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class MondrianDataSource_50_CompatibilityConverter extends AbstractCompatibilityConverter
{
  public MondrianDataSource_50_CompatibilityConverter()
  {
  }

  public int getTargetVersion()
  {
    return ClassicEngineBoot.computeVersionId(5, 0, 0);
  }

  public void inspectDataSource(final AbstractReportDefinition report, final DataFactory dataFactory)
  {
    if (!(dataFactory instanceof AbstractMDXDataFactory))
    {
      return;
    }

    final AbstractMDXDataFactory mdxDataFactory = (AbstractMDXDataFactory) dataFactory;
    final CubeFileProvider cubeFileProvider = mdxDataFactory.getCubeFileProvider();
    if (StringUtils.isEmpty(cubeFileProvider.getCubeConnectionName()) == false)
    {
      // there is a cube-name for it already. Must be a developer sample or a new report
      return;
    }

    final ResourceManager mgr = report.getResourceManager();
    final ResourceKey reportContentBase = report.getContentBase();
    final String cubeName = calculateCubeNameFromProvider (mgr, reportContentBase, cubeFileProvider);

    cubeFileProvider.setCubeConnectionName(cubeName);
  }

  private String calculateCubeNameFromProvider(final ResourceManager mgr,
                                               final ResourceKey reportContentBase,
                                               final CubeFileProvider defaultCubeFileProvider)
  {
    final String designTimeFile = defaultCubeFileProvider.getDesignTimeFile();
    if (StringUtils.isEmpty(designTimeFile))
    {
      return null;
    }
    return MondrianUtil.parseSchemaName(mgr, reportContentBase, designTimeFile);
  }
}
