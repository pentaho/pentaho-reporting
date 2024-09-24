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

package org.pentaho.reporting.libraries.css.parser.stylehandler.content;

import org.pentaho.reporting.libraries.css.keys.content.ContentValues;
import org.pentaho.reporting.libraries.css.keys.list.ListStyleTypeGlyphs;
import org.pentaho.reporting.libraries.css.model.StyleKey;
import org.pentaho.reporting.libraries.css.parser.CSSValueFactory;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;
import org.pentaho.reporting.libraries.css.values.CSSAttrFunction;
import org.pentaho.reporting.libraries.css.values.CSSFunctionValue;
import org.pentaho.reporting.libraries.css.values.CSSStringType;
import org.pentaho.reporting.libraries.css.values.CSSStringValue;
import org.pentaho.reporting.libraries.css.values.CSSValue;
import org.pentaho.reporting.libraries.css.values.CSSValueList;
import org.w3c.css.sac.LexicalUnit;

import java.util.ArrayList;

/**
 * Creation-Date: 01.12.2005, 18:06:38
 *
 * @author Thomas Morgner
 */
public class ContentReadHandler extends OneOfConstantsReadHandler {
  public ContentReadHandler() {
    super( false );
    addValue( ContentValues.CLOSE_QUOTE );
    addValue( ContentValues.CONTENTS );
    addValue( ContentValues.DOCUMENT_URL );
    addValue( ContentValues.ENDNOTE );
    addValue( ContentValues.FOOTNOTE );
    addValue( ContentValues.LISTITEM );
    addValue( ContentValues.NO_CLOSE_QUOTE );
    addValue( ContentValues.NO_OPEN_QUOTE );
    addValue( ContentValues.OPEN_QUOTE );
    addValue( ContentValues.SECTIONNOTE );

    addValue( ListStyleTypeGlyphs.BOX );
    addValue( ListStyleTypeGlyphs.CHECK );
    addValue( ListStyleTypeGlyphs.CIRCLE );
    addValue( ListStyleTypeGlyphs.DIAMOND );
    addValue( ListStyleTypeGlyphs.DISC );
    addValue( ListStyleTypeGlyphs.HYPHEN );
    addValue( ListStyleTypeGlyphs.SQUARE );
  }

  public CSSValue createValue( StyleKey name, LexicalUnit value ) {
    if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
      final String text = value.getStringValue();
      if ( ContentValues.NONE.getCSSText().equals( text ) ) {
        return ContentValues.NONE;
      }
      if ( ContentValues.INHIBIT.getCSSText().equals( text ) ) {
        return ContentValues.INHIBIT;
      }
      if ( ContentValues.NORMAL.getCSSText().equals( text ) ) {
        return ContentValues.NORMAL;
      }
    }

    final ArrayList contents = new ArrayList();
    final ArrayList contentList = new ArrayList();
    while ( value != null ) {
      if ( value.getLexicalUnitType() == LexicalUnit.SAC_IDENT ) {
        CSSValue o = lookupValue( value );
        if ( o == null ) {
          // parse error ...
          return null;
        }
        contentList.add( o );
      } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_STRING_VALUE ) {
        contentList.add( new CSSStringValue( CSSStringType.STRING, value.getStringValue() ) );
      } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_URI ) {
        final CSSStringValue uriValue = CSSValueFactory.createUriValue( value );
        if ( uriValue == null ) {
          return null;
        }
        contentList.add( uriValue );
      } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_FUNCTION ||
        value.getLexicalUnitType() == LexicalUnit.SAC_COUNTER_FUNCTION ||
        value.getLexicalUnitType() == LexicalUnit.SAC_COUNTERS_FUNCTION ) {
        final CSSFunctionValue functionValue =
          CSSValueFactory.parseFunction( value );
        if ( functionValue == null ) {
          return null;
        }
        contentList.add( functionValue );
      } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_ATTR ) {
        final CSSAttrFunction attrFn = CSSValueFactory.parseAttrFunction( value );
        if ( attrFn == null ) {
          return null;
        }
        contentList.add( attrFn );
      } else if ( value.getLexicalUnitType() == LexicalUnit.SAC_OPERATOR_COMMA ) {
        final CSSValue[] values =
          (CSSValue[]) contentList.toArray(
            new CSSValue[ contentList.size() ] );
        contents.add( new CSSValueList( values ) );
        contentList.clear();
      }
      value = value.getNextLexicalUnit();
    }

    final CSSValue[] values =
      (CSSValue[]) contentList.toArray( new CSSValue[ contentList.size() ] );
    contents.add( new CSSValueList( values ) );
    return new CSSValueList( contents );
  }
}
