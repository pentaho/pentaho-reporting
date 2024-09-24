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
* Copyright (c) 2006 - 2019 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.resourceloader;

import org.apache.commons.vfs2.FileObject;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * The key is an unique identifier for the resource. Most of the time, this may be an URL, but other (especially
 * database based) schemas are possible.
 * <p/>
 * A resource key must provide an 'equals' implementation. ResourceKeys should be implemented as immutable classes, so
 * that they can be safely stored in collections or on external storages (like caches).
 *
 * @author Thomas Morgner
 */
public final class ResourceKey implements Serializable {
  /**
   * @noinspection StaticCollection
   */
  private static final Map<ParameterKey, Object> EMPTY_MAP = Collections.emptyMap();
  private static final long serialVersionUID = -7764107570068726772L;
  private Map<? extends ParameterKey, Object> factoryParameters;
  private Integer hashCode;
  private Object schema;
  private Object identifier;
  private ResourceKey parent;

  public ResourceKey( final Object schema,
                      final Object identifier,
                      final Map<? extends ParameterKey, Object> factoryParameters ) {
    if ( schema == null ) {
      throw new NullPointerException();
    }
    if ( identifier == null ) {
      throw new NullPointerException();
    }

    this.schema = schema;
    this.identifier = identifier;
    if ( factoryParameters != null ) {
      this.factoryParameters =
        Collections.unmodifiableMap( new LinkedHashMap<ParameterKey, Object>( factoryParameters ) );
    } else {
      this.factoryParameters = EMPTY_MAP;
    }
  }

  public ResourceKey( final ResourceKey parent,
                      final Object schema,
                      final Object identifier,
                      final Map<? extends ParameterKey, Object> factoryParameters ) {
    this( schema, identifier, factoryParameters );
    this.parent = parent;
  }

  public static ResourceKey createAsDerived( final ResourceKey parent,
                                             final ResourceKey child ) {
    if ( child.parent != null ) {
      throw new IllegalArgumentException();
    }
    return new ResourceKey( parent, child.schema, child.identifier, child.factoryParameters );
  }

  public ResourceKey getParent() {
    return parent;
  }

  public Map<ParameterKey, Object> getFactoryParameters() {
    return Collections.unmodifiableMap( factoryParameters );
  }

  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( o == null || getClass() != o.getClass() ) {
      return false;
    }

    final ResourceKey that = (ResourceKey) o;
    if ( that.hashCode != null && this.hashCode != null ) {
      // shortcut: If the hashcode is not equal, the whole object cannot be equal
      // see contract of equals/hashcode for details.
      if ( that.hashCode.equals( this.hashCode ) == false ) {
        return false;
      }
    }
    if ( parent != that.parent && ObjectUtilities.equal( parent, that.parent ) == false ) {
      return false;
    }
    if ( !schema.equals( that.schema ) ) {
      return false;
    }
    if ( !factoryParameters.equals( that.factoryParameters ) ) {
      return false;
    }
    if ( identifier instanceof URL ) {
      if ( String.valueOf( identifier ).equals( String.valueOf( that.identifier ) ) == false ) {
        return false;
      }
    } else if ( identifier instanceof File && that.identifier instanceof File ) {
      // File.equals() does not check to see if two File objects refer to the same file ...
      // it checks to make sure they refer to the same file IN THE SAME way (which is deeper than we need)
      if ( ObjectUtilities.equals( (File) identifier, (File) that.identifier ) == false ) {
        return false;
      }
    } else if ( identifier instanceof FileObject && that.identifier instanceof FileObject ) {
      if ( ( (FileObject) identifier ).getName().getURI().equals( ( (FileObject) that.identifier ).getName().getURI() ) == false ) {
        return false;
      }
    } else if ( !identifier.equals( that.identifier ) ) {
      if ( identifier instanceof byte[] && that.identifier instanceof byte[] ) {
        final byte[] me = (byte[]) identifier;
        final byte[] he = (byte[]) that.identifier;
        if ( Arrays.equals( me, he ) == false ) {
          return false;
        }
      } else {
        return false;
      }
    }

    return true;
  }

  public int hashCode() {
    if ( hashCode == null ) {
      int result = factoryParameters.hashCode();
      result = 29 * result + schema.hashCode();
      if ( identifier instanceof URL ) {
        final URL url = (URL) identifier;
        result = 29 * result + url.toString().hashCode();
      } else if ( identifier instanceof byte[] ) {
        result = 29 * result + Arrays.hashCode( (byte[]) identifier );
      } else if ( identifier instanceof FileObject ) {
        result = 29 * result + ( (FileObject) identifier ).getName().getURI().hashCode();
      } else {
        result = 29 * result + identifier.hashCode();
      }
      if ( parent != null ) {
        result = 29 * result + parent.hashCode();
      }
      hashCode = ( result );
    }
    return hashCode.intValue();
  }

  public Object getIdentifier() {
    return identifier;
  }

  /**
   * Returns a String version of the identifier.
   *
   * @return the identifier as string or null, if the identifier could not be converted easily.
   */
  public String getIdentifierAsString() {
    if ( identifier instanceof File ) {
      final File file = (File) identifier;
      return file.getPath();
    }
    if ( identifier instanceof URL ) {
      final URL url = (URL) identifier;
      return url.toExternalForm();
    }
    if ( identifier instanceof String ) {
      return identifier.toString();
    }
    if ( identifier instanceof FileObject ) {
      return ( (FileObject) identifier ).getName().getURI();
    }

    return null;
  }

  /**
   * Returns the schema of this resource key. The schema is an internal identifier to locate the resource-loader
   * implementation that was responsible for creating the key in the first place.
   * <p/>
   * The schema has no meaning outside the resource loading framework.
   *
   * @return
   */
  public Object getSchema() {
    return schema;
  }

  public String toString() {

    return String.format( "ResourceKey{schema=%s, identifier=%s, factoryParameters=%s, parent=%s}",
            schema,
            ( identifier instanceof FileObject ) ? ( (FileObject) identifier ).getName().getURI() : identifier,
            factoryParameters,
            parent );
  }
}

