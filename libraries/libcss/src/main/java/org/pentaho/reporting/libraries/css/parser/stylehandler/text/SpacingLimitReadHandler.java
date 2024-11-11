/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.text.TextStyleKeys;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSCompoundValueReadHandler;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.values.CSSConstant;
import org.pentaho.reporting.libraries.css.values.CSSNumericType;
import org.pentaho.reporting.libraries.css.values.CSSNumericValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.w3c.css.sac.LexicalUnit;

import java.util.HashMap;
import java.util.Map;

/**
 * Creation-Date: 24.05.2006, 15:13:13
 *
 * @author Thomas Morgner
 */
public abstract class SpacingLimitReadHandler implements CSSCompoundValueReadHandler {
  public static final CSSConstant NORMAL = new CSSConstant( "normal" );

  /**
   * Parses the LexicalUnit and returns a map of (StyleKey, CSSValue) pairs.
   *
   * @param unit
   * @return
   */
  public Map createValues( LexicalUnit unit ) {
    CSSValue optimum = parseSingleSpacingValue( unit );
    if ( optimum == null ) {
      return null;
    }
    unit = unit.getNextLexicalUnit();

    CSSValue minimum = parseSingleSpacingValue( unit );
    if ( minimum != null ) {
      unit = unit.getNextLexicalUnit();
    }

    CSSValue maximum = parseSingleSpacingValue( unit );
    final Map map = new HashMap();
    map.put( getMinimumKey(), minimum );
    map.put( TextStyleKeys.X_MAX_LETTER_SPACING, maximum );
    map.put( TextStyleKeys.X_OPTIMUM_LETTER_SPACING, optimum );
    return map;
  }

  protected abstract StyleKey getMinimumKey();

  protected abstract StyleKey getMaximumKey();

  protected abstract StyleKey getOptimumKey();

  private CSSValue parseSingleSpacingValue( final LexicalUnit value ) {
    if ( value == null ) {
      return null;
    }

    if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      if ( value.getStringValue().equalsIgnoreCase( "normal" ) ) {
        return SpacingLimitReadHandler.NORMAL;
      }
      return null;
    }
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_PERCENTAGE ) {
      return CSSNumericValue.createValue( CSSNumericType.PERCENTAGE,
        value.getFloatValue() );
    }

    return CSSValueFactory.createLengthValue( value );
  }

  public StyleKey[] getAffectedKeys() {
    return new StyleKey[] { getMinimumKey(), getMaximumKey(), getOptimumKey() };
  }
}
