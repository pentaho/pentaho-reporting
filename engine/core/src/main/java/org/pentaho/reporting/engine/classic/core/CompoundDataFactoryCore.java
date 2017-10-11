/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.metadata.DataFactoryMetaData;
import org.pentaho.reporting.engine.classic.core.metadata.DefaultDataFactoryCore;
import org.pentaho.reporting.engine.classic.core.metadata.ResourceReference;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

public class CompoundDataFactoryCore extends DefaultDataFactoryCore {
  public CompoundDataFactoryCore() {
  }

  public String[] getReferencedFields( final DataFactoryMetaData metaData, final DataFactory element,
      final String query, final DataRow parameter ) {
    DataFactory m = getDataFactoryForQuery( element, query );
    if ( m == null ) {
      return null;
    }
    DataFactoryMetaData md = m.getMetaData();
    if ( md == null ) {
      return null;
    }
    return md.getReferencedFields( m, query, parameter );
  }

  protected DataFactory getDataFactoryForQuery( final DataFactory element, final String query ) {
    if ( element instanceof CompoundDataFactorySupport == false ) {
      return null;
    }
    CompoundDataFactorySupport cdf = (CompoundDataFactorySupport) element;
    DataFactory dataFactoryForQuery = cdf.getDataFactoryForQuery( query, false );
    if ( dataFactoryForQuery == null ) {
      dataFactoryForQuery = cdf.getDataFactoryForQuery( query, true );
      if ( dataFactoryForQuery == null ) {
        return null;
      }
    }

    return dataFactoryForQuery;
  }

  public ResourceReference[] getReferencedResources( final DataFactoryMetaData metaData, final DataFactory element,
      final ResourceManager resourceManager, final String query, final DataRow parameter ) {
    DataFactory m = getDataFactoryForQuery( element, query );
    if ( m == null ) {
      return null;
    }
    DataFactoryMetaData md = m.getMetaData();
    if ( md == null ) {
      return null;
    }
    return md.getReferencedResources( m, resourceManager, query, parameter );
  }

  public Object getQueryHash( final DataFactoryMetaData dataFactoryMetaData, final DataFactory dataFactory,
      final String queryName, final DataRow parameter ) {
    DataFactory m = getDataFactoryForQuery( dataFactory, queryName );
    if ( m == null ) {
      return null;
    }
    DataFactoryMetaData md = m.getMetaData();
    if ( md == null ) {
      return null;
    }
    return md.getQueryHash( m, queryName, parameter );
  }
}
