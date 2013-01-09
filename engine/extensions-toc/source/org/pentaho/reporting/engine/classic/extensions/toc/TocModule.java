package org.pentaho.reporting.engine.classic.extensions.toc;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleElementRegistry;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.BundleNamespaces;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.LayoutDefinitionXmlFactoryModule;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.StyleDefinitionXmlFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.toc.parser.BundleIndexXmlFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.toc.parser.BundleTocXmlFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.toc.parser.IndexReadHandler;
import org.pentaho.reporting.engine.classic.extensions.toc.parser.IndexXmlResourceFactory;
import org.pentaho.reporting.engine.classic.extensions.toc.parser.TocReadHandler;
import org.pentaho.reporting.engine.classic.extensions.toc.parser.TocXmlResourceFactory;
import org.pentaho.reporting.engine.classic.extensions.toc.writer.IndexElementWriteHandler;
import org.pentaho.reporting.engine.classic.extensions.toc.writer.TocElementWriteHandler;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class TocModule extends AbstractModule
{
  public TocModule() throws ModuleInitializeException
  {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations.
   * This method is called only once in a modules lifetime. If the initializing cannot
   * be completed, throw a ModuleInitializeException to indicate the error,. The module
   * will not be available to the system.
   *
   * @param subSystem the subSystem.
   * @throws org.pentaho.reporting.libraries.base.boot.ModuleInitializeException
   *          if an error ocurred while initializing the module.
   */
  public void initialize(final SubSystem subSystem) throws ModuleInitializeException
  {
    ElementMetaDataParser.initializeOptionalExpressionsMetaData
        ("org/pentaho/reporting/engine/classic/extensions/toc/meta-expressions.xml");

    ElementMetaDataParser.initializeOptionalReportPreProcessorMetaData
        ("org/pentaho/reporting/engine/classic/extensions/toc/report-preprocessors.xml");

    ElementMetaDataParser.initializeOptionalElementMetaData
        ("org/pentaho/reporting/engine/classic/extensions/toc/meta-elements.xml");

    ElementMetaDataParser.initializeOptionalDataFactoryMetaData
        ("org/pentaho/reporting/engine/classic/extensions/toc/meta-datafactory.xml");

    BundleElementRegistry.getInstance().register("toc", TocElementWriteHandler.class);
    BundleElementRegistry.getInstance().register("index", IndexElementWriteHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "toc", TocReadHandler.class);
    BundleElementRegistry.getInstance().register(BundleNamespaces.LAYOUT, "index", IndexReadHandler.class);

    TocXmlResourceFactory.register(BundleTocXmlFactoryModule.class);
    TocXmlResourceFactory.register(LayoutDefinitionXmlFactoryModule.class);
    TocXmlResourceFactory.register(StyleDefinitionXmlFactoryModule.class);
    IndexXmlResourceFactory.register(BundleIndexXmlFactoryModule.class);
    IndexXmlResourceFactory.register(LayoutDefinitionXmlFactoryModule.class);
    IndexXmlResourceFactory.register(StyleDefinitionXmlFactoryModule.class);
  }
}
