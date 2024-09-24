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

import org.pentaho.reporting.engine.classic.core.layout.style.SimpleStyleSheet;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;
import org.pentaho.reporting.libraries.serializer.SerializerHelper;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

/**
 * An element style-sheet contains zero, one or many attributes that affect the appearance of report elements.  For each
 * attribute, there is a predefined key that can be used to access that attribute in the style sheet.
 * <p/>
 * Every report element has an associated style-sheet.
 * <p/>
 * A style-sheet maintains a list of parent style-sheets.  If an attribute is not defined in a style-sheet, the code
 * refers to the parent style-sheets to see if the attribute is defined there.
 * <p/>
 * All StyleSheet entries are checked against the StyleKeyDefinition for validity.
 * <p/>
 * As usual, this implementation is not synchronized, we need the performance during the reporting.
 *
 * @author Thomas Morgner
 * @noinspection UnnecessaryUnboxing
 */
public class ElementStyleSheet extends AbstractStyleSheet implements Serializable, Cloneable {
  /**
   * The keys for the properties that have been explicitly set on the element.
   */
  private List<StyleKey> propertyKeys;

  /**
   * The properties that have been explicitly set on the element.
   */
  private transient Object[] properties;
  /*
   * In source, properties' flags are stored.
   * Since there are only three possible flag values (SOURCE_UNDEFINED, SOURCE_FROM_PARENT, SOURCE_DIRECT),
   * we can use only two bits for each flag instead of full byte.
   *
   * Therefore, to address a byte, where a pair of bits related to a property is put,
   * it is necessary to divide the property's index by 4. Division by 4 is the same as right shifting by 2.
   * After the byte's index has been computed, the pair's "internal" position can be shifted to the lowest bits,
   * and these bits give the flag's value.
   */
  private byte[] source;

  private static final byte SOURCE_UNDEFINED = 0;
  private static final byte SOURCE_FROM_PARENT = 1;
  private static final byte SOURCE_DIRECT = 2;

  /**
   * Style change support.
   */
  private transient StyleChangeSupport styleChangeSupport;

  private long modificationCount;
  private long changeTrackerHash;
  private static final StyleKey[] EMPTY_KEYS = new StyleKey[ 0 ];

  /**
   * Creates a new element style-sheet. The style-sheet initially contains no attributes, and has no parent
   * style-sheets.
   */
  public ElementStyleSheet() {
    this.styleChangeSupport = new StyleChangeSupport( this );
    this.propertyKeys = StyleKey.getDefinedStyleKeysList();
    if ( propertyKeys.isEmpty() || propertyKeys.get( 0 ) == null ) {
      throw new IllegalStateException( "ReportingEngine has not been initialized properly." );
    }
  }

  public long getChangeTracker() {
    return ( changeTrackerHash << 16 ) | modificationCount;
  }

  private byte getFlag( int index ) {
    final int packedByte = index >> 2;
    final int shift = ( index & 3 ) << 1;
    return (byte) ( ( source[packedByte] >> shift ) & 3 );
  }

  private void setFlag( int index, byte value ) {
    final int packedByte = index >> 2;
    final int shift = ( index & 3 ) << 1;
    final int cleared = source[packedByte] & ~( 3 << shift );
    source[packedByte] = (byte) ( cleared | ( value << shift ) );
  }

  /**
   * Returns true, if the given key is locally defined, false otherwise.
   *
   * @param key the key to test
   * @return true, if the key is local, false otherwise.
   */
  public boolean isLocalKey( final StyleKey key ) {
    if ( source == null ) {
      return false;
    }
    final int identifier = key.identifier;
    if ( properties.length <= identifier ) {
      return false;
    }
    return getFlag( identifier ) == SOURCE_DIRECT;
  }

  private void pruneCachedEntries() {
    if ( source != null && properties != null ) {
      for ( int i = 0, len = properties.length; i < len; i++ ) {
        if ( getFlag( i ) == SOURCE_FROM_PARENT ) {
          setFlag( i, SOURCE_UNDEFINED );
          properties[ i ] = null;
        }
      }
    }
  }

  public final Object[] toArray() {
    final List<StyleKey> keys = propertyKeys;
    final int size = keys.size();
    final Object[] data = new Object[ size ];
    if ( source == null ) {
      source = new byte[ ( size + 3 ) >> 2 ];
      properties = new Object[ size ];
    }

    for ( int i = 0; i < size; i++ ) {
      final StyleKey key = keys.get( i );
      if ( key == null ) {
        throw new NullPointerException();
      }
      final int identifier = key.identifier;
      final byte sourceHint = getFlag( identifier );
      if ( sourceHint == SOURCE_UNDEFINED ) {
        data[ identifier ] = getStyleProperty( key );
      } else {
        data[ identifier ] = properties[ identifier ];
      }
    }
    return data;
  }

  /**
   * Returns the value of a style.  If the style is not found in this style-sheet, the code looks in the parent
   * style-sheets.  If the style is not found in any of the parent style-sheets, then the default value (possibly
   * <code>null</code>) is returned.
   *
   * @param key          the style key.
   * @param defaultValue the default value (<code>null</code> permitted).
   * @return the value.
   */
  public Object getStyleProperty( final StyleKey key, final Object defaultValue ) {
    final int identifier = key.identifier;
    if ( properties != null ) {
      if ( properties.length <= identifier ) {
        throw new IllegalStateException();
      }

      final byte source = getFlag( identifier );
      if ( source != SOURCE_UNDEFINED ) {
        final Object value = properties[ identifier ];
        if ( value == null ) {
          return defaultValue;
        }
        return value;
      }
    }

    putInCache( key, null, SOURCE_FROM_PARENT );
    return defaultValue;
  }

  /**
   * Puts an object into the cache (if caching is enabled).
   *
   * @param key   the stylekey for that object
   * @param value the object.
   */
  private void putInCache( final StyleKey key, final Object value, final byte sourceHint ) {
    ensurePropertiesReady();

    final int identifier = key.identifier;
    properties[ identifier ] = value;
    setFlag( identifier, sourceHint );
  }

  /**
   * Sets a boolean style property.
   *
   * @param key   the style key (<code>null</code> not permitted).
   * @param value the value.
   * @throws NullPointerException if the given key is null.
   * @throws ClassCastException   if the value cannot be assigned with the given key.
   */
  public void setBooleanStyleProperty( final StyleKey key, final boolean value ) {
    if ( value ) {
      setStyleProperty( key, Boolean.TRUE );
    } else {
      setStyleProperty( key, Boolean.FALSE );
    }
  }

  /**
   * Sets a style property (or removes the style if the value is <code>null</code>).
   *
   * @param key   the style key (<code>null</code> not permitted).
   * @param value the value.
   * @throws NullPointerException if the given key is null.
   * @throws ClassCastException   if the value cannot be assigned with the given key.
   */
  public void setStyleProperty( final StyleKey key, final Object value ) {
    if ( key == null ) {
      throw new NullPointerException( "ElementStyleSheet.setStyleProperty: key is null." );
    }

    final int identifier = key.identifier;
    if ( value == null ) {
      if ( properties != null ) {
        if ( properties[ identifier ] == null ) {
          return;
        }

        // invalidate the cache ..
        putInCache( key, null, SOURCE_UNDEFINED );
        updateChangeTracker( key, null );
        styleChangeSupport.fireStyleRemoved( key );
      }
      return;
    }

    if ( key.getValueType().isAssignableFrom( value.getClass() ) == false ) {
      throw new ClassCastException( "Value for key " + key.getName()
        + " is not assignable: " + value.getClass()
        + " is not assignable from " + key.getValueType() );
    }
    ensurePropertiesReady();

    if ( ObjectUtilities.equal( properties[ identifier ], value ) ) {
      // no need to change anything ..
      return;
    }

    // invalidate the cache ..
    putInCache( key, value, SOURCE_DIRECT );
    updateChangeTracker( key, value );

    styleChangeSupport.fireStyleChanged( key, value );
  }

  private void ensurePropertiesReady() {
    if ( properties == null ) {
      final int definedStyleKeyCount = propertyKeys.size();
      properties = new Object[ definedStyleKeyCount ];
      source = new byte[ ( definedStyleKeyCount + 3 ) >> 2 ];
    }
  }

  /**
   * Creates and returns a copy of this object. After the cloning, the new StyleSheet is no longer registered with its
   * parents.
   *
   * @return a clone of this instance.
   * @see Cloneable
   */
  public ElementStyleSheet clone() {
    final ElementStyleSheet sc = (ElementStyleSheet) super.clone();
    if ( properties != null ) {
      sc.properties = properties.clone();
    }
    if ( source != null ) {
      sc.source = source.clone();
    }
    // noinspection CloneCallsConstructors
    sc.styleChangeSupport = new StyleChangeSupport( sc );
    sc.pruneCachedEntries();
    return sc;
  }

  public ElementStyleSheet derive( final boolean preserveId ) {
    final ElementStyleSheet sc = (ElementStyleSheet) super.derive( preserveId );
    if ( properties != null ) {
      sc.properties = properties.clone();
    }
    if ( source != null ) {
      sc.source = source.clone();
    }
    // noinspection CloneCallsConstructors
    sc.styleChangeSupport = new StyleChangeSupport( sc );
    sc.pruneCachedEntries();
    return sc;
  }

  public StyleKey[] getDefinedPropertyNamesArray() {
    if ( source == null ) {
      return ElementStyleSheet.EMPTY_KEYS;
    }

    final StyleKey[] retval = propertyKeys.toArray( new StyleKey[ propertyKeys.size() ] );
    for ( int i = 0, len = retval.length; i < len; i++ ) {
      if ( getFlag( i ) != SOURCE_DIRECT ) {
        retval[ i ] = null;
      }
    }
    return retval;
  }

  /**
   * Adds a {@link StyleChangeListener}.
   *
   * @param l the listener.
   */
  public void addListener( final StyleChangeListener l ) {
    styleChangeSupport.addListener( l );
  }

  /**
   * Removes a {@link StyleChangeListener}.
   *
   * @param l the listener.
   */
  public void removeListener( final StyleChangeListener l ) {
    styleChangeSupport.removeListener( l );
  }

  private void updateChangeTracker( StyleKey key, Object value ) {
    modificationCount += 1;
    changeTrackerHash = changeTrackerHash * 31 + key.getIdentifier();
    if ( value == null ) {
      changeTrackerHash = changeTrackerHash * 31;
    } else {
      changeTrackerHash = changeTrackerHash * 31 + value.hashCode();
    }
  }

  /**
   * Helper method for serialization.
   *
   * @param out the output stream where to write the object.
   * @throws IOException if errors occur while writing the stream.
   */
  private void writeObject( final ObjectOutputStream out ) throws IOException {
    out.defaultWriteObject();
    if ( properties == null ) {
      out.writeInt( 0 );
    } else {
      final int size = properties.length;
      out.writeInt( size );
      SerializerHelper helper = SerializerHelper.getInstance();
      for ( int i = 0; i < size; i++ ) {
        final Object value = properties[ i ];
        helper.writeObject( value, out );
      }
    }
  }

  /**
   * Helper method for serialization.
   *
   * @param in the input stream from where to read the serialized object.
   * @throws IOException            when reading the stream fails.
   * @throws ClassNotFoundException if a class definition for a serialized object could not be found.
   */
  private void readObject( final ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    final int size = in.readInt();

    propertyKeys = StyleKey.getDefinedStyleKeysList();
    styleChangeSupport = new StyleChangeSupport( this );

    if ( size == 0 ) {
      properties = null;
      return;
    }

    if ( size != propertyKeys.size() ) {
      throw new IOException(
        "Encountered a different style-system configuration. This report cannot be deserialized." );
    }
    if ( propertyKeys.get( 0 ) == null ) {
      throw new IllegalStateException();
    }
    properties = new Object[ size ];

    final Object[] values = new Object[ size ];
    final SerializerHelper serHelper = SerializerHelper.getInstance();
    for ( int i = 0; i < size; i++ ) {
      values[ i ] = serHelper.readObject( in );
    }

    for ( int i = 0; i < size; i++ ) {
      final StyleKey key = propertyKeys.get( i );
      if ( key != null ) {
        final int identifier = key.identifier;
        properties[ identifier ] = values[ i ];
      }
    }
  }

  /**
   * Returns the property keys. This must return the same set of keys as a call to StyleSheet.getDefinedKeys(), but it
   * allows us to avoid the synchronization on that call.
   *
   * @return the local copy of the style keys.
   */
  public StyleKey[] getPropertyKeys() {
    return propertyKeys.toArray( new StyleKey[ propertyKeys.size() ] );
  }

  public List<StyleKey> getPropertyKeyList() {
    return propertyKeys;
  }

  public void addAll( final ElementStyleSheet sourceStyleSheet ) {
    if ( sourceStyleSheet.source == null || sourceStyleSheet.properties == null ) {
      return;
    }

    ensurePropertiesReady();

    for ( int i = 0, len = sourceStyleSheet.properties.length; i < len; i++ ) {
      final byte sourceFlag = sourceStyleSheet.getFlag( i );
      if ( sourceFlag == SOURCE_DIRECT ) {
        properties[ i ] = sourceStyleSheet.properties[ i ];
        setFlag( i, sourceFlag );
      }
    }
  }

  public void addInherited( final ElementStyleSheet sourceStyleSheet ) {
    if ( sourceStyleSheet.source == null || sourceStyleSheet.properties == null ) {
      return;
    }
    ensurePropertiesReady();

    for ( int i = 0, len = properties.length; i < len; i++ ) {
      if ( propertyKeys.get( i ).isInheritable() == false ) {
        continue;
      }
      final byte sourceFlag = sourceStyleSheet.getFlag( i );
      if ( sourceFlag == SOURCE_DIRECT ) {
        properties[ i ] = sourceStyleSheet.properties[ i ];
        setFlag( i, SOURCE_FROM_PARENT );
      }
    }
  }

  public void addInherited( final SimpleStyleSheet sourceStyleSheet ) {
    ensurePropertiesReady();

    for ( int i = 0, len = properties.length; i < len; i++ ) {
      StyleKey styleKey = propertyKeys.get( i );
      if ( styleKey.isInheritable() == false ) {
        continue;
      }

      properties[ i ] = sourceStyleSheet.getStyleProperty( styleKey, null );
      setFlag( i, SOURCE_FROM_PARENT );
    }
  }

  public void addDefault( final ElementStyleSheet sourceStyleSheet ) {
    if ( sourceStyleSheet.source == null || sourceStyleSheet.properties == null ) {
      return;
    }
    ensurePropertiesReady();

    for ( int i = 0, len = properties.length; i < len; i++ ) {
      final byte sourceFlag = sourceStyleSheet.getFlag( i );
      if ( sourceFlag == SOURCE_DIRECT && getFlag( i ) == SOURCE_UNDEFINED ) {
        properties[ i ] = sourceStyleSheet.properties[ i ];
        setFlag( i, SOURCE_DIRECT );
      }
    }
  }

  public void clear() {
    if ( source == null || properties == null ) {
      return;
    }

    changeTrackerHash = 0;
    modificationCount = 0;

    Arrays.fill( properties, null );
    // SOURCE_UNDEFINED is 0, hence it can be used for clearing all four flags
    Arrays.fill( source, SOURCE_UNDEFINED );
  }

  public long getModificationCount() {
    return modificationCount;
  }

  protected void setModificationCount( final long modificationCount ) {
    this.modificationCount = modificationCount;
  }

  public long getChangeTrackerHash() {
    return changeTrackerHash;
  }

  protected void setChangeTrackerHash( final long changeTracker ) {
    this.changeTrackerHash = changeTracker;
  }

  public void copyFrom( final ElementStyleSheet style ) {
    this.changeTrackerHash = style.changeTrackerHash;
    this.modificationCount = style.modificationCount;
    this.propertyKeys = style.propertyKeys;
    if ( style.source != null ) {
      this.source = style.source.clone();
    } else if ( this.source != null ) {
      Arrays.fill( this.source, SOURCE_UNDEFINED );
    }
    if ( style.properties != null ) {
      this.properties = style.properties.clone();
    } else if ( this.properties != null ) {
      Arrays.fill( properties, null );
    }
  }
}
