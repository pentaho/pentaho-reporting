/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


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
