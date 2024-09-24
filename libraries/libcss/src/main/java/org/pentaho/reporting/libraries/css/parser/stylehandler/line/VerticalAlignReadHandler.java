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

package org.pentaho.reporting.libraries.css.parser.stylehandler.line;

import org.pentaho.reporting.libraries.css.keys.line.AlignmentBaseline;
import org.pentaho.reporting.libraries.css.keys.line.BaselineShift;
import org.pentaho.reporting.libraries.css.keys.line.LineStyleKeys;
import org.pentaho.reporting.libraries.css.keys.line.VerticalAlign;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSAutoValue;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Warning: This *is* a compound property, but one if its values depend on the element structure and it changes its
 * meaning if used in Tables.
 *
 * @author Thomas Morgner
 */
public class VerticalAlignReadHandler extends OneOfConstantsReadHandler
  implements CSSCompoundValueReadHandler {
  public VerticalAlignReadHandler() {
    super( true );
    addValue( VerticalAlign.BASELINE );
    addValue( VerticalAlign.BOTTOM );
    addValue( VerticalAlign.CENTRAL );
    addValue( VerticalAlign.MIDDLE );
    addValue( VerticalAlign.SUB );
    addValue( VerticalAlign.SUPER );
    addValue( VerticalAlign.TEXT_BOTTOM );
    addValue( VerticalAlign.TEXT_TOP );
    addValue( VerticalAlign.USE_SCRIPT );
    addValue( VerticalAlign.TOP );
  }

  protected CSSValue lookupValue( final LexicalUnit value ) {
    CSSValue constant = super.lookupValue( value );
    if ( constant != null ) {
      return constant;
    } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_PERCENTAGE ) {
      return CSSNumericValue.createValue( CSSNumericType.PERCENTAGE,
        value.getFloatValue() );
    }

    return CSSValueFactory.createLengthValue( value );
  }

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    CSSValue value = lookupValue( unit );
    HashMap map = new HashMap();
    map.put( LineStyleKeys.VERTICAL_ALIGN, value );
    map.put( LineStyleKeys.ALIGNMENT_ADJUST, CSSAutoValue.getInstance() );
    map.put( LineStyleKeys.DOMINANT_BASELINE, CSSAutoValue.getInstance() );

    if ( CSSAutoValue.getInstance().equals( value ) ) {
      map.put( LineStyleKeys.ALIGNMENT_BASELINE, AlignmentBaseline.BASELINE );
      map.put( LineStyleKeys.BASELINE_SHIFT, BaselineShift.BASELINE );
    } else if ( VerticalAlign.BASELINE.equals( value ) ) {
      map.put( LineStyleKeys.ALIGNMENT_BASELINE, AlignmentBaseline.USE_SCRIPT );
      map.put( LineStyleKeys.BASELINE_SHIFT, BaselineShift.BASELINE );
    } else if ( VerticalAlign.BOTTOM.equals( value ) ) {
      map.put( LineStyleKeys.ALIGNMENT_BASELINE, AlignmentBaseline.AFTER_EDGE );
      map.put( LineStyleKeys.BASELINE_SHIFT, BaselineShift.BASELINE );
    } else if ( VerticalAlign.CENTRAL.equals( value ) ) {
      map.put( LineStyleKeys.ALIGNMENT_BASELINE, AlignmentBaseline.CENTRAL );
      map.put( LineStyleKeys.BASELINE_SHIFT, BaselineShift.BASELINE );
    } else if ( VerticalAlign.MIDDLE.equals( value ) ) {
      map.put( LineStyleKeys.ALIGNMENT_BASELINE, AlignmentBaseline.MIDDLE );
      map.put( LineStyleKeys.BASELINE_SHIFT, BaselineShift.BASELINE );
    } else if ( VerticalAlign.SUB.equals( value ) ) {
      map.put( LineStyleKeys.ALIGNMENT_BASELINE, AlignmentBaseline.USE_SCRIPT );
      map.put( LineStyleKeys.BASELINE_SHIFT, BaselineShift.SUB );
    } else if ( VerticalAlign.SUPER.equals( value ) ) {
      map.put( LineStyleKeys.ALIGNMENT_BASELINE, AlignmentBaseline.USE_SCRIPT );
      map.put( LineStyleKeys.BASELINE_SHIFT, BaselineShift.SUPER );
    } else if ( VerticalAlign.TEXT_BOTTOM.equals( value ) ) {
      map.put( LineStyleKeys.ALIGNMENT_BASELINE, AlignmentBaseline.TEXT_AFTER_EDGE );
      map.put( LineStyleKeys.BASELINE_SHIFT, BaselineShift.BASELINE );
    } else if ( VerticalAlign.TEXT_TOP.equals( value ) ) {
      map.put( LineStyleKeys.ALIGNMENT_BASELINE, AlignmentBaseline.TEXT_BEFORE_EDGE );
      map.put( LineStyleKeys.BASELINE_SHIFT, BaselineShift.BASELINE );
    } else if ( VerticalAlign.TOP.equals( value ) ) {
      map.put( LineStyleKeys.ALIGNMENT_BASELINE, AlignmentBaseline.BEFORE_EDGE );
      map.put( LineStyleKeys.BASELINE_SHIFT, BaselineShift.BASELINE );
    } else if ( VerticalAlign.USE_SCRIPT.equals( value ) ) {
      map.put( LineStyleKeys.ALIGNMENT_BASELINE, AlignmentBaseline.USE_SCRIPT );
      map.put( LineStyleKeys.BASELINE_SHIFT, CSSAutoValue.getInstance() );
    } else {
      // Todo: handle the case when valign is a length or percentage
      map.put( LineStyleKeys.ALIGNMENT_BASELINE, AlignmentBaseline.BASELINE );
      map.put( LineStyleKeys.BASELINE_SHIFT, BaselineShift.BASELINE );
      map.put( LineStyleKeys.ALIGNMENT_ADJUST, value );
    }
    return map;
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] {
      LineStyleKeys.VERTICAL_ALIGN,
      LineStyleKeys.ALIGNMENT_BASELINE,
      LineStyleKeys.DOMINANT_BASELINE,
      LineStyleKeys.ALIGNMENT_ADJUST,
      LineStyleKeys.BASELINE_SHIFT
    };
  }
}
