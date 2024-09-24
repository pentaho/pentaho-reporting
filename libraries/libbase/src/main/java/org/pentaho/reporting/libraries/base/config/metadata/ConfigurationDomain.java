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
