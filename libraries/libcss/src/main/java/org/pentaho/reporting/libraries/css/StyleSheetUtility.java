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

package org.pentaho.reporting.libraries.css;

import org.pentaho.reporting.libraries.css.dom.LayoutElement;
import org.pentaho.reporting.libraries.css.keys.box.BoxStyleKeys;
import org.pentaho.reporting.libraries.css.keys.font.FontStyleKeys;
import org.pentaho.reporting.libraries.css.keys.page.PageStyleKeys;
import org.pentaho.reporting.libraries.css.model.CSSPageRule;
import org.pentaho.reporting.libraries.css.model.StyleSheet;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValuePair;

import java.awt.print.PageFormat;
import java.awt.print.Paper;

/**
 * Creation-Date: Dec 3, 2006, 3:29:10 PM
 *
 * @author Thomas Morgner
 */
public class StyleSheetUtility {
  private StyleSheetUtility() {
  }

  public static CSSPageRule createRuleForPage( final StyleSheet style,
                                               final PageFormat format ) {
    final CSSPageRule rule = new CSSPageRule( style, null, null, null );
    updateRuleForPage( rule, format );
    return rule;
  }

  public static void updateRuleForPage( final CSSPageRule rule,
                                        final PageFormat format ) {
    if ( format == null ) {
      rule.removeProperty( BoxStyleKeys.MARGIN_TOP );
      rule.removeProperty( BoxStyleKeys.MARGIN_LEFT );
      rule.removeProperty( BoxStyleKeys.MARGIN_BOTTOM );
      rule.removeProperty( BoxStyleKeys.MARGIN_RIGHT );
      rule.removeProperty( PageStyleKeys.SIZE );
      //      rule.removeProperty(PageStyleKeys.HORIZONTAL_PAGE_SPAN);
      //      rule.removeProperty(PageStyleKeys.VERTICAL_PAGE_SPAN);
      return;
    }


    final double width = format.getWidth();
    final double height = format.getHeight();
    rule.setPropertyValueAsString( PageStyleKeys.SIZE,
      width + "pt " + height + "pt" );
    rule.setPropertyValueAsString( BoxStyleKeys.MARGIN_TOP, format.getImageableY() + "pt" );
    rule.setPropertyValueAsString( BoxStyleKeys.MARGIN_LEFT, format.getImageableX() + "pt" );

    final double marginRight = width - format.getImageableX() - format.getImageableWidth();
    final double marginBottom = height - format.getImageableY() - format.getImageableHeight();
    rule.setPropertyValueAsString( BoxStyleKeys.MARGIN_BOTTOM, marginBottom + "pt" );
    rule.setPropertyValueAsString( BoxStyleKeys.MARGIN_RIGHT, marginRight + "pt" );
    //    rule.setPropertyValueAsString(PageStyleKeys.HORIZONTAL_PAGE_SPAN, "1");
    //    rule.setPropertyValueAsString(PageStyleKeys.VERTICAL_PAGE_SPAN, "1");
  }

  public static PageFormat getPageFormat( final CSSPageRule rule,
                                          final int resolution ) {
    // This does not take any inheritance into account.
    final CSSValue sizeValue = rule.getPropertyCSSValue( PageStyleKeys.SIZE );
    if ( sizeValue instanceof CSSValuePair == false ) {
      // not a valid thing ..
      return null;
    }
    CSSValuePair sizePair = (CSSValuePair) sizeValue;
    final CSSValue firstValue = sizePair.getFirstValue();
    final CSSValue secondValue = sizePair.getSecondValue();
    final double width = convertLengthToDouble( firstValue, resolution );
    final double height = convertLengthToDouble( secondValue, resolution );
    if ( width == 0 || height == 0 ) {
      return null;
    }

    // next the margins ..
    final double marginLeft = convertLengthToDouble
      ( rule.getPropertyCSSValue( BoxStyleKeys.MARGIN_LEFT ), resolution );
    final double marginTop = convertLengthToDouble
      ( rule.getPropertyCSSValue( BoxStyleKeys.MARGIN_TOP ), resolution );
    final double marginRight = convertLengthToDouble
      ( rule.getPropertyCSSValue( BoxStyleKeys.MARGIN_RIGHT ), resolution );
    final double marginBottom = convertLengthToDouble
      ( rule.getPropertyCSSValue( BoxStyleKeys.MARGIN_BOTTOM ), resolution );

    if ( width < height ) {
      final Paper p = new Paper();
      p.setSize( width, height );
      p.setImageableArea( marginLeft, marginTop,
        width - marginLeft - marginRight,
        height - marginTop - marginBottom );
      final PageFormat pageFormat = new PageFormat();
      pageFormat.setPaper( p );
      pageFormat.setOrientation( PageFormat.PORTRAIT );
      return pageFormat;
    } else {
      final Paper p = new Paper();
      p.setSize( height, width );
      p.setImageableArea( marginLeft, marginTop,
        width - marginLeft - marginRight,
        height - marginTop - marginBottom );
      final PageFormat pageFormat = new PageFormat();
      pageFormat.setPaper( p );
      pageFormat.setOrientation( PageFormat.LANDSCAPE );
      return pageFormat;
    }

  }


  /**
   * Returns the length in point as a double primitive value. Be aware that using double-values is not very accurate.
   *
   * @param rawValue
   * @return
   */
  public static strictfp double convertLengthToDouble( final CSSValue rawValue,
                                                       final int resolution ) {
    if ( rawValue instanceof CSSNumericValue == false ) {
      return 0;
    }

    final CSSNumericValue value = (CSSNumericValue) rawValue;
    if ( CSSNumericType.PT.equals( value.getType() ) ) {
      return value.getValue();
    }
    if ( CSSNumericType.PC.equals( value.getType() ) ) {
      return ( value.getValue() / 12.0d );
    }
    if ( CSSNumericType.INCH.equals( value.getType() ) ) {
      return ( value.getValue() / 72.0d );
    }
    if ( CSSNumericType.CM.equals( value.getType() ) ) {
      return ( ( value.getValue() * 100 * 72.0d ) / 254.0d );
    }
    if ( CSSNumericType.MM.equals( value.getType() ) ) {
      return ( ( value.getValue() * 10 * 72.0d ) / 254.0d );
    }

    if ( CSSNumericType.PX.equals( value.getType() ) ) {
      // todo Read from a configuration file or so ..
      if ( resolution <= 0 ) {
        // we assume 72 pixel per inch ...
        return value.getValue();
      }
      return value.getValue() * 72d / resolution;
    }

    return 0;
  }

  public static strictfp double convertFontSizeToDouble( final CSSValue rawValue,
                                                         final int resolution,
                                                         final LayoutElement baseElement ) {
    if ( rawValue instanceof CSSNumericValue == false ) {
      return 0;
    }

    final CSSNumericValue value = (CSSNumericValue) rawValue;
    if ( CSSNumericType.PT.equals( value.getType() ) ) {
      return value.getValue();
    }
    if ( CSSNumericType.PC.equals( value.getType() ) ) {
      return ( value.getValue() / 12.0d );
    }
    if ( CSSNumericType.INCH.equals( value.getType() ) ) {
      return ( value.getValue() / 72.0d );
    }
    if ( CSSNumericType.CM.equals( value.getType() ) ) {
      return ( ( value.getValue() * 100 * 72.0d ) / 254.0d );
    }
    if ( CSSNumericType.MM.equals( value.getType() ) ) {
      return ( ( value.getValue() * 10 * 72.0d ) / 254.0d );
    }

    if ( CSSNumericType.PX.equals( value.getType() ) ) {
      // todo Read from a configuration file or so ..
      if ( resolution <= 0 ) {
        // we assume 72 pixel per inch ...
        return value.getValue();
      }
      return value.getValue() * 72d / resolution;
    }

    if ( baseElement != null ) {

      if ( CSSNumericType.EM.equals( value.getType() ) ) {
        // base is the font-size
        final CSSValue baseVal = baseElement.getLayoutStyle().getValue( FontStyleKeys.FONT_SIZE );
        return value.getValue() * convertLengthToDouble( baseVal, resolution );
      }
      if ( CSSNumericType.EX.equals( value.getType() ) ) {
        // base is the parent font's x-height.
        final CSSValue baseVal = baseElement.getLayoutStyle().getValue( FontStyleKeys.FONT_SIZE );
        // todo: cheating for now: We assume a sensible default and do not ask the font system.
        return value.getValue() * convertLengthToDouble( baseVal, resolution ) * 0.58;
      }
      if ( CSSNumericType.PERCENTAGE.equals( value.getType() ) ) {
        final CSSValue baseVal = baseElement.getLayoutStyle().getValue( FontStyleKeys.FONT_SIZE );
        return value.getValue() * convertLengthToDouble( baseVal, resolution ) / 100d;
      }

    }

    return 0;
  }


  public static strictfp CSSValue convertFontSize( final CSSValue rawValue,
                                                   final int resolution,
                                                   final LayoutElement baseElement ) {
    if ( rawValue instanceof CSSNumericValue == false ) {
      return rawValue;
    }
    final CSSNumericValue value = (CSSNumericValue) rawValue;
    if ( baseElement != null ) {

      final CSSValue baseVal = baseElement.getLayoutStyle().getValue( FontStyleKeys.FONT_SIZE );
      if ( baseVal instanceof CSSNumericValue == false ) {
        return CSSNumericValue.ZERO_LENGTH;
      }

      final CSSNumericValue baseNValue = (CSSNumericValue) baseVal;
      if ( CSSNumericType.EM.equals( value.getType() ) ) {
        // base is the font-size
        return CSSNumericValue.createValue( baseNValue.getNumericType(), value.getValue() * baseNValue.getValue() );
      }
      if ( CSSNumericType.EX.equals( value.getType() ) ) {
        // base is the parent font's x-height.
        // todo: cheating for now: We assume a sensible default and do not ask the font system.
        return CSSNumericValue
          .createValue( baseNValue.getNumericType(), value.getValue() * baseNValue.getValue() * 0.58 );
      }
      if ( CSSNumericType.PERCENTAGE.equals( value.getType() ) ) {
        return CSSNumericValue
          .createValue( baseNValue.getNumericType(), value.getValue() * baseNValue.getValue() / 100d );
      }

    }

    return rawValue;
  }

  public static strictfp CSSNumericValue convertLength( final CSSValue rawValue,
                                                        final CSSNumericValue basePercentage,
                                                        final LayoutElement baseElement ) {
    if ( rawValue instanceof CSSNumericValue == false ) {
      return CSSNumericValue.ZERO_LENGTH;
    }

    final CSSNumericValue value = (CSSNumericValue) rawValue;
    if ( CSSNumericType.PERCENTAGE.equals( value.getType() ) ) {
      return CSSNumericValue
        .createValue( basePercentage.getNumericType(), value.getValue() * basePercentage.getValue() / 100d );
    }

    if ( baseElement != null ) {
      final CSSValue baseVal = baseElement.getLayoutStyle().getValue( FontStyleKeys.FONT_SIZE );
      if ( baseVal instanceof CSSNumericValue == false ) {
        return CSSNumericValue.ZERO_LENGTH;
      }
      final CSSNumericValue baseNValue = (CSSNumericValue) baseVal;
      if ( CSSNumericType.EM.equals( value.getType() ) ) {
        // base is the font-size
        return CSSNumericValue.createValue( baseNValue.getNumericType(), value.getValue() * baseNValue.getValue() );
      }
      if ( CSSNumericType.EX.equals( value.getType() ) ) {
        // base is the parent font's x-height.
        // todo: cheating for now: We assume a sensible default and do not ask the font system.
        return CSSNumericValue
          .createValue( baseNValue.getNumericType(), value.getValue() * baseNValue.getValue() * 0.58 );
      }
      return baseNValue;
    }
    return CSSNumericValue.ZERO_LENGTH;
  }
}
