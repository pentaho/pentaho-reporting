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
 * Copyright (c) 2001 - 2009 Object Refinery Ltd, Pentaho Corporation and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.classic.extensions.datasources.xquery;

import javax.xml.xquery.XQException;

import org.pentaho.reporting.engine.classic.extensions.datasources.xquery.DriverXQConnectionProvider;

/**
 *
 */
public class DriverXQConnectionProviderTest extends RemoteTestDB
{

  public void testNoXQDataSourceSetted()
  {
    final DriverXQConnectionProvider provider = new DriverXQConnectionProvider();
    try
    {
      provider.getConnection();
      fail("Should have thrown an exception if not XQDataSource setted");
    }
    catch (XQException e)
    {
      fail("Should have thrown earlier in the code");
    }
    catch (Exception e)
    {
      // SUCCESS
    }
  }

  public void testUnsupportedJDBC()
  {
    try
    {
      final DriverXQConnectionProvider provider = new DriverXQConnectionProvider();
      provider.setXqdatasource("org.exist.xqj.EXistXQDataSource");
      provider.setDriver(DriverXQConnectionProvider.class.getName());
      provider.getConnection();
      fail("Should have throw an exception because JDBC is not yet supported");
    }
    catch (Exception e)
    {
      // SUCCESS
    }
  }

  public void testWrongDataSourceClass()
  {
    try
    {
      final DriverXQConnectionProvider provider = new DriverXQConnectionProvider();
      provider.setXqdatasource("org.exist.xqj.EXistXQDataSourceWrongName");
      provider.getConnection();
      fail("Should have throw an exception because no such class exists");
    }
    catch (Exception e)
    {
      // SUCCESS
    }
  }
}
