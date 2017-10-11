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

package org.pentaho.reporting.libraries.css.parser.stylehandler.font;

import org.pentaho.reporting.libraries.css.keys.font.FontStyleKeys;
import org.pentaho.reporting.libraries.css.keys.line.LineStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.parser.stylehandler.line.LineHeightReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 28.11.2005, 17:05:01
 *
 * @author Thomas Morgner
 */
public class FontReadHandler implements CSSCompoundValueReadHandler {
  private FontStyleReadHandler styleReadHandler;
  private FontSizeReadHandler sizeReadHandler;
  private FontWeightReadHandler weightReadHandler;
  private FontVariantReadHandler variantReadHandler;
  private LineHeightReadHandler lineHeightReadHandler;
  private FontFamilyReadHandler fontFamilyReadHandler;

  public FontReadHandler() {
    this.styleReadHandler = new FontStyleReadHandler();
    this.sizeReadHandler = new FontSizeReadHandler();
    this.weightReadHandler = new FontWeightReadHandler();
    this.variantReadHandler = new FontVariantReadHandler();
    this.lineHeightReadHandler = new LineHeightReadHandler();
    this.fontFamilyReadHandler = new FontFamilyReadHandler();
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    // todo we ignore the font-family system font styles for now.

    CSSValue fontStyle = styleReadHandler.createValue( null, unit );
    if ( fontStyle != null ) {
      unit = unit.getNextLexicalUnit();
      if ( unit == null ) {
        return null;
      }
    }
    CSSValue fontVariant = variantReadHandler.createValue( null, unit );
    if ( fontVariant != null ) {
      unit = unit.getNextLexicalUnit();
      if ( unit == null ) {
        return null;
      }
    }
    CSSValue fontWeight = weightReadHandler.createValue( null, unit );
    if ( fontWeight != null ) {
      unit = unit.getNextLexicalUnit();
      if ( unit == null ) {
        return null;
      }
    }

    CSSValue fontSize = sizeReadHandler.createValue( null, unit );
    if ( fontSize == null ) {
      return null; // required value is missing
    }

    unit = unit.getNextLexicalUnit();
    if ( unit == null ) {
      return null; // font family missing
    }

    CSSValue lineHeight = null;
    if ( unit.getLexicalUnitType() == LexicalUnit.SAC_OPERATOR_SLASH ) {
      unit = unit.getNextLexicalUnit();
      if ( unit == null ) {
        return null;
      }

      lineHeight = lineHeightReadHandler.createValue( null, unit );
      if ( lineHeight == null ) {
        return null; // required sequence missing
      }
      unit = unit.getNextLexicalUnit();
      if ( unit == null ) {
        return null;
      }
    }

    CSSValue fontFamily = fontFamilyReadHandler.createValue( null, unit );
    if ( fontFamily == null ) {
      return null; // font family is required!
    }

    Map map = new HashMap();
    map.put( FontStyleKeys.FONT_FAMILY, fontFamily );
    map.put( FontStyleKeys.FONT_SIZE, fontSize );
    if ( lineHeight != null ) {
      map.put( LineStyleKeys.LINE_HEIGHT, lineHeight );
    }
    if ( fontWeight != null ) {
      map.put( FontStyleKeys.FONT_WEIGHT, fontWeight );
    }
    if ( fontVariant != null ) {
      map.put( FontStyleKeys.FONT_VARIANT, fontVariant );
    }
    if ( fontStyle != null ) {
      map.put( FontStyleKeys.FONT_STYLE, fontStyle );
    }
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] {
      FontStyleKeys.FONT_FAMILY,
      FontStyleKeys.FONT_SIZE,
      FontStyleKeys.FONT_WEIGHT,
      FontStyleKeys.FONT_VARIANT,
      FontStyleKeys.FONT_STYLE,
      LineStyleKeys.LINE_HEIGHT

    };
  }
}
