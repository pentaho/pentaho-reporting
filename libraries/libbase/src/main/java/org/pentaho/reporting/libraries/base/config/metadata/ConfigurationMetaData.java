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

package org.pentaho.reporting.libraries.base.config.metadata;

import java.util.HashMap;

public class ConfigurationMetaData {
  private HashMap<String, ConfigurationDomain> domains;
  private static ConfigurationMetaData instance;

  public static synchronized ConfigurationMetaData getInstance() {
    if ( instance == null ) {
      instance = new ConfigurationMetaData();
    }
    return instance;
  }

  private ConfigurationMetaData() {
    domains = new HashMap<String, ConfigurationDomain>();
  }

  public ConfigurationDomain getDomain( final String key ) {
    return domains.get( key );
  }

  public ConfigurationDomain createDomain( final String key ) {
    final ConfigurationDomain configurationDomain = domains.get( key );
    if ( configurationDomain != null ) {
      return configurationDomain;
    }

    final ConfigurationDomain newDomain = new ConfigurationDomain();
    domains.put( key, newDomain );
    return newDomain;
  }
}
