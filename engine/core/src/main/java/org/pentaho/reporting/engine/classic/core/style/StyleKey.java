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

package org.pentaho.reporting.engine.classic.core.style;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.config.Configuration;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * A style key represents a (key, class) pair. Style keys are used to access style attributes defined in a
 * <code>BandStyleSheet</code> or an <code>ElementStyleSheet</code>
 * <p/>
 * Note that this class also defines a static Hashtable in which all defined keys are stored.
 *
 * @author Thomas Morgner
 * @see BandStyleKeys
 * @see ElementStyleSheet
 */
public final class StyleKey implements Serializable, Cloneable {
  private static final Log logger = LogFactory.getLog( StyleKey.class );

  /**
   * Shared storage for the defined keys.
   */
  private static HashMap definedKeys;
  private static int definedKeySize;
  private static StyleKey[] definedKeysArray;
  private static List<StyleKey> definedKeysList;
  private static boolean locked;

  /**
   * The name of the style key.
   */
  public final String name;

  /**
   * The class of the value.
   */
  private Class valueType;

  /**
   * A unique int-key for the stylekey.
   */
  public final int identifier;

  /**
   * Whether this stylekey is transient. Transient keys will not be written when serializing a report.
   */
  private boolean trans;

  /**
   * Whether this stylekey is inheritable.
   */
  private boolean inheritable;

  /**
   * Creates a new style key.
   *
   * @param name
   *          the name (never null).
   * @param valueType
   *          the class of the value for this key (never null).
   * @param inheritable
   *          a flag indicating whether the value will be inherited from parent bands to child elements.
   * @param trans
   *          a flag indicating whether the style property should be saved. Transient properties are temporary artifacts
   *          and should not be stored in report definitions.
   */
  private StyleKey( final String name, final Class valueType, final boolean trans, final boolean inheritable ) {
    if ( name == null ) {
      throw new NullPointerException( "StyleKey.setName(...): null not permitted." );
    }
    if ( valueType == null ) {
      throw new NullPointerException( "ValueType must not be null" );
    }
    this.valueType = valueType;
    this.name = name;
    this.identifier = StyleKey.definedKeys.size();
    this.trans = trans;
    this.inheritable = inheritable;
  }

  /**
   * Returns the name of the key.
   *
   * @return the name.
   */
  public String getName() {
    return name;
  }

  /**
   * Returns the class of the value for this key.
   *
   * @return the class.
   */
  public Class<?> getValueType() {
    return valueType;
  }

  /**
   * Returns the key with the specified name. The given type is not checked against a possibly alredy defined
   * definition, it is assumed that the type is only given for a new key definition.
   *
   * @param name
   *          the name.
   * @param valueType
   *          the class.
   * @return the style key.
   */
  public static StyleKey getStyleKey( final String name, final Class valueType ) {
    return getStyleKey( name, valueType, false, true );
  }

  /**
   * Returns the key with the specified name. The given type is not checked against a possibly alredy defined
   * definition, it is assumed that the type is only given for a new key definition.
   *
   * @param name
   *          the name.
   * @param valueType
   *          the class.
   * @param inheritable
   *          a flag indicating whether the value will be inherited from parent bands to child elements.
   * @param trans
   *          a flag indicating whether the style property should be saved. Transient properties are temporary artifacts
   *          and should not be stored in report definitions.
   * @return the style key.
   */
  public static synchronized StyleKey getStyleKey( final String name, final Class valueType, final boolean trans,
      final boolean inheritable ) {
    if ( locked ) {
      throw new IllegalStateException( "StyleKeys have been locked after booting was completed." );
    }
    if ( definedKeys == null ) {
      definedKeys = new HashMap();
      definedKeySize = 0;
    }
    StyleKey key = (StyleKey) definedKeys.get( name );
    if ( key == null ) {
      key = new StyleKey( name, valueType, trans, inheritable );
      definedKeys.put( name, key );
      definedKeySize = definedKeys.size();
      definedKeysArray = null;
      definedKeysList = null;
    }
    return key;
  }

  public static synchronized void lock() {
    locked = true;
  }

  /**
   * Returns the key with the specified name.
   *
   * @param name
   *          the name.
   * @return the style key.
   */
  public static synchronized StyleKey getStyleKey( final String name ) {
    if ( definedKeys == null ) {
      return null;
    } else {
      return (StyleKey) definedKeys.get( name );
    }
  }

  /**
   * Indicates whether some other object is "equal to" this one.
   *
   * @param o
   *          the reference object with which to compare.
   * @return <code>true</code> if this object is the same as the obj argument; <code>false</code> otherwise.
   */
  public boolean equals( final Object o ) {
    if ( this == o ) {
      return true;
    }
    if ( !( o instanceof StyleKey ) ) {
      return false;
    }

    final StyleKey key = (StyleKey) o;

    if ( !name.equals( key.name ) ) {
      return false;
    }
    if ( !valueType.equals( key.valueType ) ) {
      return false;
    }

    return true;
  }

  /**
   * Returns a hash code value for the object. This method is supported for the benefit of hashtables such as those
   * provided by <code>java.util.Hashtable</code>.
   * <p/>
   *
   * @return a hash code value for this object.
   */
  public int hashCode() {
    return identifier;
  }

  /**
   * Replaces the automaticly generated instance with one of the defined stylekey instances or creates a new stylekey.
   *
   * @return the resolved element
   * @throws ObjectStreamException
   *           if the element could not be resolved.
   */
  private Object readResolve() throws ObjectStreamException {
    synchronized ( StyleKey.class ) {
      final StyleKey key = StyleKey.getStyleKey( name );
      if ( key != null ) {
        return key;
      }
      return StyleKey.getStyleKey( name, valueType, trans, inheritable );
    }
  }

  public boolean isTransient() {
    return trans;
  }

  /**
   * Returns a string representation of the object.
   *
   * @return a string representation of the object.
   */
  public String toString() {
    final StringBuffer b = new StringBuffer( 100 );
    b.append( "StyleKey={name='" );
    b.append( getName() );
    b.append( "', valueType='" );
    b.append( getValueType() );
    b.append( "'}" );
    return b.toString();
  }

  public Object clone() throws CloneNotSupportedException {
    return super.clone();
  }

  public boolean isInheritable() {
    return inheritable;
  }

  public int getIdentifier() {
    return identifier;
  }

  public static int getDefinedStyleKeyCount() {
    return definedKeySize;
  }

  public static synchronized List<StyleKey> getDefinedStyleKeysList() {
    if ( definedKeysList != null ) {
      return definedKeysList;
    }
    definedKeysList = Collections.unmodifiableList( Arrays.asList( getDefinedStyleKeys() ) );
    return definedKeysList;
  }

  public static synchronized StyleKey[] getDefinedStyleKeys() {
    if ( definedKeys == null ) {
      throw new IllegalStateException(
          "The engine has not been booted and the default keys have no been registered yet." );
    }
    if ( definedKeysArray != null ) {
      assertNoNullEntries();
      return definedKeysArray.clone();
    }

    final StyleKey[] keys = (StyleKey[]) definedKeys.values().toArray( new StyleKey[definedKeys.size()] );
    definedKeysArray = keys.clone();
    for ( int i = 0; i < keys.length; i++ ) {
      final StyleKey key = keys[i];
      definedKeysArray[key.identifier] = key;
    }
    assertNoNullEntries();
    return definedKeysArray.clone();
  }

  public static void assertNoNullEntries() {
    for ( int i = 0; i < definedKeysArray.length; i++ ) {
      final StyleKey styleKey = definedKeysArray[i];
      if ( styleKey == null ) {
        throw new NullPointerException();
      }
    }
  }

  /**
   * @noinspection ProhibitedExceptionCaught
   */
  public static synchronized void registerDefaults() {
    final Configuration config = ClassicEngineBoot.getInstance().getGlobalConfig();
    final Iterator it = config.findPropertyKeys( "org.pentaho.reporting.engine.classic.core.stylekeys." );
    final ClassLoader classLoader = ObjectUtilities.getClassLoader( StyleKey.class );

    while ( it.hasNext() ) {
      final String key = (String) it.next();
      final String keyClass = config.getConfigProperty( key );
      try {
        final Class c = Class.forName( keyClass, false, classLoader );
        registerClass( c );
      } catch ( ClassNotFoundException e ) {
        // ignore that class
        logger.warn( "Unable to register keys from " + keyClass );
      } catch ( NullPointerException e ) {
        // ignore invalid values as well.
        logger.warn( "Unable to register keys from " + keyClass );
      }
    }

  }

  public static synchronized void registerClass( final Class c ) {
    // Log.debug ("Registering stylekeys from " + c);
    try {
      final Field[] fields = c.getFields();
      for ( int i = 0; i < fields.length; i++ ) {
        final Field field = fields[i];
        final int modifiers = field.getModifiers();
        if ( Modifier.isPublic( modifiers ) && Modifier.isStatic( modifiers ) ) {
          if ( Modifier.isFinal( modifiers ) == false ) {
            logger.warn( "Invalid implementation: StyleKeys should be 'public static final': " + c );
          }
          if ( field.getType().isAssignableFrom( StyleKey.class ) ) {
            // noinspection UnusedDeclaration
            final StyleKey value = (StyleKey) field.get( null );
            // ignore the returned value, all we want is to trigger the key
            // creation
            // Log.debug ("Loaded key " + value);
          }
        }
      }
    } catch ( IllegalAccessException e ) {
      // wont happen, we've checked it..
      logger.warn( "Unable to register keys from " + c.getName() );
    }
  }

  @Deprecated // for tests only!
  static synchronized StyleKey addTestKey( String name, Class valueType, boolean trans, boolean inheritable ) {
    boolean wasLocked = locked;
    try {
      locked = false;
      return getStyleKey( name, valueType, trans, inheritable );
    } finally {
      locked = wasLocked;
      definedKeysArray = null;
      definedKeysList = null;
    }
  }

  @Deprecated // for tests only!
  static synchronized void removeTestKey( String name ) {
    try {
      if ( definedKeys != null ) {
        definedKeys.remove( name );
      }
      definedKeySize = definedKeys.size();
    } finally {
      definedKeysArray = null;
      definedKeysList = null;
    }
  }

}
