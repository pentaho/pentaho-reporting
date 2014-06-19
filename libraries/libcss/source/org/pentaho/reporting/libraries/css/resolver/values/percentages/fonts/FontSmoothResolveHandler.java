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
* Copyright (c) 2006 - 2013 Pentaho Corporation and Contributors.  All rights reserved.
*/

package org.pentaho.reporting.libraries.css.resolver.values.percentages.fonts;

import org.pentaho.reporting.libraries.css.keys.font.FontStyleKeys;
import org.pentaho.reporting.libraries.css.keys.font.FontSmooth;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.resolver.values.ResolveHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.dom.LayoutStyle;
import org.pentaho.reporting.libraries.css.dom.DocumentContext;
import org.pentaho.reporting.libraries.css.dom.LayoutOutputMetaData;
import org.pentaho.reporting.libraries.css.dom.OutputProcessorFeature;
import org.pentaho.reporting.libraries.css.StyleSheetUtility;

/**
 * Creation-Date: 18.12.2005, 20:29:20
 *
 * @author Thomas Morgner
 */
public class FontSmoothResolveHandler implements ResolveHandler
{
  public FontSmoothResolveHandler()
  {
  }


  /**
   * This indirectly defines the resolve order. The higher the order, the more
   * dependent is the resolver on other resolvers to be complete.
   *
   * @return
   */
  public StyleKey[] getRequiredStyles()
  {
    return new StyleKey[] {
            FontStyleKeys.FONT_SIZE
    };
  }

  /**
   * Resolves a single property.
   */
  public void resolve(final DocumentContext process,
                      final LayoutElement currentNode,
                      final StyleKey key)
  {
    final LayoutStyle layoutContext = currentNode.getLayoutStyle();
    final CSSValue value = layoutContext.getValue(key);
    if (value instanceof CSSNumericValue == false)
    {
      return;
    }

    final LayoutOutputMetaData metaData = process.getOutputMetaData();
    final int resolution = (int) metaData.getNumericFeatureValue(OutputProcessorFeature.DEVICE_RESOLUTION);
    final double fontSize = StyleSheetUtility.convertLengthToDouble(value, resolution);
    final double length = StyleSheetUtility.convertFontSizeToDouble(value, resolution, currentNode);

    if (fontSize < length)
    {
      layoutContext.setValue(FontStyleKeys.FONT_SMOOTH, FontSmooth.NEVER);
    }
    else
    {
      layoutContext.setValue(FontStyleKeys.FONT_SMOOTH, FontSmooth.ALWAYS);
    }
  }
}
