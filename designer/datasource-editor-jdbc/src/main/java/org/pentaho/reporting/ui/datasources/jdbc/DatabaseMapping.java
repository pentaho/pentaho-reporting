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

import org.pentaho.di.core.database.DatabaseInterface;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.database.GenericDatabaseMeta;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;

public class DatabaseMapping
{
  private DatabaseMapping()
  {
  }

  public static DatabaseInterface getMappingForDriver(final String driverClass)
  {
    if (driverClass == null || driverClass.length() == 0)
    {
      return getGenericInterface();
    }
    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    final String mappedInstance = configuration.getConfigProperty
        ("org.pentaho.reporting.ui.datasources.jdbc.driver-mapping." + driverClass);
    final DatabaseInterface[] interfaces = DatabaseMeta.getDatabaseInterfaces();
    for (int i = 0; i < interfaces.length; i++)
    {
      final DatabaseInterface databaseInterface = interfaces[i];
      if (databaseInterface.getClass().getName().equals(mappedInstance))
      {
        return databaseInterface;
      }
    }
    return mapTypeFromDriver(driverClass);
  }

  public static DatabaseInterface getGenericInterface()
  {
    // as kettle objects are so easy to break, we have to get the references from the public arrays out
    // of kettle. There is no safe way (since numeric ids are deprecated) to actually reference a database
    // meta object without hardcoding its ID.
    final DatabaseInterface[] interfaces = DatabaseMeta.getDatabaseInterfaces();
    for (int i = 0; i < interfaces.length; i++)
    {
      final DatabaseInterface anInterface = interfaces[i];
      if ("GENERIC".equals(anInterface.getPluginId()))
      {
        return anInterface;
      }
    }
    return null;
  }

  private static DatabaseInterface mapTypeFromDriver(final String driverClass)
  {
    if (driverClass == null)
    {
      return new GenericDatabaseMeta();
    }

    final DatabaseInterface[] interfaces = DatabaseMeta.getDatabaseInterfaces();
    for (int i = 0; i < interfaces.length; i++)
    {
      final DatabaseInterface dbi = interfaces[i];
      final int[] accessTypeList = dbi.getAccessTypeList();
      for (int j = 0; j < accessTypeList.length; j++)
      {
        final int al = accessTypeList[j];
        if (al != DatabaseMeta.TYPE_ACCESS_ODBC)
        {
          dbi.setAccessType(al);
          if (driverClass.equals(dbi.getDriverClass()))
          {
            return dbi;
          }
        }
      }
    }

    return new GenericDatabaseMeta();
  }

}
