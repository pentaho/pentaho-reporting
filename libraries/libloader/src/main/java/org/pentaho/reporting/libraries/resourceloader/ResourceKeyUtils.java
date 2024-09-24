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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.base.util.CSVQuoter;
import org.pentaho.reporting.libraries.base.util.CSVTokenizer;
import org.pentaho.reporting.libraries.base.util.IOUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Provides a setup of utility methods for operating on a ResourceKey class.
 *
 * @author David Kincade
 */
public class ResourceKeyUtils {
  private static final String DELIMITER = ";";
  public static final String SERIALIZATION_PREFIX = "resourcekey:";
  private static final Log logger = LogFactory.getLog( ResourceManager.class );

  /**
   * Returns a string representation of the ResourceKey based on the pieces that are passed as parameters
   *
   * @param schema            the string representation of the schema
   * @param identifier        the string representation of the identifier
   * @param factoryParameters the set of factory parameters (<code>null</code> allowed)
   * @return the string version with the pieces delimited and concatenated
   */
  public static String createStringResourceKey( final String schema,
                                                final String identifier,
                                                final Map factoryParameters ) {
    final String factoryParamString = convertFactoryParametersToString( factoryParameters );
    final CSVQuoter quoter = new CSVQuoter( ';' );
    return quoter.doQuoting( SERIALIZATION_PREFIX + schema ) + DELIMITER +
      quoter.doQuoting( identifier ) +
      ( factoryParamString == null ? "" : DELIMITER + quoter.doQuoting( factoryParamString ) );
  }

  /**
   * Parses the string version of the Resource Key into the components
   *
   * @return
   */
  public static ResourceKeyData parse( final String resourceKeyString ) throws ResourceKeyCreationException {
    if ( resourceKeyString == null ) {
      throw new IllegalArgumentException( "Source string can not be null" );
    }
    if ( !resourceKeyString.startsWith( SERIALIZATION_PREFIX ) ) {
      throw new ResourceKeyCreationException( "The source string does not start with the string ["
        + SERIALIZATION_PREFIX + "]" );
    }

    final CSVTokenizer tokenizer = new CSVTokenizer( resourceKeyString, DELIMITER, "\"", false );
    if ( tokenizer.hasMoreElements() == false ) {
      throw new ResourceKeyCreationException( "Schema is missing" );
    }
    final String rawSchema = tokenizer.nextToken();
    if ( rawSchema.startsWith( SERIALIZATION_PREFIX ) == false ) {
      throw new ResourceKeyCreationException( "Prefix is wrong" );
    }
    final String schema = rawSchema.substring( SERIALIZATION_PREFIX.length() );

    if ( tokenizer.hasMoreElements() == false ) {
      throw new ResourceKeyCreationException( "Identifier is missing" );
    }
    final String id = tokenizer.nextToken();
    final Map parameters;
    if ( tokenizer.hasMoreElements() ) {
      parameters = parseFactoryParametersFromString( tokenizer.nextToken() );
    } else {
      parameters = null;
    }

    // The 1st component is the schema... the 2nd is the identifier... the 3rd is the factory parameters (optional)
    return new ResourceKeyData( schema, id, parameters );
  }

  /**
   * Returns the list of factory parameters for the specified ResourceKey as a String representation in the format:
   * <pre>
   *   key=value:key=value:...:key=value
   * </pre>
   * The colon (:) is the separator between parameters and the equal sign (=) is the separator between the key and the
   * value.
   * <p/>
   * If the factory parameters is empty, <code>null</code> will be returned
   *
   * @param factoryParameters the parameter map.
   * @return a String representation of the factory parameters for the ResourceKey
   */
  public static String convertFactoryParametersToString( final Map factoryParameters ) {
    if ( factoryParameters == null || factoryParameters.size() <= 0 ) {
      return null;
    }

    final CSVQuoter innerQuoter = new CSVQuoter( '=' );
    final CSVQuoter quoter = new CSVQuoter( ':' );
    final StringBuilder sb = new StringBuilder();
    for ( Iterator iterator = factoryParameters.keySet().iterator(); iterator.hasNext(); ) {
      if ( sb.length() > 0 ) {
        sb.append( ':' );
      }

      final StringBuilder entrySb = new StringBuilder();
      final Object key = iterator.next();
      if ( key instanceof FactoryParameterKey ) {
        final FactoryParameterKey fkey = (FactoryParameterKey) key;
        entrySb.append( innerQuoter.doQuoting( "f:" + fkey.getName() ) );
      } else if ( key instanceof LoaderParameterKey ) {
        final LoaderParameterKey fkey = (LoaderParameterKey) key;
        entrySb.append( innerQuoter.doQuoting( "l:" + fkey.getName() ) );
      } else {
        throw new IllegalArgumentException( String.valueOf( key ) );
      }

      final Object value = factoryParameters.get( key );
      entrySb.append( '=' );
      if ( value != null ) {
        // todo: This String.valueOf is probably and very likely wrong
        entrySb.append( innerQuoter.doQuoting( String.valueOf( value ) ) );
      }

      sb.append( quoter.doQuoting( entrySb.toString() ) );
    }
    logger.debug( "Converted ResourceKey's Factory Parameters to String: [" + sb.toString() + "]" );
    return sb.toString();
  }

  /**
   * Returns a Map of parameters based on the input string. The string will be parsed using the same format as defined
   * in the <code>getFactoryParametersAsString()</code> method.
   *
   * @param factoryParameters the String representation of factory parameters
   * @return a Map of factory parameters parsed from the string, or <code>null</code> if the source string was null or
   * contained no data
   */
  public static Map parseFactoryParametersFromString( final String factoryParameters ) {
    if ( factoryParameters == null ) {
      return null;
    }
    final Map<ParameterKey, Object> params = new HashMap<ParameterKey, Object>();
    final CSVTokenizer tokenizer = new CSVTokenizer( factoryParameters, ":", "\"", false );
    while ( tokenizer.hasMoreTokens() ) {
      final String entry = tokenizer.nextToken();
      final CSVTokenizer innerTokenizer = new CSVTokenizer( entry, "=", "\"", false );
      final ParameterKey key;
      if ( innerTokenizer.hasMoreElements() ) {
        final String keyString = innerTokenizer.nextToken();
        if ( keyString.startsWith( "f:" ) ) {
          key = new FactoryParameterKey( keyString.substring( 2 ) );
        } else if ( keyString.startsWith( "l:" ) ) {
          key = new LoaderParameterKey( keyString.substring( 2 ) );
        } else {
          throw new IllegalStateException( "Invalid prefix: Key '" + keyString
            + "' must be either a loader-parameter-key or a factory-parameter-key" );
        }
      } else {
        throw new IllegalStateException();
      }

      if ( innerTokenizer.hasMoreElements() ) {
        final Object value = innerTokenizer.nextToken();
        if ( "".equals( value ) ) {
          params.put( key, null );
        } else {
          params.put( key, value );
        }
      }
    }

    if ( params.isEmpty() ) {
      return null;
    }

    if ( logger.isDebugEnabled() ) {
      logger.debug( "Converted ResourceKey's Factory Parameter String to a Map: [" + factoryParameters
        + "] -> map of size " + params.size() );
    }
    return params;
  }

  /**
   * Returns the schema portion of the serialized ResourceKey string. If the string is invalid, <code>null</code> will
   * be returned.
   *
   * @param data the String serialized version of a <code>ResourceKey</code>
   * @return the schema object.
   */
  public static Object readSchemaFromString( final String data ) {
    if ( data == null ) {
      return null;
    }
    final CSVTokenizer tokenizer = new CSVTokenizer( data, DELIMITER, "\"", false );
    if ( tokenizer.hasMoreElements() == false ) {
      return null;
    }

    final String tempData = tokenizer.nextToken();
    if ( tempData.startsWith( SERIALIZATION_PREFIX ) ) {
      return tempData.substring( SERIALIZATION_PREFIX.length() );
    }
    return null;
  }

  /**
   * Performs a simple attempt at a "smart" conversion to a ResourceKey. <ol> <li>If the <code>value</code> is
   * <code>null</code>, this method will return <code>null</code></li> <li>If the <code>value</code> is a
   * <code>ResourceKey</code>, the <code>value</code> will be returned</li> <li>If the <code>value</code> is a
   * <code>String</code> and is syntactically valid as a <code>URL</code>, it will be converted to a <code>URL</code>
   * and then used to create a <code>ResourceKey</code></li> <li>If the <code>value</code> is a <code>String</code> and
   * is NOT syntactically valid as a <code>URL</code>, it will be converted ot a <code>File</code> and then used to
   * create a <code>ResourceKey</code></li> <li>All other types will be passed along to attempt a key creation as is
   * </ol>
   *
   * @param value           the object to convert to a <code>ResourceKey</code>
   * @param resourceManager the resource manager used in key creation
   * @param parameters      the parameters that should be passed to generate a resource key
   * @return the resource key created
   * @throws ResourceKeyCreationException indicates the value can not be used to create a Resource Key
   */
  public static ResourceKey toResourceKey( final Object value,
                                           final ResourceManager resourceManager,
                                           final ResourceKey contextKey,
                                           final Map parameters )
    throws ResourceKeyCreationException {
    if ( value == null ) {
      return null;
    }

    if ( value instanceof ResourceKey ) {
      return (ResourceKey) value;
    }

    if ( resourceManager == null ) {
      throw new NullPointerException( "ResourceManager is null" );
    }

    // If the value is a String, try a URL or a File
    Object tempObject = value;
    if ( tempObject instanceof String ) {
      final String spec = (String) value;
      if ( contextKey != null ) {
        try {
          return resourceManager.deriveKey( contextKey, spec, parameters );
        } catch ( ResourceKeyCreationException e ) {
          // ignored ..
        }
      }
      try {
        tempObject = new URL( spec );
      } catch ( MalformedURLException e ) {
        tempObject = new File( spec );
      }
    }

    return resourceManager.createKey( tempObject, parameters );
  }

  /**
   * Returns a new ResourceKey with the specified source resource embedded inside as a byte []
   *
   * @param source            the ResourceKey to the source which will be embedded - NOTE: the pattern can specify an
   *                          exact name or a pattern for creating a temporary name. If the name exists, it will be
   *                          replaced.
   * @param factoryParameters any factory parameters which should be added to the ResourceKey being created
   * @return the ResourceKey for the newly created embedded entry
   */
  public static ResourceKey embedResourceInKey( final ResourceManager manager,
                                                final ResourceKey source,
                                                final Map factoryParameters )
    throws IOException, ResourceKeyCreationException, ResourceLoadingException {
    if ( manager == null ) {
      throw new IllegalArgumentException();
    }
    if ( source == null ) {
      throw new IllegalArgumentException();
    }
    final ResourceData resourceData = manager.load( source );

    // Load the resource into a byte array
    final InputStream in = resourceData.getResourceAsStream( manager );
    final ByteArrayOutputStream out = new ByteArrayOutputStream();
    try {
      IOUtils.getInstance().copyStreams( in, out );
      // Create a resource key with the byte array
      return manager.createKey( out.toByteArray(), factoryParameters );
    } finally {
      try {
        in.close();
      } catch ( IOException e ) {
        logger.error( "Error closing input stream", e );
      }
    }
  }
}
