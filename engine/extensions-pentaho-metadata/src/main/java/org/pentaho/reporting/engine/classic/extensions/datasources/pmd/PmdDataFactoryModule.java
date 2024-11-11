/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.extensions.datasources.pmd;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryXmlResourceFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.parser.PmdConfigReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.parser.PmdConfigReadHandlerFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.parser.PmdDataSourceReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.parser.PmdDataSourceXmlFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.parser.SimplePmdDataSourceReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.parser.SimplePmdDataSourceXmlFactoryModule;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class PmdDataFactoryModule extends AbstractModule {
  public static final String NAMESPACE = "http://reporting.pentaho.org/namespaces/datasources/pmd";
  public static final String META_DOMAIN =
    "http://reporting.pentaho.org/namespaces/engine/meta-attributes/pentaho-meta-data";
  public static final String TAG_DEF_PREFIX =
    "org.pentaho.reporting.engine.classic.extensions.datasources.pmd.tag-def.";
  private static final Log logger = LogFactory.getLog( PmdDataFactoryModule.class );

  public PmdDataFactoryModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem the subSystem.
   * @throws ModuleInitializeException if an error ocurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    try {
      // init kettle without simplejndi
      if ( KettleEnvironment.isInitialized() == false ) {
        KettleEnvironment.init( false );
      }
    } catch ( Throwable e ) {
      // Kettle dependencies are messed up and conflict with dpendencies from Mondrian, PMD and other projects.
      // I'm not going through and fix that now.
      logger.debug( "Failed to init Pentaho Metadata", e );

      // Should not happen, as there is no code in that method that could possibly raise
      // a Kettle exception.
      throw new ModuleInitializeException( "Failed to initialize Kettle" );
    }

    DataFactoryXmlResourceFactory.register( PmdDataSourceXmlFactoryModule.class );
    DataFactoryXmlResourceFactory.register( SimplePmdDataSourceXmlFactoryModule.class );

    DataFactoryReadHandlerFactory.getInstance()
      .setElementHandler( NAMESPACE, "pmd-datasource", PmdDataSourceReadHandler.class );
    DataFactoryReadHandlerFactory.getInstance()
      .setElementHandler( NAMESPACE, "simple-pmd-datasource", SimplePmdDataSourceReadHandler.class );

    PmdConfigReadHandlerFactory.getInstance().setElementHandler( NAMESPACE, "config", PmdConfigReadHandler.class );

    ElementMetaDataParser.initializeOptionalDataFactoryMetaData
      ( "org/pentaho/reporting/engine/classic/extensions/datasources/pmd/meta-datafactory.xml" );
  }
}
