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

package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.color.CSSSystemColors;
import org.pentaho.reporting.libraries.css.keys.text.TextDecorationMode;
import org.pentaho.reporting.libraries.css.keys.text.TextDecorationStyle;
import org.pentaho.reporting.libraries.css.keys.text.TextStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSAutoValue;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 03.12.2005, 19:06:09
 *
 * @author Thomas Morgner
 */
public class TextDecorationReadHandler extends OneOfConstantsReadHandler
  implements CSSCompoundValueReadHandler {
  public TextDecorationReadHandler() {
    super( false );
    addValue( new CSSConstant( "none" ) );
    addValue( new CSSConstant( "underline" ) );
    addValue( new CSSConstant( "overline" ) );
    addValue( new CSSConstant( "line-through" ) );
    addValue( new CSSConstant( "blink" ) );
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    final Map map = new HashMap();
    map.put( TextStyleKeys.TEXT_UNDERLINE_POSITION, CSSAutoValue.getInstance() );
    map.put( TextStyleKeys.TEXT_UNDERLINE_MODE, TextDecorationMode.CONTINUOUS );
    map.put( TextStyleKeys.TEXT_OVERLINE_MODE, TextDecorationMode.CONTINUOUS );
    map.put( TextStyleKeys.TEXT_LINE_THROUGH_MODE, TextDecorationMode.CONTINUOUS );
    map.put( TextStyleKeys.TEXT_UNDERLINE_COLOR, CSSSystemColors.CURRENT_COLOR );
    map.put( TextStyleKeys.TEXT_OVERLINE_COLOR, CSSSystemColors.CURRENT_COLOR );
    map.put( TextStyleKeys.TEXT_LINE_THROUGH_COLOR, CSSSystemColors.CURRENT_COLOR );
    map.put( TextStyleKeys.TEXT_UNDERLINE_WIDTH, CSSAutoValue.getInstance() );
    map.put( TextStyleKeys.TEXT_OVERLINE_WIDTH, CSSAutoValue.getInstance() );
    map.put( TextStyleKeys.TEXT_LINE_THROUGH_WIDTH, CSSAutoValue.getInstance() );
    map.put( TextStyleKeys.TEXT_UNDERLINE_STYLE, TextDecorationStyle.NONE );
    map.put( TextStyleKeys.TEXT_OVERLINE_STYLE, TextDecorationStyle.NONE );
    map.put( TextStyleKeys.TEXT_LINE_THROUGH_STYLE, TextDecorationStyle.NONE );

    while ( unit != null ) {
      CSSValue constant = lookupValue( unit );
      if ( constant == null ) {
        return null;
      }
      if ( constant.getCSSText().equals( "none" ) ) {
        map.put( TextStyleKeys.TEXT_UNDERLINE_STYLE, TextDecorationStyle.NONE );
        map.put( TextStyleKeys.TEXT_OVERLINE_STYLE, TextDecorationStyle.NONE );
        map.put( TextStyleKeys.TEXT_LINE_THROUGH_STYLE, TextDecorationStyle.NONE );
        return map;
      }
      if ( constant.getCSSText().equals( "blink" ) ) {
        map.put( TextStyleKeys.TEXT_BLINK, new CSSConstant( "blink" ) );
      } else if ( constant.getCSSText().equals( "underline" ) ) {
        map.put( TextStyleKeys.TEXT_UNDERLINE_STYLE, TextDecorationStyle.SOLID );
      } else if ( constant.getCSSText().equals( "overline" ) ) {
        map.put( TextStyleKeys.TEXT_OVERLINE_STYLE, TextDecorationStyle.SOLID );
      } else if ( constant.getCSSText().equals( "line-through" ) ) {
        map.put( TextStyleKeys.TEXT_LINE_THROUGH_STYLE, TextDecorationStyle.SOLID );
      }
      unit = unit.getNextLexicalUnit();
    }
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] {
      TextStyleKeys.TEXT_UNDERLINE_POSITION,
      TextStyleKeys.TEXT_UNDERLINE_MODE,
      TextStyleKeys.TEXT_OVERLINE_MODE,
      TextStyleKeys.TEXT_LINE_THROUGH_MODE,
      TextStyleKeys.TEXT_UNDERLINE_COLOR,
      TextStyleKeys.TEXT_OVERLINE_COLOR,
      TextStyleKeys.TEXT_LINE_THROUGH_COLOR,
      TextStyleKeys.TEXT_UNDERLINE_WIDTH,
      TextStyleKeys.TEXT_OVERLINE_WIDTH,
      TextStyleKeys.TEXT_LINE_THROUGH_WIDTH,
      TextStyleKeys.TEXT_UNDERLINE_STYLE,
      TextStyleKeys.TEXT_OVERLINE_STYLE,
      TextStyleKeys.TEXT_LINE_THROUGH_STYLE
    };
  }
}
