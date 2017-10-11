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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataFactoryMetaProvider;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class DefaultDataFactoryCore implements DataFactoryCore {
  private static final Log logger = LogFactory.getLog( DefaultDataFactoryCore.class );

  public DefaultDataFactoryCore() {
  }

  public String[] getReferencedFields( final DataFactoryMetaData metaData, final DataFactory element,
      final String query, final DataRow parameter ) {
    if ( element instanceof DataFactoryMetaProvider ) {
      try {
        DataFactoryMetaProvider p = (DataFactoryMetaProvider) element;
        return p.getReferencedFields( query, parameter );
      } catch ( final ReportDataFactoryException e ) {
        logger.info( "Unable to compute design-time data: Referenced fields", e );
      }
    }
    return null;
  }

  public ResourceReference[] getReferencedResources( final DataFactoryMetaData metaData, final DataFactory element,
      final ResourceManager resourceManager, final String query, final DataRow parameter ) {
    return new ResourceReference[0];
  }

  public String getDisplayConnectionName( final DataFactoryMetaData metaData, final DataFactory dataFactory ) {
    if ( dataFactory instanceof DataFactoryMetaProvider ) {
      DataFactoryMetaProvider p = (DataFactoryMetaProvider) dataFactory;
      return p.getDisplayConnectionName();
    }
    return null;
  }

  public Object getQueryHash( final DataFactoryMetaData dataFactoryMetaData, final DataFactory dataFactory,
      final String queryName, final DataRow parameter ) {
    if ( dataFactory instanceof DataFactoryMetaProvider ) {
      try {
        DataFactoryMetaProvider p = (DataFactoryMetaProvider) dataFactory;
        return p.getReferencedFields( queryName, parameter );
      } catch ( final ReportDataFactoryException e ) {
        logger.info( "Unable to compute design-time data: Query Hash", e );
      }
    }
    return null;
  }
}
