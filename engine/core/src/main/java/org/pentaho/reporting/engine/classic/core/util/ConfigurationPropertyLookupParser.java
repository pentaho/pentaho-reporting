/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.core.util;

import org.pentaho.reporting.libraries.base.config.Configuration;

public class ConfigurationPropertyLookupParser extends PropertyLookupParser {
  private Configuration configuration;

  public ConfigurationPropertyLookupParser( final Configuration configuration ) {
    this.configuration = configuration;
  }

  public ConfigurationPropertyLookupParser() {

  }

  protected String lookupVariable( final String property ) {
    return configuration.getConfigProperty( property );
  }
}
