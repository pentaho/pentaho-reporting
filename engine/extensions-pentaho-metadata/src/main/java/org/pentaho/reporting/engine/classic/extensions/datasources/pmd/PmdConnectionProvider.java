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
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.core.modules.misc.datafactory.sql.JndiConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.pmd.loader.MetadataModelResourceFactory;
import org.pentaho.reporting.libraries.base.util.StringUtils;
import org.pentaho.reporting.libraries.resourceloader.ParameterKey;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import javax.swing.table.TableModel;
import java.io.File;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PmdConnectionProvider implements IPmdConnectionProvider {
  public PmdConnectionProvider() {
  }

  protected InputStream createStream( final ResourceManager manager,
                                      final ResourceKey contextKey,
                                      final String xmiFile ) throws ResourceException {
    if ( contextKey != null ) {
      try {
        final ResourceKey resourceKey = manager.deriveKey( contextKey, xmiFile );
        final ResourceData data = manager.load( resourceKey );
        return data.getResourceAsStream( manager );
      } catch ( ResourceException re ) {
        // ignore, lets go on to the direct parsing as a local file
      }
    }

    final ResourceKey resourceKey = manager.createKey( new File( xmiFile ) );
    final ResourceData data = manager.load( resourceKey );
    return data.getResourceAsStream( manager );
  }

  public IMetadataDomainRepository getMetadataDomainRepository( final String domainId,
                                                                final ResourceManager resourceManager,
                                                                final ResourceKey contextKey,
                                                                final String xmiFile )
    throws ReportDataFactoryException {
    if ( domainId == null ) {
      throw new NullPointerException( "Domain ID must be given and be valid." );
    }
    try {
      final Map<ParameterKey, Object> params = new HashMap<ParameterKey, Object>();
      params.put( MetadataModelResourceFactory.DOMAIN_ID, domainId );

      if ( contextKey != null ) {
        try {
          final ResourceKey key = resourceManager.deriveKey( contextKey, xmiFile, params );
          final Resource resource = resourceManager.create( key, contextKey, IMetadataDomainRepository.class );
          return (IMetadataDomainRepository) resource.getResource();
        } catch ( ResourceException re ) {
          // ignore, lets go on to the direct parsing as a local file
        }
      }

      final ResourceKey key = resourceManager.createKey( xmiFile, params );
      final Resource resource = resourceManager.create( key, contextKey, IMetadataDomainRepository.class );
      return (IMetadataDomainRepository) resource.getResource();
    } catch ( Exception e ) {
      throw new ReportDataFactoryException( "The Specified XMI File is invalid: " + xmiFile, e );
    }
  }

  public Connection createConnection( final DatabaseMeta databaseMeta,
                                      final String username,
                                      final String password ) throws ReportDataFactoryException {
    final String realUser =
      ( StringUtils.isEmpty( databaseMeta.getUsername() ) ) ? username : databaseMeta.getUsername();
    final String realPassword =
      ( StringUtils.isEmpty( databaseMeta.getPassword() ) ) ? password : databaseMeta.getPassword();

    if ( databaseMeta.getAccessType() == DatabaseMeta.TYPE_ACCESS_JNDI ) {
      final String jndiName = databaseMeta.getDatabaseName();
      if ( jndiName != null ) {
        final JndiConnectionProvider connectionProvider = new JndiConnectionProvider();
        connectionProvider.setConnectionPath( jndiName );
        try {
          return connectionProvider.createConnection( realUser, realPassword );
        } catch ( SQLException e ) {
          throw new ReportDataFactoryException
            ( "JNDI dataconnection was requested, but no connection could be established", e );
        }
      }
    }

    try {
      final String connectionInfo = databaseMeta.getURL();
      if ( connectionInfo == null ) {
        throw new ReportDataFactoryException(
          "Unable to create a connection: DatabaseMeta does not contain any driver or connection info" );
      }

      final String code = databaseMeta.getPluginId();
      final Map<String, String> map = databaseMeta.getExtraOptions();
      final Iterator<Map.Entry<String, String>> entryIterator = map.entrySet().iterator();

      final DriverConnectionProvider driverProvider = new DriverConnectionProvider();
      driverProvider.setDriver( databaseMeta.getDriverClass() );
      driverProvider.setUrl( connectionInfo );
      while ( entryIterator.hasNext() ) {
        final Map.Entry<String, String> entry = entryIterator.next();
        final String key = entry.getKey();
        final String realKey = key.substring( code.length() + 1 );
        final String value = entry.getValue();
        if ( DatabaseMeta.EMPTY_OPTIONS_STRING.equals( value ) ) {
          driverProvider.setProperty( realKey, "" );
        } else {
          driverProvider.setProperty( realKey, value );
        }
      }

      return driverProvider.createConnection( realUser, realPassword );
    } catch ( Exception e ) {
      throw new ReportDataFactoryException( "Unable to create a connection", e );
    }
  }

  @Override
  public TableModel executeQuery( final Query query, final DataRow parameters )
    throws ReportDataFactoryException {
    throw new UnsupportedOperationException
      ( "The default PmdConnectionProvider does not yet implement alternative physical model execution engines." );
  }

}
