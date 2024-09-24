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

package org.pentaho.reporting.libraries.formula;

import junit.framework.TestCase;
import org.pentaho.reporting.libraries.formula.lvalues.Term;
import org.pentaho.reporting.libraries.formula.parser.ParseException;

public class FormulaParsingTest extends TestCase {
  public FormulaParsingTest() {
    super();
  }

  public FormulaParsingTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    LibFormulaBoot.getInstance().start();
  }

  public void testEmptyArray() throws ParseException, EvaluationException {
    final Formula formula = new Formula( "{}" );
    formula.initialize( new DefaultFormulaContext() );
    formula.getRootReference();

  }

  public void testEmptyArray2() throws ParseException, EvaluationException {
    final Formula formula = new Formula( "COUNT({})" );
    formula.initialize( new DefaultFormulaContext() );
    formula.getRootReference();

  }

  public void testTermOperands() throws ParseException, EvaluationException {
    final Formula formula = new Formula( "\"a\" & \"b\" & \"c\"" );
    formula.initialize( new DefaultFormulaContext() );
    Term term = (Term) formula.getRootReference();
    term.getOperands();
  }

  public void testParseWithLineBreaks() throws ParseException, EvaluationException {
    final Formula formula = new Formula( "\"aaa\" \n&\n \"bb\" & \"\n\" & \"\"" );
    formula.initialize( new DefaultFormulaContext() );
    final Object o = formula.evaluate();
    assertEquals( "Formula value", "aaabb\n", o );
  }

  public void testParse() throws ParseException, EvaluationException {
    final Formula formula = new Formula( "MID(UPPER([name] & \n\r" +
      "   \" \" \n\r" +
      "   & [firstname]);5;10)" );
    final DefaultFormulaContext context = new DefaultFormulaContext();
    context.defineReference( "name", "name" );
    context.defineReference( "firstname", "firstname" );
    formula.initialize( context );
    final Object o = formula.evaluate();
    assertEquals( "Formula value", " FIRSTNAME", o );
  }

  public void testEscapes() throws Exception {
    final Formula formula = new Formula( "T(\"\\\") = \"A\"" );
    final DefaultFormulaContext context = new DefaultFormulaContext();
    context.defineReference( "path", "x" );
    formula.initialize( context );
    final Object o = formula.evaluate();
    assertEquals( "Formula value", Boolean.FALSE, o );
  }

  public void testQuotedReference() throws Exception {
    final Formula formula = new Formula( "T([\"\\\\\"]) = \"A\"" );
    final DefaultFormulaContext context = new DefaultFormulaContext();
    context.defineReference( "\\\\", "Dummy" );
    formula.initialize( context );
    final Object o = formula.evaluate();
    assertEquals( "Formula value", Boolean.FALSE, o );
  }

  public void testQuotedReference2() throws Exception {
    final Formula formula = new Formula( "T([\"[x]\"]) = \"x\"" );
    final DefaultFormulaContext context = new DefaultFormulaContext();
    context.defineReference( "[x]", "x" );
    formula.initialize( context );
    final Object o = formula.evaluate();
    assertEquals( "Formula value", Boolean.TRUE, o );
  }

  public void testParseFailure() throws Exception {
    try {
      final Formula formula = new Formula( "T([year4))" );
      fail();
    } catch ( ParseException pe ) {
    }
  }

  public void testParseLogicalCondition() throws Exception {
    new Formula(
      "AND ( [TABLEA.COLA] = 23; [TABLEA.COLB] = 2012; ( OR ( [TABLEB.COLA] = 20932598; [TABLEA.COLC] = 20932598 ) ) )" );
  }

}
