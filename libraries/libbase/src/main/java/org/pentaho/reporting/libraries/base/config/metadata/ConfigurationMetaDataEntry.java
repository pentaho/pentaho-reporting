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

public class ConfigurationMetaDataEntry {
  public static final String[] EMPTY_TAGS = new String[ 0 ];
  private String key;
  private boolean global;
  private boolean hidden;
  private String description;
  private String className;
  private LinkedHashMap<String, String> tags;

  public ConfigurationMetaDataEntry( final String key ) {
    this.key = key;
  }

  public String getKey() {
    return key;
  }

  public boolean isGlobal() {
    return global;
  }

  public void setGlobal( final boolean global ) {
    this.global = global;
  }

  public boolean isHidden() {
    return hidden;
  }

  public void setHidden( final boolean hidden ) {
    this.hidden = hidden;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription( final String description ) {
    this.description = description;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName( final String className ) {
    this.className = className;
  }

  public String[] getTags() {
    if ( tags == null ) {
      return EMPTY_TAGS;
    }
    return tags.keySet().toArray( new String[ tags.size() ] );
  }

  public void addTag( final String tag, final String description ) {
    if ( tag == null ) {
      throw new NullPointerException();
    }
    if ( tags == null ) {
      this.tags = new LinkedHashMap<String, String>();
    }
    tags.put( tag, description );
  }

  public void removeTag( final String tag ) {
    if ( tags == null ) {
      return;
    }
    tags.remove( tag );
  }
}
