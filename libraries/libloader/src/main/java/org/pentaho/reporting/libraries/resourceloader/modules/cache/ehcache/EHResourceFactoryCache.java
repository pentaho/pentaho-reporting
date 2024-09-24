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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader.modules.cache.ehcache;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheException;
import net.sf.ehcache.Element;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.cache.ResourceFactoryCache;

public class EHResourceFactoryCache implements ResourceFactoryCache {
  private static class CompoundCacheKey {
    private ResourceKey key;
    private Class target;

    public CompoundCacheKey( final ResourceKey key, final Class target ) {
      if ( key == null ) {
        throw new NullPointerException();
      }
      if ( target == null ) {
        throw new NullPointerException();
      }
      this.key = key;
      this.target = target;
    }

    public ResourceKey getKey() {
      return key;
    }

    public Class getTarget() {
      return target;
    }

    public void setTarget( final Class target ) {
      this.target = target;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final CompoundCacheKey that = (CompoundCacheKey) o;

      if ( !target.equals( that.target ) ) {
        return false;
      }
      if ( !key.equals( that.key ) ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = key.hashCode();
      result = 31 * result + target.hashCode();
      return result;
    }

    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append( "CompoundCacheKey" );
      sb.append( "{key=" ).append( key );
      sb.append( ", target=" ).append( target );
      sb.append( '}' );
      return sb.toString();
    }
  }

  private static final Log logger = LogFactory.getLog( EHResourceFactoryCache.class );
  private Cache factoryCache;

  public EHResourceFactoryCache( final Cache factoryCache ) {
    if ( factoryCache == null ) {
      throw new NullPointerException();
    }
    this.factoryCache = factoryCache;
  }

  public Resource get( final ResourceKey key, final Class[] target ) {
    for ( int i = 0; i < target.length; i++ ) {
      final Resource res = getInternal( key, target[ i ] );
      if ( res != null ) {
        if ( EHCacheModule.CACHE_MONITOR.isDebugEnabled() ) {
          EHCacheModule.CACHE_MONITOR.debug( "Res  Cache Hit  " + key );
        }
        return res;
      }
    }
    if ( EHCacheModule.CACHE_MONITOR.isDebugEnabled() ) {
      EHCacheModule.CACHE_MONITOR.debug( "Res  Cache Miss  " + key );
    }
    return null;
  }

  private Resource getInternal( final ResourceKey key, final Class target ) {
    try {
      final Element element = factoryCache.get( new CompoundCacheKey( key, target ) );
      if ( element != null ) {
        final Resource resource = (Resource) element.getObjectValue();
        if ( resource != null ) {
          return resource;
        }
        final Resource resource1 = (Resource) element.getValue();
        if ( resource1 != null ) {
          return resource1;
        }
        return null;
      } else {
        return null;
      }
    } catch ( CacheException e ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Failed to retrieve resource for key " + key, e );
      }
      return null;
    }
  }

  public void put( final Resource resource ) {
    final Object source = new CompoundCacheKey( resource.getSource(), resource.getTargetType() );
    try {
      factoryCache.put( new Element( source, (Object) resource ) );
    } catch ( Exception e ) {
      if ( logger.isDebugEnabled() ) {
        logger.debug( "Failed to store resource for key " + source, e );
      }
      // ignore ... the object is not serializable ..
    }
  }

  public void remove( final Resource resource ) {
    final Object source = new CompoundCacheKey( resource.getSource(), resource.getTargetType() );
    factoryCache.remove( source );
  }

  public void clear() {
    try {
      factoryCache.removeAll();
    } catch ( Exception e ) {
      logger.debug( "Clearing cache failed", e );
      // ignore ..
    }
  }

  public void shutdown() {
    try {
      factoryCache.getCacheManager().shutdown();
    } catch ( Exception e ) {
      logger.debug( "Failed to shut-down cache", e );
      // ignore it ..
    }
  }
}
