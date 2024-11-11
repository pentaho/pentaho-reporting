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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.settings;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.config.DefaultConfiguration;

import java.util.Enumeration;

/**
 * Todo: Document Me
 *
 * @author Thomas Morgner
 */
public class BundleSettings implements Cloneable {
  private DefaultConfiguration configuration;

  public BundleSettings( final Configuration configuration ) {
    if ( configuration == null ) {
      throw new NullPointerException();
    }

    final DefaultConfiguration defConf = new DefaultConfiguration();
    final Enumeration configProperties = configuration.getConfigProperties();
    while ( configProperties.hasMoreElements() ) {
      final String key = (String) configProperties.nextElement();
      final String value = configuration.getConfigProperty( key );
      if ( value != null ) {
        defConf.setConfigProperty( key, value );
      }
    }
    this.configuration = defConf;
  }

  public Configuration getConfiguration() {
    return (Configuration) configuration.clone();
  }

  public Object clone() throws CloneNotSupportedException {
    final BundleSettings settings = (BundleSettings) super.clone();
    settings.configuration = (DefaultConfiguration) configuration.clone();
    return settings;
  }
}
