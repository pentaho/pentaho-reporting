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

package org.pentaho.reporting.engine.classic.core.cache;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

public class DataCacheFactory {
  private static final Log logger = LogFactory.getLog( DataCacheFactory.class );
  private static DataCache dataCache;
  private static boolean noCacheDefined;

  private DataCacheFactory() {
  }

  public static synchronized void notifyCacheShutdown( final DataCache cache ) {
    if ( cache == dataCache ) {
      dataCache = null;
    }
  }

  public static synchronized DataCache getCache() {
    if ( dataCache == null && noCacheDefined == false ) {
      final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
      final String cacheImpl = config.getConfigProperty( "org.pentaho.reporting.engine.classic.core.cache.DataCache" );

      dataCache = ObjectUtilities.loadAndInstantiate( cacheImpl, DataCacheFactory.class, DataCache.class );
      if ( dataCache == null ) {
        logger.info( "Unable to create valid cache, returning <null>" );
        noCacheDefined = true;
        dataCache = null;
      }
    }
    return dataCache;
  }
}
