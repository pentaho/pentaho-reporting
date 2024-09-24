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
