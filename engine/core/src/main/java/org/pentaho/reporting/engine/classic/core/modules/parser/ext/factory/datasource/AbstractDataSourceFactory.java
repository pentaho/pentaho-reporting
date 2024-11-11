/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


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
