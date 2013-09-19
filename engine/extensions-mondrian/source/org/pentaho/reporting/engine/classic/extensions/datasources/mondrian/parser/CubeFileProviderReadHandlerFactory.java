package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.parser;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractReadHandlerFactory;

public class CubeFileProviderReadHandlerFactory extends AbstractReadHandlerFactory<CubeFileProviderReadHandler>
{
  private static final String PREFIX_SELECTOR =
      "org.pentaho.reporting.engine.classic.extensions.datasources.mondrian.cube-factory-prefix.";

  private static CubeFileProviderReadHandlerFactory readHandlerFactory;

  public CubeFileProviderReadHandlerFactory()
  {
  }

  protected Class<CubeFileProviderReadHandler> getTargetClass()
  {
    return CubeFileProviderReadHandler.class;
  }

  public static synchronized CubeFileProviderReadHandlerFactory getInstance()
  {
    if (readHandlerFactory == null)
    {
      readHandlerFactory = new CubeFileProviderReadHandlerFactory();
      readHandlerFactory.configureGlobal(ClassicEngineBoot.getInstance().getGlobalConfig(), PREFIX_SELECTOR);
    }
    return readHandlerFactory;
  }

}
