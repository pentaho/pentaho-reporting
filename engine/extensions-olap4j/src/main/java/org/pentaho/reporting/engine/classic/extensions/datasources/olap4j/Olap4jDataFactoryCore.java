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

package org.pentaho.reporting.engine.classic.extensions.datasources.olap4j;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.DriverConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.JndiConnectionProvider;
import org.pentaho.reporting.engine.classic.extensions.datasources.olap4j.connections.OlapConnectionProvider;

public class Olap4jDataFactoryCore extends DefaultDataFactoryCore {
  private static final Log logger = LogFactory.getLog( Olap4jDataFactoryCore.class );

  public Olap4jDataFactoryCore() {
  }

  public String getDisplayConnectionName( final DataFactoryMetaData metaData,
                                          final DataFactory dataFactory ) {
    final AbstractMDXDataFactory mdxDataFactory = (AbstractMDXDataFactory) dataFactory;
    final OlapConnectionProvider connectionProvider = mdxDataFactory.getConnectionProvider();
    if ( connectionProvider instanceof DriverConnectionProvider ) {
      final DriverConnectionProvider driverConnectionProvider = (DriverConnectionProvider) connectionProvider;
      return driverConnectionProvider.getProperty( "::pentaho-reporting::name" );
    } else if ( connectionProvider instanceof JndiConnectionProvider ) {
      final JndiConnectionProvider jndiConnectionProvider = (JndiConnectionProvider) connectionProvider;
      return jndiConnectionProvider.getConnectionPath();
    }
    return null;
  }

  public Object getQueryHash( final DataFactoryMetaData dataFactoryMetaData,
                              final DataFactory dataFactory,
                              final String queryName, final DataRow parameter ) {
    try {
      final AbstractMDXDataFactory mdxDataFactory = (AbstractMDXDataFactory) dataFactory;
      return mdxDataFactory.getQueryHash( queryName, parameter );
    } catch ( ReportDataFactoryException e ) {
      logger.warn( "Unable to create query hash", e );
      return null;
    }
  }

  public String[] getReferencedFields( final DataFactoryMetaData metaData,
                                       final DataFactory element,
                                       final String query,
                                       final DataRow parameter ) {
    try {
      final AbstractMDXDataFactory mdxDataFactory = (AbstractMDXDataFactory) element;
      return mdxDataFactory.getReferencedFields( query, parameter );
    } catch ( ReportDataFactoryException e ) {
      logger.warn( "Unable to collect referenced fields", e );
      return null;
    }
  }
}
