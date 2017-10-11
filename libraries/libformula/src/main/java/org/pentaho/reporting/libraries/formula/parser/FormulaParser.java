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

package org.pentaho.reporting.libraries.formula.parser;

import org.pentaho.reporting.libraries.formula.LibFormulaBoot;
import org.pentaho.reporting.libraries.formula.lvalues.LValue;
import org.pentaho.reporting.libraries.formula.operators.DefaultOperatorFactory;
import org.pentaho.reporting.libraries.formula.operators.OperatorFactory;

import java.io.StringReader;

public class FormulaParser extends GeneratedFormulaParser {
  // This is my parser class
  private OperatorFactory operatorFactory;

  public FormulaParser() {
    super( new StringReader( "" ) );
    operatorFactory = new DefaultOperatorFactory();
    operatorFactory.initalize( LibFormulaBoot.getInstance().getGlobalConfig() );
  }

  protected OperatorFactory getOperatorFactory() {
    return operatorFactory;
  }

  /**
   * @noinspection ThrowableInstanceNeverThrown, ThrowableResultOfMethodCallIgnored
   */
  public ParseException generateParseException() {
    final ParseException parent = super.generateParseException();
    return new FormulaParseException
      ( parent.currentToken, parent.expectedTokenSequences, parent.tokenImage );
  }

  public LValue parse( final String formula ) throws ParseException {
    if ( formula == null ) {
      throw new NullPointerException( "Formula-text given must not be null." );
    }
    try {
      ReInit( new StringReader( formula ) );
      final LValue expression = getExpression();
      if ( token.next != null && token.next.image.length() > 0 ) {
        throw new FormulaParseException( "Extra content: '" + token.next.image + "'" );
      }
      return expression;
    } catch ( ParseException pe ) {
      if ( pe instanceof FormulaParseException ) {
        throw pe;
      }
      throw new FormulaParseException( pe );
    } catch ( TokenMgrError te ) {
      throw new FormulaParseException( te );
    }
  }

  public static void main( final String[] args ) throws ParseException {
    LibFormulaBoot.getInstance().start();

    final FormulaParser formulaParser = new FormulaParser();
    formulaParser.enable_tracing();

    System.out.println( "LValue: " + formulaParser.parse( "[\\n]" ) );
    System.out.println( "LValue: " + formulaParser.parse( "[\"\"]" ) );
    final String s = "[\\\\]";
    System.out.println( s );
    System.out.println( "LValue: " + formulaParser.parse( s ) );
    System.out.println( "LValue: " + formulaParser.parse( "T(\"a\\\\\") = \"a\"" ) );

    System.out.println( "LValue: " + formulaParser.parse( "[\"[OK]\"]" ) );
  }
}
