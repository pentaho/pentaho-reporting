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
* Copyright (c) 2000 - 2013 Pentaho Corporation and Contributors...
* All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.testsupport;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.lang3.StringEscapeUtils;
import org.pentaho.reporting.engine.classic.core.metadata.AttributeRegistry;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultAttributeMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.ElementTypeRegistry;
import org.pentaho.reporting.libraries.base.boot.AbstractModule;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;
import org.pentaho.reporting.libraries.base.util.DebugLog;

public class TestSetupModule extends AbstractModule
{
  public TestSetupModule() throws ModuleInitializeException
  {
    loadModuleInfo();
  }

  public void initialize(final SubSystem subSystem) throws ModuleInitializeException
  {
    final String bundleLocation = "org.pentaho.reporting.engine.classic.core.testsupport.metadata";
    final String keyPrefix = "attribute.test-run.";
    final DefaultAttributeMetaData metaData =
        new DefaultAttributeMetaData("test-run", "test-value", bundleLocation, keyPrefix, Object.class, false, 0);

    final ElementMetaData[] types = ElementTypeRegistry.getInstance().getAllElementTypes();
    for (int i = 0; i < types.length; i++)
    {
      final ElementMetaData type = types[i];
      final AttributeRegistry attributeRegistry = ElementTypeRegistry.getInstance().getAttributeRegistry(type.getName());
      attributeRegistry.putAttributeDescription(metaData);
    }

    try
    {
      Class.forName("org.hsqldb.jdbcDriver");
      populateDatabase();
    }
    catch (Exception e)
    {
      throw new ModuleInitializeException("Failed to load the HSQL-DB driver", e);
    }
  }

  private void populateDatabase()
      throws SQLException, IOException
  {
    final Connection connection = DriverManager.getConnection("jdbc:hsqldb:mem:SampleData", "sa", "");
    connection.setAutoCommit(false);
    try
    {
      final InputStream in = new FileInputStream("sql/sampledata.script");
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
              statement.addBatch(StringEscapeUtils.unescapeJava(line));
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
          statement.executeBatch();
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

      connection.commit();
    }
    catch (FileNotFoundException fe)
    {
      DebugLog.log("Unable to populate test database, no script at ./sql/sampledata.script");
    }
    finally
    {
      connection.close();
    }
  }
}
