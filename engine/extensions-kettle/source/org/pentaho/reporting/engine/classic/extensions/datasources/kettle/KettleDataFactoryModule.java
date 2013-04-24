/*
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2008 - 2009 Pentaho Corporation, .  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.di.core.logging.CentralLogStore;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryXmlResourceFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser.KettleDataSourceReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser.KettleDataSourceXmlFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser.KettleEmbeddedTransReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser.KettleTransFromFileReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser.KettleTransformationProducerReadHandler;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.parser.KettleTransformationProducerReadHandlerFactory;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class KettleDataFactoryModule extends AbstractModule
{
  public static final String NAMESPACE = "http://jfreereport.sourceforge.net/namespaces/datasources/kettle";
  public static final String TAG_DEF_PREFIX = "org.pentaho.reporting.engine.classic.extensions.datasources.kettle.tag-def.";

  private static final Log logger = LogFactory.getLog(KettleDataFactoryModule.class);

  public KettleDataFactoryModule() throws ModuleInitializeException
  {
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
  public void initialize(final SubSystem subSystem) throws ModuleInitializeException
  {
    try
    {
      // init kettle without simplejndi
      if (KettleEnvironment.isInitialized() == false)
      {
        KettleEnvironment.init(false);
        
        // Route logging from Kettle to Apache Commons Logging...
        //
        CentralLogStore.getAppender().addLoggingEventListener( new KettleToCommonsLoggingEventListener());
      }
    }
    catch (Throwable e)
    {
      // Kettle dependencies are messed up and conflict with dpendencies from Mondrian, PMD and other projects.
      // I'm not going through and fix that now.
      logger.debug("Failed to init Kettle", e);

      // Should not happen, as there is no code in that method that could possibly raise
      // a Kettle exception.
      throw new ModuleInitializeException("Failed to initialize Kettle");
    }

    DataFactoryXmlResourceFactory.register(KettleDataSourceXmlFactoryModule.class);

    DataFactoryReadHandlerFactory.getInstance().setElementHandler(NAMESPACE, "kettle-datasource", KettleDataSourceReadHandler.class);

    KettleTransformationProducerReadHandlerFactory.getInstance().setElementHandler(NAMESPACE, "query-file", KettleTransFromFileReadHandler.class);
    KettleTransformationProducerReadHandlerFactory.getInstance().setElementHandler(NAMESPACE, "query-repository", KettleTransformationProducerReadHandler.class);
    KettleTransformationProducerReadHandlerFactory.getInstance().setElementHandler(NAMESPACE, "query-embedded", KettleEmbeddedTransReadHandler.class);

    ElementMetaDataParser.initializeOptionalDataFactoryMetaData
        ("org/pentaho/reporting/engine/classic/extensions/datasources/kettle/meta-datafactory.xml");

    // ... initialize the templated datasources ...
    try 
    {
    
      TransformationDatasourceMetadata.registerDatasources();
    
    } catch (ReportDataFactoryException e) {
      // Do not bail here... this subsystem of datasources is not core to the functioning of the 
      // Kettle datasource. 
      logger.warn("Error initializing templated datasources.", e);
    }


  }
}
