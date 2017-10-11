/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.model;

import org.pentaho.reporting.libraries.css.parser.StyleSheetParserUtil;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.util.Arrays;

/**
 * This class is a merger between the CSSStyleDeclaration and the other stylerule classes holding property name pairs.
 * Actually, this is what once was called a stylesheet in JFreeReport.
 * <p/>
 * StyleProperties are key as Strings and have CSSValues as mapped values..
 *
 * @author Thomas Morgner
 */
public abstract class CSSDeclarationRule extends StyleRule {
  private CSSValue[] styleValues;
  private boolean[] importantValues;
  private StyleSheetParserUtil styleSheetParserUtil;

  protected CSSDeclarationRule( final StyleSheet parentStyle,
                                final StyleRule parentRule ) {
    super( parentStyle, parentRule );
  }

  public boolean isImportant( StyleKey propertyName ) {
    if ( propertyName == null ) {
      throw new NullPointerException();
    }

    if ( importantValues == null ) {
      return false;
    }
    return importantValues[ propertyName.index ];
  }

  protected void setImportant( StyleKey propertyName, final boolean important ) {
    if ( propertyName == null ) {
      throw new NullPointerException();
    }

    if ( importantValues == null ) {
      final StyleKeyRegistry styleKeyRegistry = getStyleKeyRegistry();
      importantValues = new boolean[ styleKeyRegistry.getKeyCount() ];
    }

    importantValues[ propertyName.index ] = important;
  }

  public CSSValue getPropertyCSSValue( StyleKey propertyName ) {
    if ( propertyName == null ) {
      throw new NullPointerException();
    }
    if ( styleValues == null ) {
      return null;
    }
    return styleValues[ propertyName.index ];
  }

  /**
   * Parses the given value for the stylekey. As stylekeys are only defined for atomic style declarations, this method
   * will only affect a single name-value pair.
   *
   * @param styleKey
   * @param value
   */
  public void setPropertyValueAsString( final StyleKey styleKey,
                                        final String value ) {
    final StyleSheet parentStyle = getParentStyle();
    final ResourceKey source;
    if ( parentStyle == null ) {
      source = null;
    } else {
      source = parentStyle.getSource();
    }

    if ( styleSheetParserUtil == null ) {
      styleSheetParserUtil = new StyleSheetParserUtil();
    }

    final StyleSheet parent = getParentStyle();
    final CSSStyleRule cssValues;
    if ( parent != null ) {
      cssValues = styleSheetParserUtil.parseStyles
        ( parent.getNamespaces(), styleKey.getName(), value, source,
          parent.getResourceManager(), StyleKeyRegistry.getRegistry() );
    } else {
      final ResourceManager resourceManager = new ResourceManager();
      resourceManager.registerDefaults();

      cssValues = styleSheetParserUtil.parseStyles
        ( null, styleKey.getName(), value, source, resourceManager, StyleKeyRegistry.getRegistry() );
    }

    if ( cssValues != null ) {
      if ( cssValues.isEmpty() ) {
        return;
      }
      final boolean[] importantFlags = cssValues.getImportantValues();
      final CSSValue[] values = cssValues.getStyleValues();
      final StyleKey[] keys = cssValues.getPropertyKeysAsArray();
      for ( int i = 0; i < values.length; i++ ) {
        final CSSValue cssValue = values[ i ];
        if ( cssValue != null ) {
          setPropertyValue( keys[ i ], cssValue, importantFlags[ i ] );
        }
      }
    }
  }

  public void setPropertyValue( StyleKey propertyName, CSSValue value ) {
    setPropertyValue( propertyName, value, false );
  }

  public void setPropertyValue( StyleKey propertyName, CSSValue value, boolean important ) {
    if ( styleValues == null ) {
      final StyleKeyRegistry styleKeyRegistry = getStyleKeyRegistry();
      styleValues = new CSSValue[ styleKeyRegistry.getKeyCount() ];
    }

    styleValues[ propertyName.index ] = value;
    setImportant( propertyName, important );
  }

  public void removeProperty( StyleKey name ) {
    if ( styleValues == null ) {
      return;
    }

    setPropertyValue( name, null );
  }

  public void clear() {
    if ( styleValues != null ) {
      Arrays.fill( styleValues, null );
    }
    if ( importantValues != null ) {
      Arrays.fill( importantValues, false );
    }
  }

  public StyleKey[] getPropertyKeysAsArray() {
    return getStyleKeyRegistry().getKeys();
  }

  public CSSValue[] getStyleValues() {
    if ( styleValues == null ) {
      final StyleKeyRegistry styleKeyRegistry = getStyleKeyRegistry();
      styleValues = new CSSValue[ styleKeyRegistry.getKeyCount() ];
    }
    return (CSSValue[]) styleValues.clone();
  }

  public boolean[] getImportantValues() {
    if ( importantValues == null ) {
      importantValues = new boolean[ getStyleKeyRegistry().getKeyCount() ];
    }
    return (boolean[]) importantValues.clone();
  }

  public Object clone() throws CloneNotSupportedException {
    final CSSDeclarationRule rule = (CSSDeclarationRule) super.clone();
    if ( importantValues != null ) {
      rule.importantValues = (boolean[]) importantValues.clone();
    }
    if ( styleValues != null ) {
      rule.styleValues = (CSSValue[]) styleValues.clone();
    }
    return rule;
  }

  public boolean isEmpty() {
    return importantValues == null && styleValues == null;
  }
}
