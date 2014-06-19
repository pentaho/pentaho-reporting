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

package org.pentaho.reporting.ui.datasources.olap4j;

import java.awt.Dialog;
import java.awt.Frame;
import java.util.Enumeration;
import java.util.Properties;

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.SimpleDenormalizedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.JndiConnectionProvider;
import org.pentaho.reporting.ui.datasources.jdbc.connection.DriverConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JdbcConnectionDefinition;
import org.pentaho.reporting.ui.datasources.jdbc.connection.JndiConnectionDefinition;

/**
 * @author Michael D'Amour
 */
public class SimpleDenormalizedMdxDataSourceEditor extends SimpleOlap4JDataSourceEditor
{

  public SimpleDenormalizedMdxDataSourceEditor(final DesignTimeContext context)
  {
    super(context);
  }

  public SimpleDenormalizedMdxDataSourceEditor(final DesignTimeContext context, final Dialog owner)
  {
    super(context, owner);
  }

  public SimpleDenormalizedMdxDataSourceEditor(final DesignTimeContext context, final Frame owner)
  {
    super(context, owner);
  }

  protected void init(final DesignTimeContext context)
  {
    super.init(context);
    setTitle(Messages.getString("SimpleDenormalizedMdxDataSourceEditor.Title"));
  }

  protected String getDialogId()
  {
    return "Olap4JDataSourceEditor.SimpleDenormalized";
  }

  protected AbstractMDXDataFactory createDataFactory()
  {
    final JdbcConnectionDefinition connectionDefinition =
        (JdbcConnectionDefinition) getDialogModel().getConnections().getSelectedItem();

    if (connectionDefinition instanceof JndiConnectionDefinition)
    {
      final JndiConnectionDefinition jcd = (JndiConnectionDefinition) connectionDefinition;
      final JndiConnectionProvider provider = new JndiConnectionProvider();
      provider.setConnectionPath(jcd.getJndiName());
      provider.setUsername(jcd.getUsername());
      provider.setPassword(jcd.getPassword());
      return new SimpleDenormalizedMDXDataFactory(provider);
    }
    else if (connectionDefinition instanceof DriverConnectionDefinition)
    {
      final DriverConnectionDefinition dcd = (DriverConnectionDefinition) connectionDefinition;
      final DriverConnectionProvider provider = new DriverConnectionProvider();
      provider.setDriver(dcd.getDriverClass());
      provider.setUrl(dcd.getConnectionString());

      final Properties properties = dcd.getProperties();
      final Enumeration keys = properties.keys();
      while (keys.hasMoreElements())
      {
        final String key = (String) keys.nextElement();
        provider.setProperty(key, properties.getProperty(key));
      }

      return new SimpleDenormalizedMDXDataFactory(provider);
    }
    else
    {
      return null;
    }
  }
}
