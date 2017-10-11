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

package org.pentaho.reporting.engine.classic.core.wizard;

import org.pentaho.reporting.libraries.base.boot.SingletonHint;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.base.util.LFUMap;

@SingletonHint
public class DefaultDataAttributeCache implements DataAttributeCache {
  private static class CacheKey {
    private final String locale;
    private final String outputType;
    private final ImmutableDataAttributes attrs;

    private CacheKey( final DataAttributeContext context,
                      final ImmutableDataAttributes attrs ) {
      this.locale = context.getLocale().toLanguageTag();
      this.outputType = context.getOutputProcessorMetaData().getExportDescriptor();
      this.attrs = attrs;
    }

    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final CacheKey cacheKey = (CacheKey) o;

      if ( locale != null ? !locale.equals( cacheKey.locale ) : cacheKey.locale != null ) {
        return false;
      }
      if ( outputType != null ? !outputType.equals( cacheKey.outputType ) : cacheKey.outputType != null ) {
        return false;
      }
      if ( attrs != null ? !attrs.equals( cacheKey.attrs ) : cacheKey.attrs != null ) {
        return false;
      }

      return true;
    }

    public int hashCode() {
      int result = locale != null ? locale.hashCode() : 0;
      result = 31 * result + ( outputType != null ? outputType.hashCode() : 0 );
      result = 31 * result + ( attrs != null ? attrs.hashCode() : 0 );
      return result;
    }

    public String toString() {
      return "CacheKey{"
        + "locale='" + locale + '\''
        + ", outputType='" + outputType + '\''
        + ", attrs=" + attrs + '}';
    }
  }

  private LFUMap<CacheKey, ImmutableDataAttributes> backend;

  public DefaultDataAttributeCache() {
    DebugLog.log( this.toString() );
    this.backend = new LFUMap<CacheKey, ImmutableDataAttributes>( 5000 );
  }

  public ImmutableDataAttributes normalize( final DataAttributes attrs,
                                                         final DataAttributeContext context ) {
    ImmutableDataAttributes key = ImmutableDataAttributes.create( attrs, context );
    CacheKey cacheKey = new CacheKey( context, key );
    synchronized ( this ) {
      ImmutableDataAttributes fromCache = backend.get( cacheKey );
      if ( fromCache == null ) {
        backend.put( cacheKey, key );
        return key;
      }
      return fromCache;
    }
  }
}
