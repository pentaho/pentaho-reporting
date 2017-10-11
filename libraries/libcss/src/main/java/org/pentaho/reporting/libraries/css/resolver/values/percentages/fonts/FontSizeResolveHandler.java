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
* Copyright (c) 2006 - 2017 Hitachi Vantara and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.resolver.values.percentages.fonts;

import org.pentaho.reporting.libraries.css.LibCssBoot;
import org.pentaho.reporting.libraries.css.StyleSheetUtility;
import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.dom.OutputProcessorFeature;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;

/**
 * Creation-Date: 18.12.2005, 18:06:23
 *
 * @author Thomas Morgner
 */
public class FontSizeResolveHandler implements ResolveHandler {
  private double baseFontSize;
  private static final StyleKey[] EMPTY_KEYS = new StyleKey[ 0 ];

  public FontSizeResolveHandler() {
    baseFontSize = parseDouble( "org.jfree.layouting.defaults.FontSize", 12 );
  }

  private double parseDouble( final String configKey,
                              final double defaultValue ) {
    final LibCssBoot boot = LibCssBoot.getInstance();
    final String value = boot.getGlobalConfig().getConfigProperty( configKey );
    if ( value == null ) {
      return defaultValue;
    }
    try {
      return Double.parseDouble( value );
    } catch ( final NumberFormatException nfe ) {
      return defaultValue;
    }
  }

  /**
   * This indirectly defines the resolve order. The higher the order, the more dependent is the resolver on other
   * resolvers to be complete.
   *
   * @return
   */
  public StyleKey[] getRequiredStyles() {
    return EMPTY_KEYS;
  }

  public void resolve( final DocumentContext process,
                       final LayoutElement currentNode,
                       final StyleKey key ) {
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final CSSValue value = layoutContext.getValue( key );
    final LayoutElement parent = currentNode.getParentLayoutElement();

    if ( value instanceof CSSNumericValue == false ) {
      if ( parent == null ) {
        layoutContext.setValue( key, CSSNumericValue.createValue( CSSNumericType.PT, baseFontSize ) );
      } else {
        final LayoutStyle parentContext = parent.getLayoutStyle();
        layoutContext.setValue( key, parentContext.getValue( key ) );
      }
      return;
    }

    final int resolution = (int)
      process.getOutputMetaData().getNumericFeatureValue( OutputProcessorFeature.DEVICE_RESOLUTION );
    layoutContext.setValue( key, StyleSheetUtility.convertFontSize( value, resolution, currentNode ) );
  }
}
