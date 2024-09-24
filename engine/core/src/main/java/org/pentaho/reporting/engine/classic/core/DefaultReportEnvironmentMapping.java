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

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.libraries.base.config.Configuration;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

public class DefaultReportEnvironmentMapping implements ReportEnvironmentMapping {
  public static final ReportEnvironmentMapping INSTANCE = new DefaultReportEnvironmentMapping();

  private static final String ENV_MAPPING_KEY_PREFIX = "org.pentaho.reporting.engine.classic.core.env-mapping.";
  private Map<String, String> cachedEnvironmentMapping;

  public DefaultReportEnvironmentMapping() {
    preComputeEnvironmentMapping();
  }

  /**
   * Creates a ordered map that contains the environment names as keys and the data-row column names as values.
   *
   * @return the mapping from environment names to data-row column names.
   */
  public Map<String, String> createEnvironmentMapping() {
    return cachedEnvironmentMapping;
  }

  private void preComputeEnvironmentMapping() {
    if ( cachedEnvironmentMapping == null ) {
      final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
      final Iterator propertyKeys = configuration.findPropertyKeys( ENV_MAPPING_KEY_PREFIX );
      final LinkedHashMap<String, String> names = new LinkedHashMap<String, String>();
      while ( propertyKeys.hasNext() ) {
        final String key = (String) propertyKeys.next();
        final String value = configuration.getConfigProperty( key );
        final String shortKey = key.substring( ENV_MAPPING_KEY_PREFIX.length() );
        names.put( shortKey, value );
      }
      cachedEnvironmentMapping = Collections.unmodifiableMap( names );
    }
  }
}
