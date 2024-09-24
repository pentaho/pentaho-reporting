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

package org.pentaho.reporting.engine.classic.extensions.datasources.scriptable;

import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryXmlResourceFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.parser.ScriptableDataSourceReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.parser.ScriptableDataSourceXmlFactoryModule;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

/**
 * Creation-Date: Jan 22, 2007, 2:10:39 PM
 *
 * @author Thomas Morgner
 */
public class ScriptableDataFactoryModule extends AbstractModule {
  public static final String NAMESPACE = "http://jfreereport.sourceforge.net/namespaces/datasources/scriptable";
  public static final String TAG_DEF_PREFIX =
      "org.pentaho.reporting.engine.classic.extensions.datasources.scriptable.tag-def.";

  public ScriptableDataFactoryModule() throws ModuleInitializeException {
    loadModuleInfo();
  }

  /**
   * Initializes the module. Use this method to perform all initial setup operations. This method is called only once in
   * a modules lifetime. If the initializing cannot be completed, throw a ModuleInitializeException to indicate the
   * error,. The module will not be available to the system.
   *
   * @param subSystem
   *          the subSystem.
   * @throws ModuleInitializeException
   *           if an error occurred while initializing the module.
   */
  public void initialize( final SubSystem subSystem ) throws ModuleInitializeException {
    DataFactoryXmlResourceFactory.register( ScriptableDataSourceXmlFactoryModule.class );

    DataFactoryReadHandlerFactory.getInstance().setElementHandler( NAMESPACE, "scriptable-datasource",
        ScriptableDataSourceReadHandler.class );

    ElementMetaDataParser
        .initializeOptionalDataFactoryMetaData( "org/pentaho/reporting/engine/classic/extensions/datasources/scriptable/meta-datafactory.xml" );

  }
}
