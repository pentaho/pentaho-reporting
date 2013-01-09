package org.pentaho.reporting.engine.classic.extensions.datasources.cda;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryXmlResourceFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.parser.CdaDataSourceReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.cda.parser.CdaDataSourceXmlFactoryModule;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class CdaModule extends AbstractModule
{
  public static final String NAMESPACE = "http://jfreereport.sourceforge.net/namespaces/datasources/cda";
  public static final String TAG_DEF_PREFIX = "org.pentaho.reporting.engine.classic.extensions.datasources.cda.tag-def.";

  public CdaModule() throws ModuleInitializeException
  {
    loadModuleInfo();
  }

  public void initialize(final SubSystem subSystem) throws ModuleInitializeException
  {
    DataFactoryXmlResourceFactory.register(CdaDataSourceXmlFactoryModule.class);

    DataFactoryReadHandlerFactory.getInstance().setElementHandler(NAMESPACE, "cda-datasource", CdaDataSourceReadHandler.class);

    ElementMetaDataParser.initializeOptionalDataFactoryMetaData
        ("org/pentaho/reporting/engine/classic/extensions/datasources/cda/meta-datafactory.xml");

  }
}
