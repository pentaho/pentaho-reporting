package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.logging.KettleLogStore;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryXmlResourceFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser.KettleDataSourceReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser.KettleDataSourceXmlFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser.KettleEmbeddedTransReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser.KettleTransFromFileReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser
  .KettleTransformationProducerReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser
  .KettleTransformationProducerReadHandlerFactory;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializer;

public class KettleDataFactoryModuleInitializer implements ModuleInitializer {
  private static final Log logger = LogFactory.getLog( KettleDataFactoryModuleInitializer.class );

  public KettleDataFactoryModuleInitializer() {
  }

  public void performInit() throws ModuleInitializeException {
    try {
      logger.debug( "DEFAULT_PLUGIN_BASE_FOLDERS=" + Const.DEFAULT_PLUGIN_BASE_FOLDERS );

      // init kettle without simplejndi
      if ( KettleEnvironment.isInitialized() == false ) {
        KettleEnvironment.init( false );

        // Route logging from Kettle to Apache Commons Logging...
        //
        KettleLogStore.getAppender().addLoggingEventListener( new KettleToCommonsLoggingEventListener() );
      }
    } catch ( Throwable e ) {
      // Kettle dependencies are messed up and conflict with dpendencies from Mondrian, PMD and other projects.
      // I'm not going through and fix that now.
      logger.debug( "Failed to init Kettle", e );

      // Should not happen, as there is no code in that method that could possibly raise
      // a Kettle exception.
      throw new ModuleInitializeException( "Failed to initialize Kettle" );
    }

    DataFactoryXmlResourceFactory.register( KettleDataSourceXmlFactoryModule.class );

    DataFactoryReadHandlerFactory.getInstance()
      .setElementHandler( KettleDataFactoryModule.NAMESPACE, "kettle-datasource", KettleDataSourceReadHandler.class );

    KettleTransformationProducerReadHandlerFactory.getInstance()
      .setElementHandler( KettleDataFactoryModule.NAMESPACE, "query-file", KettleTransFromFileReadHandler.class );
    KettleTransformationProducerReadHandlerFactory.getInstance()
      .setElementHandler( KettleDataFactoryModule.NAMESPACE, "query-repository",
        KettleTransformationProducerReadHandler.class );
    KettleTransformationProducerReadHandlerFactory.getInstance()
      .setElementHandler( KettleDataFactoryModule.NAMESPACE, "query-embedded", KettleEmbeddedTransReadHandler.class );

    ElementMetaDataParser.initializeOptionalDataFactoryMetaData
      ( "org/pentaho/reporting/engine/classic/extensions/datasources/kettle/meta-datafactory.xml" );

    // ... initialize the templated datasources ...
    try {

      TransformationDatasourceMetadata.registerDatasources();

    } catch ( ReportDataFactoryException e ) {
      // Do not bail here... this subsystem of datasources is not core to the functioning of the
      // Kettle datasource.
      logger.warn( "Error initializing templated datasources.", e );
    }
  }
}
