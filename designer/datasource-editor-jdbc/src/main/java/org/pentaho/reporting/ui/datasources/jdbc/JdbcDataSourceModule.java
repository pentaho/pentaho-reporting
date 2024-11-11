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


package org.pentaho.reporting.ui.datasources.jdbc;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.di.core.KettleEnvironment;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class JdbcDataSourceModule extends AbstractModule
{
  public static final String MESSAGES = "org.pentaho.reporting.ui.datasources.jdbc.messages";
  private static Log logger = LogFactory.getLog(JdbcDataSourceModule.class);

  public JdbcDataSourceModule() throws ModuleInitializeException
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
      }
    }
    catch (Throwable e)
    {
      // Kettle dependencies are messed up and conflict with dependencies from Mondrian, PMD and other projects.
      // I'm not going through and fix that now.
      logger.debug("Failedt to init Datasource dialog dependencies", e);

      // Should not happen, as there is no code in that method that could possibly raise
      // a Kettle exception.
      throw new ModuleInitializeException("Failed to initialize Kettle");
    }

  }
}
