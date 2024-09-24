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

package org.pentaho.reporting.libraries.formula.util;

import org.pentaho.reporting.libraries.base.util.ArgumentNullException;
import org.pentaho.reporting.libraries.base.util.DebugLog;
import org.pentaho.reporting.libraries.formula.DefaultFormulaContext;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.lvalues.ContextLookup;
import org.pentaho.reporting.libraries.formula.lvalues.FormulaFunction;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.lvalues.StaticValue;
import org.pentaho.reporting.libraries.formula.lvalues.Term;
import org.pentaho.reporting.libraries.formula.parser.FormulaParser;
import org.pentaho.reporting.libraries.formula.parser.ParseException;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FormulaUtil {
  private static final char QUOTE_CHAR = '"';

  private FormulaUtil() {
  }

  public static String quoteReference( final String reference ) {
    if ( reference == null ) {
      throw new NullPointerException();
    }
    final char[] referenceChars = reference.toCharArray();
    if ( isQuotingNeeded( referenceChars ) == false ) {
      return '[' + reference + ']';
    }

    return '[' + quoteString( reference ) + ']';
  }

  private static boolean isQuotingNeeded( final char[] referenceChars ) {
    if ( referenceChars == null ) {
      throw new NullPointerException();
    }
    for ( int i = 0; i < referenceChars.length; i++ ) {
      final char c = referenceChars[ i ];
      if ( Character.isJavaIdentifierPart( c ) == false ) {
        return true;
      }
    }
    return false;
  }

  public static String quoteString( final String text ) {
    if ( text == null ) {
      return null;
    }
    final StringBuilder b = new StringBuilder( text.length() );
    final char[] chars = text.toCharArray();
    b.append( QUOTE_CHAR );
    for ( int i = 0; i < chars.length; i++ ) {
      final char c = chars[ i ];
      if ( c == QUOTE_CHAR ) {
        b.append( QUOTE_CHAR );
      }
      b.append( c );
    }
    b.append( QUOTE_CHAR );
    return b.toString();
  }

  public static String[] getReferences( final String formula ) throws ParseException {
    if ( formula == null ) {
      throw new NullPointerException();
    }
    final String formulaExpression = extractFormula( formula );
    if ( formulaExpression == null ) {
      throw new ParseException( "Formula is invalid" );
    }
    return getReferences( new Formula( formulaExpression ) );
  }

  public static String[] getReferences( final Formula formula ) {
    if ( formula == null ) {
      throw new NullPointerException();
    }
    final LinkedHashMap<String, Boolean> map = new LinkedHashMap<String, Boolean>();
    final LValue lValue = formula.getRootReference();
    collectReferences( lValue, map );
    return map.keySet().toArray( new String[ map.size() ] );
  }

  private static void collectReferences( final LValue lval, final LinkedHashMap<String, Boolean> map ) {
    if ( lval instanceof Term ) {
      final Term t = (Term) lval;
      final LValue[] childValues = t.getChildValues();
      for ( int i = 0; i < childValues.length; i++ ) {
        final LValue childValue = childValues[ i ];
        collectReferences( childValue, map );
      }
    } else if ( lval instanceof ContextLookup ) {
      final ContextLookup cl = (ContextLookup) lval;
      map.put( cl.getName(), Boolean.TRUE );
    } else if ( lval instanceof FormulaFunction ){
      FormulaFunction ff = FormulaFunction.class.cast(lval);
      LValue[] lvals = ff.getChildValues();
      for (int i = 0; i < lvals.length; i++) {
        collectReferences( lvals[i], map );
      }
    }
  }

  public static String extractFormula( final String formula ) {
    String[] strings = extractFormulaContext( formula );
    return strings[ 1 ];
  }

  public static String extractStaticTextFromFormula( final String formula ) {
    if ( formula == null ) {
      return null;
    }
    final String formulaFragment = extractFormula( formula );
    return extractStaticTextFromFormulaFragment( formulaFragment );
  }

  public static String extractStaticTextFromFormulaFragment( final String formula ) {
    if ( formula == null ) {
      return null;
    }
    try {
      final FormulaParser parser = new FormulaParser();
      final LValue lValue = parser.parse( formula );
      if ( lValue.isConstant() ) {
        if ( lValue instanceof StaticValue ) {
          final StaticValue staticValue = (StaticValue) lValue;
          final Object o = staticValue.getValue();
          if ( o == null ) {
            return null; // NON-NLS
          }
          return String.valueOf( o );
        }
      }
      return null; // NON-NLS
    } catch ( Exception e ) {
      return null; // NON-NLS
    }
  }

  public static boolean isValidFormulaFragment( final String formula ) {
    try {
      final FormulaParser parser = new FormulaParser();
      final LValue lValue = parser.parse( formula );
      return true;
    } catch ( Exception e ) {
      return false;
    }
  }

  public static String createCellUITextFromFormula( final String formula ) {
    return createCellUITextFromFormula( formula, new DefaultFormulaContext() );
  }

  public static String createCellUITextFromFormula( final String formula, final FormulaContext context ) {
    try {
      final FormulaParser parser = new FormulaParser();
      final LValue lValue = parser.parse( formula );
      lValue.initialize( context );

      if ( lValue.isConstant() ) {
        if ( lValue instanceof StaticValue ) {
          final StaticValue staticValue = (StaticValue) lValue;
          final Object o = staticValue.getValue();
          if ( o == null ) {
            return "=NA()"; // NON-NLS
          }
          return String.valueOf( o );
        }
      } else if ( lValue instanceof ContextLookup ) {
        ContextLookup l = (ContextLookup) lValue;
        return l.toString();
      }

      final String cellText = formula;
      return cellText.startsWith( "=" ) ? cellText : "=" + cellText;
    } catch ( Exception e ) {
      e.printStackTrace();
      return formula;
    }
  }

  public static String createEditorTextFromFormula( final String formula,
                                                    final FormulaContext formulaContext ) {
    ArgumentNullException.validate( "fomulaContext", formulaContext );

    try {
      final FormulaParser parser = new FormulaParser();
      final LValue lValue = parser.parse( formula );
      lValue.initialize( formulaContext );
      if ( lValue.isConstant() ) {
        if ( lValue instanceof StaticValue ) {
          final StaticValue staticValue = (StaticValue) lValue;
          final Object o = staticValue.getValue();
          if ( o == null ) {
            return "=NA()";
          }
          if ( o instanceof Number ) {
            return String.valueOf( o );
          }
          return '\'' + String.valueOf( o );
        }
      }

      final String cellText = formula;
      return cellText.startsWith( "=" ) ? cellText : "=" + cellText;
    } catch ( Exception e ) {
      return "'" + formula;
    }
  }

  /**
   * Creates a formula fragment from the given UI text. Within the reporting engine, the fragment must be prefixed with
   * a formula-context selector.
   * <p/>
   * The input follows the Excel/OpenOffice Calc rules: A leading equals indicates a formula, a leading apostrophe
   * indicates a text. Otherwise, if the input is parsable as number it will be formed into a formula that returns that
   * static number, and everything else will be treated as text.
   * <p/>
   * Note that there is no syntax check for formula input - if the user writes garbage after the formula selector, this
   * garbage will still be treated as formula.
   *
   * @param formula the input from the user.
   * @return the normalized formula.
   */
  public static String createFormulaFromUIText( final String formula ) {
    if ( formula.startsWith( "=" ) ) {
      // it is a formula ...
      return formula.substring( 1 );
    }
    try {
      final BigDecimal bd = new BigDecimal( formula.trim() );
      return formula.trim();
    } catch ( NumberFormatException nfe ) {
      // ignore ..
    }

    if ( formula.startsWith( "\'" ) ) {
      // is a explicit text ..
      return FormulaUtil.quoteString( formula.substring( 1 ) );
    }

    return FormulaUtil.quoteString( formula );
  }

  public static String[] extractFormulaContext( String formula ) {
    String formulaNamespace;
    String formulaExpression;
    if ( formula == null ) {
      formulaNamespace = null;
      formulaExpression = null;
    } else {
      if ( formula.endsWith( ";" ) ) {
        DebugLog.log( "A formula with a trailing semicolon is not valid. Auto-correcting the formula." );
        formula = formula.substring( 0, formula.length() - 1 );
      }

      Pattern pattern = Pattern.compile( "^((\\w+):|=)(.*)" );
      Matcher matcher = pattern.matcher( formula );
      if ( matcher.matches() ) {
        formulaNamespace = matcher.group( 2 );
        if ( formulaNamespace == null ) {
          formulaNamespace = "report";
        }
        formulaExpression = matcher.group( 3 );
      } else {
        formulaNamespace = null;
        formulaExpression = null;
      }
    }
    return new String[] { formulaNamespace, formulaExpression };
  }
}
