package org.pentaho.reporting.designer.core.editor.drilldown.parser;

import org.pentaho.reporting.designer.core.ReportDesignerBoot;
import org.pentaho.reporting.designer.core.editor.drilldown.model.ParameterDocument;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.xmlns.parser.AbstractXmlResourceFactory;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModule;
import org.pentaho.reporting.libraries.xmlns.parser.XmlFactoryModuleRegistry;

public class ParameterDocumentResourceFactory extends AbstractXmlResourceFactory
{
  private static final XmlFactoryModuleRegistry registry = new XmlFactoryModuleRegistry();

  public static void register(final Class<? extends XmlFactoryModule> readHandler)
  {
    registry.register(readHandler);
  }

  public ParameterDocumentResourceFactory()
  {
  }

  public void initializeDefaults()
  {
    super.initializeDefaults();
    final XmlFactoryModule[] registeredHandlers = registry.getRegisteredHandlers();
    for (int i = 0; i < registeredHandlers.length; i++)
    {
      registerModule(registeredHandlers[i]);
    }
  }

  /**
   * Returns the configuration that should be used to initialize this factory.
   *
   * @return the configuration for initializing the factory.
   */
  protected Configuration getConfiguration()
  {
    return ReportDesignerBoot.getInstance().getGlobalConfig();
  }

  /**
   * Returns the expected result type.
   *
   * @return the result type.
   */
  public Class getFactoryType()
  {
    return ParameterDocument.class;
  }
}
