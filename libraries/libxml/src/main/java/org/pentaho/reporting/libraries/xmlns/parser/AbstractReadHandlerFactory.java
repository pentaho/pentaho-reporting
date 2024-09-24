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

package org.pentaho.reporting.libraries.xmlns.parser;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
public abstract class AbstractReadHandlerFactory<T extends XmlReadHandler> {
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

    public String toString() {
      final StringBuilder sb = new StringBuilder();
      sb.append( "[" );
      if ( namespace == null ) {
        sb.append( "<null>" );
      } else {
        sb.append( namespace );
      }
      sb.append( '\'' );
      sb.append( "|" );
      if ( tagName == null ) {
        sb.append( "<null>" );
      } else {
        sb.append( tagName );
      }
      sb.append( '\'' );
      sb.append( ']' );
      return sb.toString();
    }
  }

  private static class TagDefinitionValue {
    private String className;
    private boolean legacyOverride;

    private TagDefinitionValue( final String className, final boolean legacyOverride ) {
      this.className = className;
      this.legacyOverride = legacyOverride;
    }

    public String getClassName() {
      return className;
    }

    public boolean isLegacyOverride() {
      return legacyOverride;
    }
  }

  private HashMap<TagDefinitionKey, TagDefinitionValue> tagData;
  private String defaultNamespace;

  /**
   * A default-constructor.
   */
  protected AbstractReadHandlerFactory() {
    tagData = new HashMap<TagDefinitionKey, TagDefinitionValue>();
  }

  public void configureGlobal( final Configuration config, final String prefix ) {
    final Iterator propertyKeys = config.findPropertyKeys( prefix );
    while ( propertyKeys.hasNext() ) {
      final String key = (String) propertyKeys.next();
      final String value = config.getConfigProperty( key );
      if ( value != null ) {
        configure( config, value );
      }
    }
  }

  /**
   * Configures this factory from the given configuration using the specified prefix as filter.
   *
   * @param conf   the configuration.
   * @param prefix the key-prefix.
   * @noinspection ObjectAllocationInLoop as this method configures the factory.
   */
  public void configure( final Configuration conf, final String prefix ) {
    final HashMap<String, String> knownNamespaces = new HashMap<String, String>();

    final String nsConfPrefix = prefix + "namespace.";
    final Iterator namespaces = conf.findPropertyKeys( nsConfPrefix );
    while ( namespaces.hasNext() ) {
      final String key = (String) namespaces.next();
      final String nsPrefix = key.substring( nsConfPrefix.length() );
      final String nsUri = conf.getConfigProperty( key );
      knownNamespaces.put( nsPrefix, nsUri );
    }

    final Log legacyWarningLog = LogFactory.getLog( getClass() );
    boolean warnedLegacyConfig = false;
    final String legacyDefaultNamespace = knownNamespaces.get( conf.getConfigProperty( prefix + "namespace" ) );
    if ( legacyDefaultNamespace != null ) {
      if ( warnedLegacyConfig == false ) {
        legacyWarningLog.warn( "Configured configuration-properties based override for global read-hander. " +
          "Change your code to use proper module-initializers instead. " +
          "This method of configuring the parser will go away in the next major version." );
        warnedLegacyConfig = true;
      }
      setDefaultNamespace( legacyDefaultNamespace );
    }

    final String globalDefaultKey = prefix + "default";
    final String globalValue = conf.getConfigProperty( globalDefaultKey );
    if ( isValidHandler( globalValue ) ) {
      this.tagData.put( new TagDefinitionKey( null, null ), new TagDefinitionValue( globalValue, true ) );
      if ( warnedLegacyConfig == false ) {
        legacyWarningLog.warn( "Configured configuration-properties based override for global read-hander. " +
          "Change your code to use proper module-initializers instead. " +
          "This method of configuring the parser will go away in the next major version." );
        warnedLegacyConfig = true;
      }
    }

    final String nsDefaultPrefix = prefix + "default.";
    final Iterator defaults = conf.findPropertyKeys( nsDefaultPrefix );
    while ( defaults.hasNext() ) {
      final String key = (String) defaults.next();
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
        if ( warnedLegacyConfig == false ) {
          legacyWarningLog.warn( "Configured configuration-properties based override for global read-hander. " +
            "Change your code to use proper module-initializers instead. " +
            "This method of configuring the parser will go away in the next major version." );
          warnedLegacyConfig = true;
        }
        this.tagData.put( new TagDefinitionKey( nsUri, null ), new TagDefinitionValue( tagData, true ) );
      }
    }

    final String nsTagsPrefix = prefix + "tag.";
    final Iterator tags = conf.findPropertyKeys( nsTagsPrefix );
    while ( tags.hasNext() ) {
      final String key = (String) tags.next();
      final String tagDef = key.substring( nsTagsPrefix.length() );
      final String tagData = conf.getConfigProperty( key );
      if ( tagData == null ) {
        continue;
      }
      if ( isValidHandler( tagData ) == false ) {
        continue;
      }

      if ( warnedLegacyConfig == false ) {
        legacyWarningLog.warn( "Configured configuration-properties based override for global read-hander. " +
          "Change your code to use proper module-initializers instead. " +
          "This method of configuring the parser will go away in the next major version." );
        warnedLegacyConfig = true;
      }

      final int delim = tagDef.indexOf( '.' );
      if ( delim == -1 ) {
        this.tagData.put( new TagDefinitionKey( null, tagDef ), new TagDefinitionValue( tagData, true ) );
      } else {
        final String nsPrefix = tagDef.substring( 0, delim );
        final String nsUri = knownNamespaces.get( nsPrefix );
        if ( nsUri == null ) {
          continue;
        }

        final String tagName = tagDef.substring( delim + 1 );
        this.tagData.put( new TagDefinitionKey( nsUri, tagName ), new TagDefinitionValue( tagData, true ) );
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
  public void setNamespaceHandler( final String namespaceUri, final Class<? extends T> hasCData ) {
    final TagDefinitionKey key = new TagDefinitionKey( namespaceUri, null );
    setValueIfNotDefinedAsLegacy( key, hasCData );
  }

  /**
   * Adds a configuration entry for the given namespace and tag-name to the tag-descriptions.
   *
   * @param namespaceUri the namespace URI for which a default should be configured.
   * @param tagName      the tagname for which the entry should be added.
   * @param hasCData     the default value.
   */
  public void setElementHandler( final String namespaceUri, final String tagName, final Class<? extends T> hasCData ) {
    if ( namespaceUri == null ) {
      throw new NullPointerException();
    }
    if ( tagName == null ) {
      throw new NullPointerException();
    }
    final TagDefinitionKey key = new TagDefinitionKey( namespaceUri, tagName );
    setValueIfNotDefinedAsLegacy( key, hasCData );
  }

  /**
   * Adds a configuration entry for the given namespace and tag-name to the tag-descriptions.
   *
   * @param tagName  the tagname for which the entry should be added.
   * @param hasCData the default value.
   */
  public void setElementHandler( final String tagName, final Class<? extends T> hasCData ) {
    if ( tagName == null ) {
      throw new NullPointerException();
    }
    final TagDefinitionKey key = new TagDefinitionKey( defaultNamespace, tagName );
    setValueIfNotDefinedAsLegacy( key, hasCData );
  }

  private void setValueIfNotDefinedAsLegacy( final TagDefinitionKey key, final Class<? extends T> hasCData ) {
    final TagDefinitionValue existingValue = tagData.get( key );
    if ( existingValue != null && existingValue.isLegacyOverride() ) {
      final Log legacyWarningLog = LogFactory.getLog( getClass() );
      legacyWarningLog.debug
        ( "Module-Configuration ignored as a legacy properties-based configuration exists for " + key );
      return;
    }
    tagData.put( key, new TagDefinitionValue( hasCData.getName(), false ) );
  }

  public void setDefaultNamespace( final String defaultNamespace ) {
    this.defaultNamespace = defaultNamespace;
  }

  public String getDefaultNamespace() {
    return defaultNamespace;
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
    final XmlReadHandler o = ObjectUtilities.loadAndInstantiate( className, getClass(), getTargetClass() );
    return o != null;
  }

  /**
   * Returns the implementation class for this read-handler factory.
   *
   * @return the implementation class.
   */
  protected abstract Class<T> getTargetClass();

  /**
   * The returned handler can be null, in case no handler is registered.
   *
   * @param namespace the namespace of the xml-tag for which a handler should be returned.
   * @param tagname   the tagname of the xml-tag.
   * @return the instantiated read handler, or null if there is no handler registered.
   */
  public T getHandler( String namespace, final String tagname ) {
    if ( namespace == null ) {
      namespace = defaultNamespace;
    }

    final TagDefinitionKey key = new TagDefinitionKey( namespace, tagname );
    final TagDefinitionValue tagVal = tagData.get( key );
    if ( tagVal != null ) {
      return ObjectUtilities.loadAndInstantiate( tagVal.getClassName(), getClass(), getTargetClass() );
    }

    final TagDefinitionKey defaultKey = new TagDefinitionKey( namespace, null );
    final TagDefinitionValue className = tagData.get( defaultKey );
    if ( className != null ) {
      return ObjectUtilities.loadAndInstantiate( className.getClassName(), getClass(), getTargetClass() );
    }

    final TagDefinitionKey fallbackKey = new TagDefinitionKey( null, null );
    final TagDefinitionValue fallbackName = tagData.get( fallbackKey );
    if ( fallbackName == null ) {
      return null;
    }
    final T fallbackValue =
      ObjectUtilities.loadAndInstantiate( fallbackName.getClassName(), getClass(), getTargetClass() );
    if ( fallbackValue != null ) {
      return fallbackValue;
    }
    return null;
  }


}
