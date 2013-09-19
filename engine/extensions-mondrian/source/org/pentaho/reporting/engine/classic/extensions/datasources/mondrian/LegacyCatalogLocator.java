package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import mondrian.spi.CatalogLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;

public class LegacyCatalogLocator implements CatalogLocator
{
  private static final Log logger = LogFactory.getLog(LegacyCatalogLocator.class);
  private Properties mapping;

  public LegacyCatalogLocator()
  {
    mapping = load();
  }

  public Properties load()
  {
    final URL resource = getClass().getResource("/mondrian-schema-mapping.properties");
    if (resource == null)
    {
      logger.debug("Unable to locate properties at '/mondrian-schema-mapping.properties'");
      return new Properties();
    }

    final Properties p = new Properties();
    try
    {
      final InputStream inStream = resource.openStream();
      try
      {
        p.load(inStream);
      }
      finally
      {
        inStream.close();
      }
    }
    catch (IOException e)
    {
      logger.debug("Failed to parse mapping", e);
    }
    return p;
  }

  public String locate(final String s)
  {
    final String fileName = IOUtils.getInstance().getFileName(s);
    final String mapped = mapping.getProperty(fileName);
    if (StringUtils.isEmpty(mapped))
    {
      return null;
    }
    return mapped;
  }
}
