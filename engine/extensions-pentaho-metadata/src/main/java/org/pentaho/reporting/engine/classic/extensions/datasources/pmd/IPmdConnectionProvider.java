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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.extensions.datasources.pmd;

import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.metadata.query.model.Query;
import org.pentaho.metadata.repository.IMetadataDomainRepository;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.TableModel;
import java.io.Serializable;
import java.sql.Connection;

/**
 * A stateless connection provider. It is used to replace the way connections get created and how the metadata
 * repository is located.
 */
public interface IPmdConnectionProvider extends Serializable {
  public Connection createConnection( DatabaseMeta databaseMeta,
                                      final String username,
                                      final String password ) throws ReportDataFactoryException;

  public IMetadataDomainRepository getMetadataDomainRepository( String domain,
                                                                ResourceManager resourceManager,
                                                                ResourceKey contextKey,
                                                                String xmiFile ) throws ReportDataFactoryException;

  /**
   * This brokers execution of a query to the connection provider, it is used for non-SQL based Pentaho Metadata
   * datasources
   */
  public TableModel executeQuery( final Query query, final DataRow parameters ) throws ReportDataFactoryException;

}
