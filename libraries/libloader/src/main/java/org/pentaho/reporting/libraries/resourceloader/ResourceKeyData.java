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
* Copyright (c) 2008 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Data class which holds the pieces of the String version of the Resource Key.
 *
 * @author David M. Kincade
 */
public class ResourceKeyData {
  private String schema;
  private String identifier;
  private Map<ParameterKey, Object> factoryParameters;

  public ResourceKeyData( final String schema, final String identifier,
                          final Map<ParameterKey, Object> factoryParameters ) {
    this.schema = schema;
    this.identifier = identifier;
    if ( factoryParameters == null ) {
      this.factoryParameters = null;
    } else {
      this.factoryParameters = Collections.unmodifiableMap( new HashMap<ParameterKey, Object>( factoryParameters ) );
    }
  }

  public String getSchema() {
    return schema;
  }

  public String getIdentifier() {
    return identifier;
  }

  public Map<ParameterKey, Object> getFactoryParameters() {
    return factoryParameters;
  }
}
