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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.Iterator;

public class DataSchemaUtility {
  private static final Log logger = LogFactory.getLog( DataSchemaUtility.class );
  private static DataSchemaDefinition cachedDataSchema;

  private DataSchemaUtility() {
  }

  public static void clearCachedDataSchema() {
    cachedDataSchema = null;
  }

  public static DataSchemaDefinition parseDefaults( final ResourceManager manager ) {
    if ( manager == null ) {
      throw new NullPointerException();
    }

    final DataSchemaDefinition fromCache = cachedDataSchema;
    if ( fromCache != null ) {
      return (DataSchemaDefinition) fromCache.clone();
    }

    final Configuration configuration = ClassicEngineBoot.getInstance().getGlobalConfig();
    final Iterator sources =
        configuration.findPropertyKeys( "org.pentaho.reporting.engine.classic.core.DataSchemaDefinition" );

    final DefaultDataSchemaDefinition definition = new DefaultDataSchemaDefinition();

    while ( sources.hasNext() ) {

      final Object sourceKey = configuration.getConfigProperty( (String) sources.next() );
      try {
        logger.debug( "Loading data-schema " + sourceKey );
        final Resource resource = manager.createDirectly( sourceKey, DataSchemaDefinition.class );
        final DataSchemaDefinition fromResource = (DataSchemaDefinition) resource.getResource();
        definition.merge( fromResource );
      } catch ( ResourceKeyCreationException e ) {
        // silently ignored .. the key was invalid
        logger.debug( "Unable to create key for schema " + sourceKey, e );
      } catch ( ResourceLoadingException e ) {
        // silently ignored .. the file could not be loaded (physical layer error)
        logger.debug( "Unable to load data for key for schema " + sourceKey, e );
      } catch ( ResourceException e ) {
        logger.debug( "Unable to load data-schema definition", e );
      }
    }
    cachedDataSchema = (DataSchemaDefinition) definition.clone();
    return definition;
  }
}
