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


package org.pentaho.reporting.libraries.resourceloader.cache;

import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.lang.ref.Reference;
import java.lang.ref.WeakReference;

/**
 * Disables caching. It simply returns null on every request and ignores the put requests. You certainly want to use one
 * of the other cache providers in real world applications.
 *
 * @author Thomas Morgner
 */
public class NullResourceDataCache implements ResourceDataCache {
  private Reference lastEntry;

  public NullResourceDataCache() {
  }

  public ResourceData put( final ResourceManager caller, final ResourceData data ) throws ResourceLoadingException {
    final ResourceData retval = CachingResourceData.createCached( data );
    lastEntry = new WeakReference( new DefaultResourceDataCacheEntry( retval, caller ) );
    return retval;
  }

  public ResourceDataCacheEntry get( final ResourceKey key ) {
    if ( lastEntry != null ) {
      final ResourceDataCacheEntry entry = (ResourceDataCacheEntry) lastEntry.get();
      if ( entry != null && key.equals( entry.getData().getKey() ) ) {
        return entry;
      }

      lastEntry = null;
    }
    return null;
  }

  public void remove( final ResourceData data ) {
  }

  public void clear() {
    lastEntry = null;
  }

  public void shutdown() {
    lastEntry = null;
  }
}
