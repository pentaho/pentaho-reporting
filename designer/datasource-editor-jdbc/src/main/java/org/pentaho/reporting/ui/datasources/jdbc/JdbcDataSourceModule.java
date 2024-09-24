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
 * Copyright (c) 2009 - 2017 Hitachi Vantara.  All rights reserved.
 */

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
