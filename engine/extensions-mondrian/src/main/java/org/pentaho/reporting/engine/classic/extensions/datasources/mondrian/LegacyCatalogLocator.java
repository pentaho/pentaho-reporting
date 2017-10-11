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

package org.pentaho.reporting.engine.classic.extensions.datasources.mondrian;

import mondrian.spi.CatalogLocator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

public class LegacyCatalogLocator implements CatalogLocator {
  private static final Log logger = LogFactory.getLog( LegacyCatalogLocator.class );
  private Properties mapping;

  public LegacyCatalogLocator() {
    mapping = load();
  }

  public Properties load() {
    final URL resource = getClass().getResource( "/mondrian-schema-mapping.properties" );
    if ( resource == null ) {
      logger.debug( "Unable to locate properties at '/mondrian-schema-mapping.properties'" );
      return new Properties();
    }

    final Properties p = new Properties();
    try {
      final InputStream inStream = resource.openStream();
      try {
        p.load( inStream );
      } finally {
        inStream.close();
      }
    } catch ( IOException e ) {
      logger.debug( "Failed to parse mapping", e );
    }
    return p;
  }

  public String locate( final String s ) {
    final String fileName = IOUtils.getInstance().getFileName( s );
    final String mapped = mapping.getProperty( fileName );
    if ( StringUtils.isEmpty( mapped ) ) {
      return null;
    }
    return mapped;
  }
}
