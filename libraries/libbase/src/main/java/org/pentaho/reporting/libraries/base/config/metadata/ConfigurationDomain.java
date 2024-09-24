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

import java.util.LinkedHashMap;

public class ConfigurationDomain {
  private LinkedHashMap<String, ConfigurationMetaDataEntry> entries;
  private boolean loaded;

  public ConfigurationDomain() {
    entries = new LinkedHashMap<String, ConfigurationMetaDataEntry>();
  }

  public boolean isLoaded() {
    return loaded;
  }

  public void setLoaded( final boolean loaded ) {
    this.loaded = loaded;
  }

  public ConfigurationMetaDataEntry get( final String key ) {
    return entries.get( key );
  }

  public ConfigurationMetaDataEntry[] getAll() {
    return entries.values().toArray( new ConfigurationMetaDataEntry[ entries.size() ] );
  }

  public void add( final ConfigurationMetaDataEntry entry ) {
    entries.put( entry.getKey(), entry );
  }
}
