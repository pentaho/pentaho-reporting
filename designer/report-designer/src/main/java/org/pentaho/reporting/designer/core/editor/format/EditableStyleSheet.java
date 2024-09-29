/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/


package org.pentaho.reporting.designer.core.editor.format;

import org.pentaho.reporting.engine.classic.core.Element;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ResolverStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.StyleKey;
import org.pentaho.reporting.engine.classic.core.style.resolver.SimpleStyleResolver;
import org.pentaho.reporting.libraries.base.util.ObjectUtilities;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class EditableStyleSheet extends ElementStyleSheet {
  private HashSet<StyleKey> editedKeys;
  private HashSet<StyleKey> removedKeys;
  private HashMap<StyleKey, Object> parentValues;

  public EditableStyleSheet() {
    editedKeys = new HashSet<StyleKey>();
    removedKeys = new HashSet<StyleKey>();
    parentValues = new HashMap<StyleKey, Object>();
  }

  public void copyParentValues( final ElementStyleSheet parent ) {
    if ( parent != null ) {
      final StyleKey[] definedPropertyNamesArray = parent.getDefinedPropertyNamesArray();
      for ( int i = 0; i < definedPropertyNamesArray.length; i++ ) {
        final StyleKey styleKey = definedPropertyNamesArray[ i ];
        if ( styleKey == null ) {
          continue;
        }
        setStyleProperty( styleKey, parent.getStyleProperty( styleKey ) );
      }
      final StyleKey[] propertyKeys = parent.getPropertyKeys();
      for ( int i = 0; i < propertyKeys.length; i++ ) {
        final StyleKey propertyKey = propertyKeys[ i ];
        parentValues.put( propertyKey, parent.getStyleProperty( propertyKey ) );
      }
    }

    editedKeys.clear();
    removedKeys.clear();
  }

  public void clearEdits() {
    editedKeys.clear();
    removedKeys.clear();

    final StyleKey[] propertyKeys = getPropertyKeys();
    for ( int i = 0; i < propertyKeys.length; i++ ) {
      final StyleKey propertyKey = propertyKeys[ i ];
      parentValues.put( propertyKey, getStyleProperty( propertyKey ) );
    }
  }

  public static EditableStyleSheet create( final List<Element> visualElements ) {
    return create( visualElements.toArray( new Element[ visualElements.size() ] ) );
  }

  public static EditableStyleSheet create( final Element... visualElements ) {
    final SimpleStyleResolver styleResolver = new SimpleStyleResolver( true );

    // collect all common values ..
    final StyleKey[] keys = StyleKey.getDefinedStyleKeys();
    final Object[] values = new Object[ keys.length ];
    final ResolverStyleSheet[] styles = new ResolverStyleSheet[ visualElements.length ];
    for ( int i = 0; i < styles.length; i++ ) {
      final ResolverStyleSheet style = new ResolverStyleSheet();
      styleResolver.resolve( visualElements[ i ], style );
      styles[ i ] = style;
    }

    for ( int i = 0; i < keys.length; i++ ) {
      final StyleKey styleKey = keys[ i ];
      for ( int elementIdx = 0; elementIdx < visualElements.length; elementIdx++ ) {
        final Object o = styles[ elementIdx ].getStyleProperty( styleKey );
        if ( values[ i ] == null ) {
          values[ i ] = o;
        } else {
          if ( ObjectUtilities.equal( values[ i ], o ) == false ) {
            values[ i ] = null;
            break;
          }
        }
      }
    }

    final EditableStyleSheet styleSheet = new EditableStyleSheet();
    for ( int i = 0; i < keys.length; i++ ) {
      final StyleKey styleKey = keys[ i ];
      styleSheet.setStyleProperty( styleKey, values[ i ] );
    }
    return styleSheet;
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
    final Object styleProperty = parentValues.get( key );
    if ( styleProperty == value || ObjectUtilities.equal( styleProperty, value ) ) {
      return;
    }
    editedKeys.add( key );
    if ( value == null ) {
      removedKeys.add( key );
    } else {
      removedKeys.remove( key );
    }
    super.setStyleProperty( key, value );
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
    if ( removedKeys.contains( key ) ) {
      return defaultValue;
    }
    return super.getStyleProperty( key, defaultValue );
  }

  public StyleKey[] getDefinedPropertyNamesArray() {
    final StyleKey[] keys = getPropertyKeys();
    for ( int i = 0; i < keys.length; i++ ) {
      final StyleKey key = keys[ i ];
      if ( editedKeys.contains( key ) == false ) {
        keys[ i ] = null;
      }
    }
    return keys;
  }
}
