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

package org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.datasource;

import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ClassFactoryImpl;
import org.pentaho.reporting.engine.classic.core.modules.parser.ext.factory.base.ObjectDescription;

import java.util.HashMap;
import java.util.Iterator;

/**
 * A base class for implementing the {@link DataSourceFactory} interface.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractDataSourceFactory extends ClassFactoryImpl implements DataSourceFactory {
  /**
   * Storage for the data sources.
   */
  private final HashMap dataSources;

  /**
   * Creates a new factory.
   */
  protected AbstractDataSourceFactory() {
    dataSources = new HashMap();
  }

  /**
   * Registers a data source.
   *
   * @param name
   *          the name.
   * @param o
   *          the object description.
   */
  public void registerDataSources( final String name, final ObjectDescription o ) {
    dataSources.put( name, o );
    registerClass( o.getObjectClass(), o );
  }

  /**
   * Returns a data source description.
   *
   * @param name
   *          the data source name.
   * @return The object description.
   */
  public ObjectDescription getDataSourceDescription( final String name ) {
    final ObjectDescription od = (ObjectDescription) dataSources.get( name );
    if ( od != null ) {
      return od.getInstance();
    }
    return null;
  }

  /**
   * Returns a data source name given a description.
   *
   * @param od
   *          the object description.
   * @return The name.
   */
  public String getDataSourceName( final ObjectDescription od ) {
    final Iterator keys = dataSources.keySet().iterator();
    while ( keys.hasNext() ) {
      final String key = (String) keys.next();
      final ObjectDescription ds = (ObjectDescription) dataSources.get( key );
      if ( ds.getObjectClass().equals( od.getObjectClass() ) ) {
        return key;
      }
    }
    return null;
  }

  /**
   * Returns the names of all registered datasources as iterator.
   *
   * @return the registered names.
   */
  public Iterator getRegisteredNames() {
    return dataSources.keySet().iterator();
  }
}
