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


package org.pentaho.openformula.ui;

import junit.framework.TestCase;
import org.pentaho.openformula.ui.model2.FormulaClosingParenthesisElement;
import org.pentaho.openformula.ui.model2.FormulaDocument;
import org.pentaho.openformula.ui.model2.FormulaElement;
import org.pentaho.openformula.ui.model2.FormulaOpenParenthesisElement;
import org.pentaho.openformula.ui.model2.FormulaOperatorElement;
import org.pentaho.openformula.ui.model2.FormulaParser;
import org.pentaho.openformula.ui.model2.FormulaRootElement;
import org.pentaho.openformula.ui.model2.FormulaSemicolonElement;
import org.pentaho.openformula.ui.model2.FormulaTextElement;

import javax.swing.text.BadLocationException;

public class FormulaDocumentTest extends TestCase {
  public FormulaDocumentTest() {
  }

  public FormulaDocumentTest( final String s ) {
    super( s );
  }

  public void testParseFunction() throws BadLocationException {
    FormulaDocument doc = new FormulaDocument();
    final String str = "=IF(IF([a];[b];\"C\");[c]; [d]) ";
    doc.insertString( 0, str, null );
    final FormulaRootElement element = doc.getRootElement();
    assertEquals( "Length", str.length(), doc.getLength() );
    assertEquals( "Number of elements: ", 17, element.getElementCount() );
  }

  public void testParseFunctionBad() throws BadLocationException {
    FormulaDocument doc = new FormulaDocument();
    final String str = "=IF(IF([a];[b];\"C\"));[c]; [d]) ";
    doc.insertString( 0, str, null );
    final FormulaRootElement element = doc.getRootElement();
    assertEquals( "Length", str.length(), doc.getLength() );
    assertEquals( "Number of elements: ", 18, element.getElementCount() );
  }

  public void testParseFunctionBad2() throws BadLocationException {
    FormulaDocument doc = new FormulaDocument();
    final String str = "=IF(([a];[b];\"C\"));[c]; [d]) ";
    doc.insertString( 0, str, null );
    final FormulaRootElement element = doc.getRootElement();
    assertEquals( "Length", str.length(), doc.getLength() );
    assertEquals( "Number of elements: ", 17, element.getElementCount() );
  }

  public void testParseTextOpenParenthesisCorrectlyPRD5009()  {
    FormulaDocument doc = new FormulaDocument();
    final String str = "=IF((1=3);1;2)";
    FormulaElement[] result = FormulaParser.parseText( doc, str );
    assertEquals( "class of =", result[0].getClass(), FormulaOperatorElement.class );
    assertEquals( "class of IF", result[1].getClass(), FormulaTextElement.class );
    assertEquals( "class of (", result[2].getClass(), FormulaOpenParenthesisElement.class );
    assertEquals( "class of (", result[3].getClass(), FormulaOpenParenthesisElement.class );
    assertEquals( "class of 1", result[4].getClass(), FormulaTextElement.class );
    assertEquals( "class of =", result[5].getClass(), FormulaOperatorElement.class );
    assertEquals( "class of 3", result[6].getClass(), FormulaTextElement.class );
    assertEquals( "class of )", result[7].getClass(), FormulaClosingParenthesisElement.class );
    assertEquals( "class of ;", result[8].getClass(), FormulaSemicolonElement.class );
    assertEquals( "class of 1", result[9].getClass(), FormulaTextElement.class );
    assertEquals( "class of ;", result[10].getClass(), FormulaSemicolonElement.class );
    assertEquals( "class of 2", result[11].getClass(), FormulaTextElement.class );
    assertEquals( "class of )", result[12].getClass(), FormulaClosingParenthesisElement.class );
  }
}
