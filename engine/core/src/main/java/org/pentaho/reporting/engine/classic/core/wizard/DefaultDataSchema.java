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


package org.pentaho.reporting.engine.classic.core.wizard;

import java.util.LinkedHashMap;

public class DefaultDataSchema implements DataSchema {
  private LinkedHashMap<String, DataAttributes> mapping;
  private DataAttributes tableAttributes;

  public DefaultDataSchema() {
    this.tableAttributes = new DefaultDataAttributes();
    this.mapping = new LinkedHashMap<String, DataAttributes>();
  }

  public void setTableAttributes( final DataAttributes tableAttributes ) throws CloneNotSupportedException {
    if ( tableAttributes == null ) {
      throw new NullPointerException();
    }
    this.tableAttributes = (DataAttributes) tableAttributes.clone();
  }

  public DataAttributes getTableAttributes() {
    return tableAttributes;
  }

  public void setAttributes( final String name, final DataAttributes attributes ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    if ( attributes == null ) {
      this.mapping.remove( name );
    } else {
      this.mapping.put( name, attributes );
    }
  }

  public DataAttributes getAttributes( final String name ) {
    if ( name == null ) {
      throw new NullPointerException();
    }
    return mapping.get( name );
  }

  public Object clone() throws CloneNotSupportedException {
    final DefaultDataSchema dataSchema = (DefaultDataSchema) super.clone();
    dataSchema.mapping = (LinkedHashMap<String, DataAttributes>) mapping.clone();
    return dataSchema;
  }

  public String[] getNames() {
    return mapping.keySet().toArray( new String[mapping.size()] );
  }
}
