/*!
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
* Copyright (c) 2002-2013 Pentaho Corporation..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.sampledata;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaDataParser;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializer;
import org.pentaho.reporting.libraries.base.config.Configuration;

public class SampleDataModuleInitializer implements ModuleInitializer
{
  private static final Log logger = LogFactory.getLog(SampleDataModuleInitializer.class);

  public SampleDataModuleInitializer()
  {
  }

  /**
   * Performs the initalization of the module.
   *
   * @throws ModuleInitializeException if an error occurs which prevents the module
   *                                   from being usable.
   */
  public void performInit() throws ModuleInitializeException
  {
    try
    {
      Class.forName("org.hsqldb.jdbcDriver");
      populateDatabase();
    }
    catch (Exception e)
    {
      throw new ModuleInitializeException("Failed to load the HSQL-DB driver", e);
    }

    ElementMetaDataParser.initializeOptionalDataFactoryMetaData
        ("org/pentaho/reporting/engine/classic/extensions/datasources/sampledata/meta-datafactory.xml");

  }

  private void populateDatabase()
      throws SQLException, IOException
  {
    final Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:SampleData", "sa", "");
    try
    {
      final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
      final String location = config.getConfigProperty
          ("org.pentaho.reporting.engine.classic.extensions.datasources.sampledata.SampleDataLocation");
      final InputStream in = SampleDataModule.class.getResourceAsStream(location);
      if (in == null)
      {
        logger.warn("Invalid database init-script specified. Sample database will be empty. [" + location + "]");
        return;
      }
      final InputStreamReader inReader = new InputStreamReader(in);
      final BufferedReader bin = new BufferedReader(inReader);
      try
      {
        final Statement statement = connection.createStatement();
        try
        {
          String line;
          while ((line = bin.readLine()) != null)
          {
            try
            {
              statement.execute(StringEscapeUtils.unescapeJava(line));
            }
            catch (SQLException e)
            {
              if (line.startsWith("CREATE SCHEMA ") ||
                  line.startsWith("CREATE USER SA ") ||
                  line.startsWith("GRANT DBA TO SA"))
              {
                // ignore the error, HSQL sucks
              }
              else
              {
                throw e;
              }
            }
          }
        }
        finally
        {
          statement.close();
        }
      }
      finally
      {
        bin.close();
      }
    }
    finally
    {
      connection.close();
    }
  }
}
