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

package org.pentaho.reporting.libraries.docbundle.metadata.writer;

import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.HashMap;
import java.util.Iterator;

/**
 * The AbstractReadHandlerFactory provides a base implementation for all read-handler factories. A read-handler factory
 * decouples the tag-handlers of a SAX parser and allows to configure alternate parser configuations at runtime,
 * resulting in a more flexible parsing process.
 *
 * @author Thomas Morgner
 */
public class BundleMetaDataWriteHandlerFactory {
  /**
   * The TagDefinitionKey is a compund key to lookup handler implementations using a namespace and tagname.
   */
  private static class TagDefinitionKey {
    private String namespace;
    private String tagName;

    /**
     * Creates a new key.
     *
     * @param namespace the namespace (can be null for undefined).
     * @param tagName   the tagname (can be null for undefined).
     */
    private TagDefinitionKey( final String namespace, final String tagName ) {
      this.namespace = namespace;
      this.tagName = tagName;
    }

    /**
     * Compares this key for equality with an other object.
     *
     * @param o the other object.
     * @return true, if this key is the same as the given object, false otherwise.
     */
    public boolean equals( final Object o ) {
      if ( this == o ) {
        return true;
      }
      if ( o == null || getClass() != o.getClass() ) {
        return false;
      }

      final TagDefinitionKey that = (TagDefinitionKey) o;

      if ( namespace != null ? !namespace.equals( that.namespace ) : that.namespace != null ) {
        return false;
      }
      if ( tagName != null ? !tagName.equals( that.tagName ) : that.tagName != null ) {
        return false;
      }

      return true;
    }

    /**
     * Computes the hashcode for this key.
     *
     * @return the hashcode.
     */
    public int hashCode() {
      int result = ( namespace != null ? namespace.hashCode() : 0 );
      result = 29 * result + ( tagName != null ? tagName.hashCode() : 0 );
      return result;
    }
  }

  private HashMap<String, String> defaultDefinitions;
  private HashMap<TagDefinitionKey, String> tagData;
  private String defaultNamespace;

  /**
   * A default-constructor.
   */
  protected BundleMetaDataWriteHandlerFactory() {
    defaultDefinitions = new HashMap<String, String>();
    tagData = new HashMap<TagDefinitionKey, String>();
  }

  /**
   * Configures this factory from the given configuration using the speoified prefix as filter.
   *
   * @param conf   the configuration.
   * @param prefix the key-prefix.
   */
  public void configure( final Configuration conf, final String prefix ) {
    if ( conf == null ) {
      throw new NullPointerException();
    }
    if ( prefix == null ) {
      throw new NullPointerException();
    }

    final HashMap<String, String> knownNamespaces = new HashMap<String, String>();

    final String nsConfPrefix = prefix + "namespace.";
    final Iterator<String> namespaces = conf.findPropertyKeys( nsConfPrefix );
    while ( namespaces.hasNext() ) {
      final String key = namespaces.next();
      final String nsPrefix = key.substring( nsConfPrefix.length() );
      final String nsUri = conf.getConfigProperty( key );
      knownNamespaces.put( nsPrefix, nsUri );
    }

    defaultNamespace = knownNamespaces.get
      ( conf.getConfigProperty( prefix + "namespace" ) );

    final String globalDefaultKey = prefix + "default";
    final String globalValue = conf.getConfigProperty( globalDefaultKey );
    if ( isValidHandler( globalValue ) ) {
      defaultDefinitions.put( null, globalValue );
    } else {
      // let the loading fail ..
      defaultDefinitions.put( null, "" );
    }

    final String nsDefaultPrefix = prefix + "default.";
    final Iterator<String> defaults = conf.findPropertyKeys( nsDefaultPrefix );
    while ( defaults.hasNext() ) {
      final String key = defaults.next();
      final String nsPrefix = key.substring( nsDefaultPrefix.length() );
      final String nsUri = knownNamespaces.get( nsPrefix );
      if ( nsUri == null ) {
        continue;
      }

      final String tagData = conf.getConfigProperty( key );
      if ( tagData == null ) {
        continue;
      }
      if ( isValidHandler( tagData ) ) {
        defaultDefinitions.put( nsUri, tagData );
      } else {
        // let the loading fail .. to indicate we want no parsing ..
        defaultDefinitions.put( nsUri, "" );
      }
    }

    final String nsTagsPrefix = prefix + "tag.";
    final Iterator<String> tags = conf.findPropertyKeys( nsTagsPrefix );
    while ( tags.hasNext() ) {
      final String key = tags.next();
      final String tagDef = key.substring( nsTagsPrefix.length() );
      final String tagData = conf.getConfigProperty( key );
      if ( tagData == null ) {
        continue;
      }
      if ( isValidHandler( tagData ) == false ) {
        continue;
      }

      final int delim = tagDef.indexOf( '.' );
      if ( delim == -1 ) {
        this.tagData.put( new TagDefinitionKey( null, tagDef ), tagData );
      } else {
        final String nsPrefix = tagDef.substring( 0, delim );
        final String nsUri = knownNamespaces.get( nsPrefix );
        if ( nsUri == null ) {
          continue;
        }

        final String tagName = tagDef.substring( delim + 1 );
        this.tagData.put( new TagDefinitionKey( nsUri, tagName ), tagData );
      }
    }
  }

  /**
   * Checks, whether the given handler classname can be instantiated and is in fact an object of the required
   * target-type.
   *
   * @param className the classname that should be checked.
   * @return true, if the handler is valid, false otherwise.
   */
  private boolean isValidHandler( final String className ) {
    if ( className == null ) {
      return false;
    }
    final BundleMetaDataEntryWriteHandler o =
      ObjectUtilities.loadAndInstantiate( className, getClass(), BundleMetaDataEntryWriteHandler.class );
    return o != null;
  }


  /**
   * The returned handler can be null, in case no handler is registered.
   *
   * @param namespace the namespace of the xml-tag for which a handler should be returned.
   * @param tagname   the tagname of the xml-tag.
   * @return the instantiated read handler, never null.
   */
  public BundleMetaDataEntryWriteHandler getHandler( String namespace, final String tagname ) {
    if ( namespace == null ) {
      namespace = defaultNamespace;
    }

    final TagDefinitionKey key = new TagDefinitionKey( namespace, tagname );
    final String tagVal = tagData.get( key );
    if ( tagVal != null ) {
      return ObjectUtilities.loadAndInstantiate( tagVal, getClass(), BundleMetaDataEntryWriteHandler.class );
    }

    final String className = defaultDefinitions.get( namespace );
    if ( className != null ) {
      return ObjectUtilities.loadAndInstantiate( className, getClass(), BundleMetaDataEntryWriteHandler.class );
    }

    final String fallbackName = defaultDefinitions.get( null );
    final BundleMetaDataEntryWriteHandler fallbackValue =
      ObjectUtilities.loadAndInstantiate( fallbackName, getClass(), BundleMetaDataEntryWriteHandler.class );
    if ( fallbackValue != null ) {
      return fallbackValue;
    }

    return null;
  }


}
