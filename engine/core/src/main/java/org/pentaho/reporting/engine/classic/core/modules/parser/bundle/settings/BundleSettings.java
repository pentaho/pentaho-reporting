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
