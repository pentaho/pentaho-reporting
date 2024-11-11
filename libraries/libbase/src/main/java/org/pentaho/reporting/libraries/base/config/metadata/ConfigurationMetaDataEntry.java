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
