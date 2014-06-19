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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.modules.parser.data.sql;

import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryReadHandlerFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.base.DataFactoryXmlResourceFactory;
import org.pentaho.reporting.engine.classic.core.modules.parser.data.compounddata.CompoundDataFactoryResourceXmlFactoryModule;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class SQLDataFactoryModule extends AbstractModule
{
  public static final String NAMESPACE =
      "http://jfreereport.sourceforge.net/namespaces/datasources/sql";

  public static final String CONNECTION_WRITER_PREFIX =
      "org.pentaho.reporting.engine.classic.core.modules.parser.data.sql.writer.handler.sql-connection-provider.";

  public SQLDataFactoryModule() throws ModuleInitializeException
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
    DataFactoryXmlResourceFactory.register(DirectSQLResourceXmlFactoryModule.class);
    DataFactoryXmlResourceFactory.register(SQLResourceXmlFactoryModule.class);

    DataFactoryReadHandlerFactory.getInstance().setElementHandler(NAMESPACE, "sql-datasource", SQLDataSourceReadHandler.class);
    DataFactoryReadHandlerFactory.getInstance().setElementHandler(NAMESPACE, "direct-sql-datasource", SQLDirectDataSourceReadHandler.class);
    ConnectionReadHandlerFactory.getInstance().setElementHandler(NAMESPACE, "connection", DriverConnectionReadHandler.class);
    ConnectionReadHandlerFactory.getInstance().setElementHandler(NAMESPACE, "jndi", JndiConnectionReadHandler.class);
  }
}
