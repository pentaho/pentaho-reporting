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

import org.pentaho.reporting.engine.classic.core.designtime.DesignTimeContext;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.AbstractNamedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.DenormalizedMDXDataFactory;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;

/**
 * @author Michael D'Amour
 */
public class DenormalizedMdxDataSourceEditor extends Olap4JDataSourceEditor
{

  public DenormalizedMdxDataSourceEditor(final DesignTimeContext context)
  {
    super(context);
  }

  public DenormalizedMdxDataSourceEditor(final DesignTimeContext context, final Dialog owner)
  {
    super(context, owner);
  }

  public DenormalizedMdxDataSourceEditor(final DesignTimeContext context, final Frame owner)
  {
    super(context, owner);
  }

  protected void init(final DesignTimeContext context)
  {
    super.init(context);
    setTitle(Messages.getString("DenormalizedMdxDataSourceEditor.Title"));
  }

  protected String getDialogId()
  {
    return "Olap4JDataSourceEditor.Denormalized";
  }

  protected AbstractNamedMDXDataFactory createDataFactory()
  {
    final OlapConnectionProvider connectionProvider = createConnectionProvider();
    if (connectionProvider == null)
    {
      return null;
    }
    final DenormalizedMDXDataFactory returnDataFactory = new DenormalizedMDXDataFactory(connectionProvider);
    configureQueries(returnDataFactory);
    return returnDataFactory;

  }
}
