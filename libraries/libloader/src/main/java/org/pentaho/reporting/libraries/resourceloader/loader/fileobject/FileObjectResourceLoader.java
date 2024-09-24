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
 * Copyright (c) 2006 - 2020 Hitachi Vantara and Contributors.  All rights reserved.
 */

package org.pentaho.reporting.libraries.resourceloader.loader.fileobject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.vfs2.VFS;
import org.apache.commons.vfs2.FileObject;
import org.apache.commons.vfs2.FileSystemException;
import org.apache.commons.vfs2.provider.UriParser;
import org.pentaho.reporting.libraries.resourceloader.ResourceData;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyData;
import org.pentaho.reporting.libraries.resourceloader.ResourceKeyUtils;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoader;
import org.pentaho.reporting.libraries.resourceloader.ResourceLoadingException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.io.File;

public class FileObjectResourceLoader implements ResourceLoader {
  public static final String SCHEMA_NAME = FileObjectResourceLoader.class.getName();
  private static final Log logger = LogFactory.getLog( FileObjectResourceLoader.class );

  public FileObjectResourceLoader() {
  }

 /**
  * Checks, whether this resource loader implementation was responsible for creating this key.
  *
  * @param key
  * @return
  */
  public boolean isSupportedKey( final ResourceKey key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( SCHEMA_NAME.equals( key.getSchema() ) ) {
      return true;
    }
    return false;
  }

 /**
  * Creates a new resource key from the given object and the factory keys.
  *
  * @param value
  * @param factoryKeys
  * @return the created key.
  * @throws org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException if creating the key failed.
  */
  public ResourceKey createKey( final Object value, final Map factoryKeys ) throws ResourceKeyCreationException {
    try {
      if ( value instanceof FileObject ) {
        final FileObject f = (FileObject) value;
        if ( f.exists() && f.isFile() ) {
          return new ResourceKey( SCHEMA_NAME, f, factoryKeys );
        }
      } else if ( value instanceof String ) {
        //verify string value conforms to valid VFS URI
        UriParser.checkUriEncoding( String.valueOf( value ) );
        File file = new File( String.valueOf( value ) );

        final FileObject f = VFS.getManager().resolveFile( file.toURI().toString() );
        if ( f.exists() && f.isFile() ) {
          return new ResourceKey( SCHEMA_NAME, f, factoryKeys );
        }
      }
    } catch ( FileSystemException fse ) {
      return null;
    }
    return null;
  }

 /**
  * Derives a new resource key from the given key. If neither a path nor new factory-keys are given, the parent key is
  * returned.
  *
  * @param parent      the parent
  * @param path        the derived path (can be null).
  * @param factoryKeys the optional factory keys (can be null).
  * @return the derived key.
  * @throws org.pentaho.reporting.libraries.resourceloader.ResourceKeyCreationException if the key cannot be derived
  *                                                                                     for any reason.
  */
  public ResourceKey deriveKey( final ResourceKey parent, final String path, final Map factoryKeys )
        throws ResourceKeyCreationException {
    if ( isSupportedKey( parent ) == false ) {
      throw new ResourceKeyCreationException( "Assertation: Unsupported parent key type" );
    }
    try {
      final File target;
      if ( path != null ) {
        final File parentResource = new File( parent.getIdentifierAsString() );
        final File parentFile = parentResource.getParentFile();
        if ( parentFile == null ) {
          throw new FileNotFoundException( "Parent file does not exist" );
        }
        target = new File( parentFile, path );
      } else {
        return parent;
      }
      final Map map;
      if ( factoryKeys != null ) {
        map = new HashMap();
        map.putAll( parent.getFactoryParameters() );
        map.putAll( factoryKeys );
      } else {
        map = parent.getFactoryParameters();
      }
      String targetIdentifier = ( (FileObject) parent.getIdentifier() ).getFileSystem() != null ? target.toString() : "solution:" + target.toString();
      return new ResourceKey( parent.getSchema(), VFS.getManager().resolveFile( targetIdentifier ), map );
    } catch ( IOException ioe ) {
      throw new ResourceKeyCreationException( "Failed to create key", ioe );
    }
  }

  public URL toURL( final ResourceKey key ) {
    if ( key == null ) {
      throw new NullPointerException();
    }
    if ( isSupportedKey( key ) == false ) {
      throw new IllegalArgumentException( "Key format is not recognized." );
    }

    try {
      final FileObject fileObject = (FileObject) key.getIdentifier();
      return fileObject.getURL();
    } catch ( FileSystemException e ) {
      return null;
    }
  }

  public ResourceData load( final ResourceKey key ) throws ResourceLoadingException {
    if ( isSupportedKey( key ) == false ) {
      throw new ResourceLoadingException( "Key format is not recognized." );
    }
    return new FileObjectResourceData( key );
  }

  public String serialize( final ResourceKey bundleKey, final ResourceKey key ) throws ResourceException {
    // Validate the parameter
    if ( key == null ) {
      throw new NullPointerException( "The ResourceKey can not be null" );
    }
    if ( isSupportedKey( key ) == false ) {
      throw new IllegalArgumentException( "Key format is not recognized." );
    }
    if ( !( key.getIdentifier() instanceof FileObject ) ) {
      throw new IllegalArgumentException( "ResourceKey is invalid - identifier is not a File object" );
    }

    // Log information
    logger.debug( "Serializing a FileObject Resource Key..." );
    if ( key.getParent() != null ) {
      throw new ResourceException( "Unable to serialize a FileObject-ResourceKey with a parent. "
              + "This type is not expected to have a parent." );
    }

    // Create a string version of the identifier
    try {
      final FileObject fileObject = (FileObject) key.getIdentifier();
      final String strIdentifier = fileObject.getName().getURI();
      final String result = ResourceKeyUtils.createStringResourceKey(
              key.getSchema().toString(), strIdentifier, key.getFactoryParameters() );
      logger.debug( "Serialized FileObject Resource Key: [" + result + "]" );
      return result;
    } catch ( Exception ioe ) {
      throw new IllegalArgumentException( "Could not determine fileObject URL specified in ResourceKey: "
              + ioe.getMessage() );
    }
  }

  public ResourceKey deserialize( final ResourceKey bundleKey, final String stringKey )
        throws ResourceKeyCreationException {
    // Parse the data
    final ResourceKeyData keyData = ResourceKeyUtils.parse( stringKey );

    // Validate the data
    if ( SCHEMA_NAME.equals( keyData.getSchema() ) == false ) {
      throw new ResourceKeyCreationException( "Serialized version of fileObject key does not contain correct schema" );
    }

    // Create a new fileObject based on the path provided
    try {
      final FileObject fileObject = VFS.getManager().resolveFile( keyData.getIdentifier() );
      return new ResourceKey( SCHEMA_NAME, fileObject, keyData.getFactoryParameters() );
    } catch ( FileSystemException fse ) {
      throw new ResourceKeyCreationException( "Serialized version of fileObject key does not result into a valid FileObject" );
    }
  }

  public boolean isSupportedDeserializer( final String data ) {
    return SCHEMA_NAME.equals( ResourceKeyUtils.readSchemaFromString( data ) );
  }
}
