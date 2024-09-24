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
