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


package org.pentaho.reporting.engine.classic.core.modules.parser.data.external;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryXmlResourceFactory;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class ExternalDataFactoryModule extends AbstractModule {
  public static final String NAMESPACE = "http://jfreereport.sourceforge.net/namespaces/datasources/external";
  public static final String TAG_DEF_PREFIX =
      "org.pentaho.reporting.engine.classic.core.modules.parser.data.external.tag-def.";

  public ExternalDataFactoryModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem
   *          the subSystem.
   * @throws org.pentaho.reporting.libraries.base.boot.ModuleInitializeException
   *           if an error ocurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    DataFactoryXmlResourceFactory.register( ExternalResourceXmlFactoryModule.class );

    DataFactoryReadHandlerFactory.getInstance().setElementHandler( NAMESPACE, "external-datasource",
        ExternalDataSourceReadHandler.class );
  }
}
