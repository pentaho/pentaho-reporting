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

import org.pentaho.reporting.libraries.resourceloader.ResourceBundleData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * Disables caching. It simply returns null on every request and ignores the put requests. You certainly want to use one
 * of the other cache providers in real world applications.
 *
 * @author Thomas Morgner
 */
public class NullResourceBundleDataCache implements ResourceBundleDataCache {
  private Object lastEntry;

  public NullResourceBundleDataCache() {
  }

  public ResourceBundleData put( final ResourceManager caller, final ResourceBundleData data )
    throws ResourceLoadingException {
    final ResourceBundleData retval = CachingResourceBundleData.createCached( data );
    lastEntry = new DefaultResourceBundleDataCacheEntry( retval, caller );
    return retval;
  }

  public ResourceBundleDataCacheEntry get( final ResourceKey key ) {
    if ( lastEntry != null ) {
      final ResourceBundleDataCacheEntry entry = (ResourceBundleDataCacheEntry) lastEntry;
      if ( key.equals( entry.getData().getBundleKey() ) ) {
        return entry;
      }
      lastEntry = null;
    }
    return null;
  }

  public void remove( final ResourceBundleData data ) {
  }

  public void clear() {
    lastEntry = null;
  }

  public void shutdown() {
    lastEntry = null;
  }
}
