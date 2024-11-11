/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core.modules.parser.ext;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.MasterReportXmlResourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.SubReportReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.SubReportXmlResourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.common.DataFactoryRefReadHandler;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.readhandlers.ExtSubReportReadHandler;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * The module definition for the extended parser module.
 *
 * @author Thomas Morgner
 */
public class ExtParserModule extends AbstractModule {
  public static final String NAMESPACE = "http://jfreereport.sourceforge.net/namespaces/reports/legacy/ext";

  /**
   * DefaultConstructor. Loads the module specification.
   *
   * @throws ModuleInitializeException
   *           if an error occured.
   */
  public ExtParserModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initalizes the module. This performs the external initialization and checks that an JAXP1.1 parser is available.
   *
   * @param subSystem
   *          the subsystem for this module.
   * @throws ModuleInitializeException
   *           if an error occured.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    if ( AbstractModule.isClassLoadable( "org.xml.sax.ext.LexicalHandler", ExtParserModule.class ) == false ) {
      throw new ModuleInitializeException( "Unable to load JAXP-1.1 classes. "
          + "Check your classpath and XML parser configuration." );
    }

    SubReportReadHandlerFactory.getInstance()
        .setElementHandler( NAMESPACE, "sub-report", ExtSubReportReadHandler.class );
    DataFactoryReadHandlerFactory.getInstance().setElementHandler( NAMESPACE, "data-factory",
        DataFactoryRefReadHandler.class );

    SubReportXmlResourceFactory.register( ExtSubReportXmlFactoryModule.class );
    MasterReportXmlResourceFactory.register( ExtReportXmlFactoryModule.class );

    performExternalInitialize( ExtParserModuleInit.class.getName(), ExtParserModule.class );
  }

}
