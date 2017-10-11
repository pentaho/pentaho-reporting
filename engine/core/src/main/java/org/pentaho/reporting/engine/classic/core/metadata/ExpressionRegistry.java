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

package org.pentaho.reporting.engine.classic.core.metadata;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.metadata.parser.ExpressionMetaDataCollection;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.IOException;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

public class ExpressionRegistry {
  private static final Log logger = LogFactory.getLog( ExpressionRegistry.class );
  private static ExpressionRegistry instance;

  private ConcurrentHashMap<String, ExpressionMetaData> backend;
  private ResourceManager resourceManager;

  public static synchronized ExpressionRegistry getInstance() {
    if ( instance == null ) {
      instance = new ExpressionRegistry();
    }
    return instance;
  }

  private ExpressionRegistry() {
    this.resourceManager = new ResourceManager();
    this.backend = new ConcurrentHashMap<String, ExpressionMetaData>();
  }

  public void registerFromXml( final URL expressionMetaSource ) throws IOException {
    if ( expressionMetaSource == null ) {
      throw new NullPointerException( "Error: Could not find the expression meta-data description file" );
    }
    try {
      final Resource resource =
          resourceManager.createDirectly( expressionMetaSource, ExpressionMetaDataCollection.class );
      final ExpressionMetaDataCollection typeCollection = (ExpressionMetaDataCollection) resource.getResource();
      final ExpressionMetaData[] types = typeCollection.getExpressionMetaData();
      for ( int i = 0; i < types.length; i++ ) {
        final ExpressionMetaData metaData = types[i];
        if ( metaData != null ) {
          registerExpression( metaData );
        }
      }
    } catch ( Exception e ) {
      throw new IOException( "Error: Could not parse the element meta-data description file", e );
    }
  }

  public void registerExpression( final ExpressionMetaData metaData ) {
    if ( metaData == null ) {
      throw new NullPointerException();
    }
    final Class type = metaData.getExpressionType();
    final int modifiers = type.getModifiers();
    if ( Modifier.isAbstract( modifiers ) || Modifier.isInterface( modifiers ) ) {
      throw new IllegalArgumentException( "Expression-Implementation cannot be abstract or an interface." );
    }
    this.backend.put( type.getName(), metaData );
  }

  public ExpressionMetaData[] getAllExpressionMetaDatas() {
    return backend.values().toArray( new ExpressionMetaData[backend.size()] );
  }

  public boolean isExpressionRegistered( final String identifier ) {
    if ( identifier == null ) {
      throw new NullPointerException();
    }
    return backend.containsKey( identifier );
  }

  public ExpressionMetaData getExpressionMetaData( final String identifier ) throws MetaDataLookupException {
    if ( identifier == null ) {
      throw new NullPointerException();
    }
    final ExpressionMetaData retval = backend.get( identifier );
    if ( retval == null ) {
      throw new MetaDataLookupException( "Unable to locate metadata for expression type " + identifier );
    }
    return retval;
  }
}
