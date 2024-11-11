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
