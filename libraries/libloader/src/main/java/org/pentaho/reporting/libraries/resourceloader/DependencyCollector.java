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

package org.pentaho.reporting.libraries.resourceloader;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Creation-Date: 08.04.2006, 14:12:14
 *
 * @author Thomas Morgner
 */
public class DependencyCollector implements Serializable, Cloneable {
  private HashMap dependencies;
  private static final long serialVersionUID = -3568774359923270189L;

  public DependencyCollector( final ResourceKey source,
                              final long version ) {
    if ( source == null ) {
      throw new NullPointerException();
    }
    dependencies = new HashMap();
    dependencies.put( source, new Long( version ) );
  }

  public ResourceKey[] getDependencies() {
    return (ResourceKey[]) dependencies.keySet().toArray
      ( new ResourceKey[ dependencies.size() ] );
  }

  public void add( final Resource dependentResource ) {
    if ( dependentResource == null ) {
      throw new NullPointerException();
    }

    final ResourceKey[] depKeys = dependentResource.getDependencies();
    for ( int i = 0; i < depKeys.length; i++ ) {
      final ResourceKey depKey = depKeys[ i ];
      final long version = dependentResource.getVersion( depKey );
      add( depKey, version );
    }
  }

  public void add( final ResourceKey resourceKey, final long version ) {
    if ( resourceKey == null ) {
      throw new NullPointerException();
    }

    dependencies.put( resourceKey, new Long( version ) );
  }

  public long getVersion( final ResourceKey key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }

    final Long l = (Long) dependencies.get( key );
    if ( l == null ) {
      return -1;
    }
    return l.longValue();
  }

  public Object clone() throws CloneNotSupportedException {
    final DependencyCollector dc = (DependencyCollector) super.clone();
    dc.dependencies = (HashMap) dependencies.clone();
    return dc;
  }
}
