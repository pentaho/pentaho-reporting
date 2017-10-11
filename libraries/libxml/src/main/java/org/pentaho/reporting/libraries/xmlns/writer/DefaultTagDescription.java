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

package org.pentaho.reporting.libraries.xmlns.writer;

import org.pentaho.reporting.libraries.base.config.Configuration;

import java.util.HashMap;
import java.util.Iterator;

/**
 * A tag-description provides information about xml tags. At the moment, we simply care whether an element can contain
 * CDATA. In such cases, we do not indent the inner elements.
 *
 * @author Thomas Morgner
 */
public class DefaultTagDescription implements TagDescription, Cloneable {
  /**
   * The TagDefinitionKey is a compund key to lookup tag-specifications using a namespace and tagname.
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
     * Updates the internal state for the tag-definition lookup-key. Calling this on a non-lookup key will give funny
     * and unpredictable results.
     *
     * @param namespace the namespace of the key.
     * @param tagName   the tagname.
     */
    public void update( final String namespace, final String tagName ) {
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

    public int hashCode() {
      int result = ( namespace != null ? namespace.hashCode() : 0 );
      result = 29 * result + ( tagName != null ? tagName.hashCode() : 0 );
      return result;
    }

    /**
     * Computes the hashcode for this key.
     *
     * @return the hashcode.
     */
    public String toString() {
      return "TagDefinitionKey{" +
        "namespace='" + namespace + '\'' +
        ", tagName='" + tagName + '\'' +
        '}';
    }
  }

  private HashMap<String, Boolean> defaultDefinitions;
  private HashMap<TagDefinitionKey, Boolean> tagData;
  private String defaultNamespace;
  private TagDefinitionKey lookupKey;

  /**
   * A default-constructor.
   */
  public DefaultTagDescription() {
    defaultDefinitions = new HashMap<String, Boolean>();
    tagData = new HashMap<TagDefinitionKey, Boolean>();
    lookupKey = new TagDefinitionKey( null, null );
  }

  /**
   * Creates and configures a new TagDescription collection.
   *
   * @param conf   the configuration.
   * @param prefix the key-prefix.
   * @see #configure(Configuration, String)
   */
  public DefaultTagDescription( final Configuration conf, final String prefix ) {
    this();
    configure( conf, prefix );
  }

  /**
   * Configures this factory from the given configuration using the speoified prefix as filter.
   *
   * @param conf   the configuration.
   * @param prefix the key-prefix.
   * @noinspection ObjectAllocationInLoop as this is a factory configuration method.
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

    setDefaultNamespace( knownNamespaces.get( conf.getConfigProperty( prefix + "namespace" ) ) );

    final String globalDefaultKey = prefix + "default";
    final boolean globalValue = "allow".equals( conf.getConfigProperty( globalDefaultKey ) ) == false;
    setNamespaceHasCData( null, globalValue );

    final String nsDefaultPrefix = prefix + "default.";
    final Iterator<String> defaults = conf.findPropertyKeys( nsDefaultPrefix );
    while ( defaults.hasNext() ) {
      final String key = defaults.next();
      final String nsPrefix = key.substring( nsDefaultPrefix.length() );
      final String nsUri = knownNamespaces.get( nsPrefix );
      if ( nsUri == null ) {
        continue;
      }

      final boolean value = "allow".equals( conf.getConfigProperty( key ) ) == false;
      setNamespaceHasCData( nsUri, value );
    }

    final String nsTagsPrefix = prefix + "tag.";
    final Iterator<String> tags = conf.findPropertyKeys( nsTagsPrefix );
    while ( tags.hasNext() ) {
      final String key = tags.next();
      final String tagDef = key.substring( nsTagsPrefix.length() );
      final boolean value = "allow".equals( conf.getConfigProperty( key ) ) == false;

      final int delim = tagDef.indexOf( '.' );
      if ( delim == -1 ) {
        setElementHasCData( tagDef, value );
      } else {
        final String nsPrefix = tagDef.substring( 0, delim );
        final String nsUri = knownNamespaces.get( nsPrefix );
        if ( nsUri == null ) {
          continue;
        }

        final String tagName = tagDef.substring( delim + 1 );
        setElementHasCData( nsUri, tagName, value );
      }
    }
  }

  /**
   * Adds a configuration default for the given namespace to the tag-descriptions. If the namespace URI given here is
   * null, this defines the global default for all namespaces.
   *
   * @param namespaceUri the namespace URI for which a default should be configured.
   * @param hasCData     the default value.
   */
  public void setNamespaceHasCData( final String namespaceUri, final boolean hasCData ) {
    defaultDefinitions.put( namespaceUri, hasCData ? Boolean.TRUE : Boolean.FALSE );
  }

  /**
   * Adds a configuration entry for the given namespace and tag-name to the tag-descriptions.
   *
   * @param namespaceUri the namespace URI for which a default should be configured.
   * @param tagName      the tagname for which the entry should be added.
   * @param hasCData     the default value.
   */
  public void setElementHasCData( final String namespaceUri, final String tagName, final boolean hasCData ) {
    if ( namespaceUri == null ) {
      throw new NullPointerException();
    }
    if ( tagName == null ) {
      throw new NullPointerException();
    }
    tagData.put( new TagDefinitionKey( namespaceUri, tagName ), hasCData ? Boolean.TRUE : Boolean.FALSE );
  }

  /**
   * Adds a configuration entry for the given namespace and tag-name to the tag-descriptions.
   *
   * @param tagName  the tagname for which the entry should be added.
   * @param hasCData the default value.
   */
  public void setElementHasCData( final String tagName, final boolean hasCData ) {
    if ( tagName == null ) {
      throw new NullPointerException();
    }
    tagData.put( new TagDefinitionKey( defaultNamespace, tagName ), hasCData ? Boolean.TRUE : Boolean.FALSE );
  }

  public void setDefaultNamespace( final String defaultNamespace ) {
    this.defaultNamespace = defaultNamespace;
  }

  public String getDefaultNamespace() {
    return defaultNamespace;
  }

  /**
   * Queries the defined tag-descriptions whether the given tag and namespace is defined to allow character-data.
   *
   * @param namespace the namespace.
   * @param tagname   the xml-tagname.
   * @return true, if the element may contain character data, false otherwise.
   */
  public boolean hasCData( String namespace, final String tagname ) {
    if ( tagname == null ) {
      throw new NullPointerException();
    }

    if ( namespace == null ) {
      namespace = defaultNamespace;
    }

    if ( tagData.isEmpty() == false ) {
      lookupKey.update( namespace, tagname );
      final Object tagVal = tagData.get( lookupKey );
      if ( tagVal != null ) {
        return Boolean.FALSE.equals( tagVal ) == false;
      }
    }

    if ( defaultDefinitions.isEmpty() ) {
      return true;
    }

    final Object obj = defaultDefinitions.get( namespace );
    if ( obj != null ) {
      return Boolean.FALSE.equals( obj ) == false;
    }

    final Object defaultValue = defaultDefinitions.get( null );
    return Boolean.FALSE.equals( defaultValue ) == false;
  }

  public DefaultTagDescription clone() {
    try {
      final DefaultTagDescription clone = (DefaultTagDescription) super.clone();
      clone.tagData = (HashMap<TagDefinitionKey, Boolean>) tagData.clone();
      clone.defaultDefinitions = (HashMap<String, Boolean>) defaultDefinitions.clone();
      clone.lookupKey = new TagDefinitionKey( null, null );
      return clone;
    } catch ( CloneNotSupportedException e ) {
      throw new IllegalStateException();
    }
  }
}
