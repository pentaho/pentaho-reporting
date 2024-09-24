/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

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
