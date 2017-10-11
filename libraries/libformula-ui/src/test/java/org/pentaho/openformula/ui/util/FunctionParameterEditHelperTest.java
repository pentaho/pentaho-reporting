/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.openformula.ui.util;

import junit.framework.TestCase;
import org.pentaho.openformula.ui.ParameterUpdateEvent;
import org.pentaho.openformula.ui.model2.FormulaDocument;
import org.pentaho.openformula.ui.model2.FunctionInformation;
import org.pentaho.reporting.libraries.formula.LibFormulaBoot;

@SuppressWarnings( "HardCodedStringLiteral" )
public class FunctionParameterEditHelperTest extends TestCase {
  public FunctionParameterEditHelperTest() {
  }

  protected void setUp() throws Exception {
    LibFormulaBoot.getInstance().start();
  }

  public void testEditExtraParameterEmptyText() {
    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 10, "", true );
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=SUM()" );
    final FunctionInformation fn = doc.getFunctionForPosition( 1 );

    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, doc.getText() );

    assertEquals( "=SUM()", editResult.text );
    assertEquals( 5, editResult.caretPositionAfterEdit );
  }

  public void testEditExtraParameterText() {
    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 4, "1", true );
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=SUM()" );
    final FunctionInformation fn = doc.getFunctionForPosition( 1 );

    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, doc.getText() );

    assertEquals( "=SUM(;;;;1)", editResult.text );
    assertEquals( 10, editResult.caretPositionAfterEdit );
  }

  public void testEditExtraParameterText2() {
    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 0, "1", true );
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=SUM()" );
    final FunctionInformation fn = doc.getFunctionForPosition( 1 );

    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, doc.getText() );

    assertEquals( "=SUM(1)", editResult.text );
    assertEquals( 6, editResult.caretPositionAfterEdit );
  }

  public void testEditExtraParameterText3() {
    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 1, "1", true );
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=SUM(1)" );
    final FunctionInformation fn = doc.getFunctionForPosition( 1 );

    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, doc.getText() );

    assertEquals( "=SUM(1;1)", editResult.text );
    assertEquals( 8, editResult.caretPositionAfterEdit );
  }

  public void testEditExtraParameterText0() {
    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 0, "1", true );
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=SUM(2)" );
    final FunctionInformation fn = doc.getFunctionForPosition( 1 );

    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, doc.getText() );

    assertEquals( "=SUM(1)", editResult.text );
    assertEquals( 6, editResult.caretPositionAfterEdit );
  }

  public void testGlobalReplaceAgainstEmpty() {
    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, -1, "SUM(1; (1 + 1))", true );
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=SUM()" );
    final FunctionInformation fn = doc.getFunctionForPosition( 1 );

    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, doc.getText() );

    assertEquals( "=SUM(1; (1 + 1))", editResult.text );
    assertEquals( 16, editResult.caretPositionAfterEdit );
  }

  public void testParameterReplacement() {
    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 1, "SUM(1; (1 + 1))", false );
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=IF(;;)" );
    final FunctionInformation fn = doc.getFunctionForPosition( 1 );

    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, doc.getText() );

    assertEquals( "=IF(;SUM(1; (1 + 1));)", editResult.text );
    assertEquals( 20, editResult.caretPositionAfterEdit );
  }

  public void testParameterReplacementFirst() {
    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 0, "SUM(1; (1 + 1))", false );
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=IF(;;)" );
    final FunctionInformation fn = doc.getFunctionForPosition( 1 );

    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, doc.getText() );

    assertEquals( "=IF(SUM(1; (1 + 1));;)", editResult.text );
    assertEquals( 19, editResult.caretPositionAfterEdit );
  }

  public void testParameterReplacementLast() {
    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 2, "SUM(1; (1 + 1))", false );
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=IF(;;)" );
    final FunctionInformation fn = doc.getFunctionForPosition( 1 );

    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, doc.getText() );

    assertEquals( "=IF(;;SUM(1; (1 + 1)))", editResult.text );
    assertEquals( 21, editResult.caretPositionAfterEdit );
  }

  public void testNestedParameterReplacement() {
    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 1, "AND(1; (1 + 1))", true );
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=IF(;SUM();)" );
    final FunctionInformation fn = doc.getFunctionForPosition( 9 );
    assertEquals( "SUM", fn.getCanonicalName() );

    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, doc.getText() );

    assertEquals( "=IF(;SUM(;AND(1; (1 + 1)));)", editResult.text );
    assertEquals( 25, editResult.caretPositionAfterEdit );
  }

  public void testNestedParameterReplacement2() {
    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 0, "AND(1; (1 + 1))", true );
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=IF(;SUM();)" );
    final FunctionInformation fn = doc.getFunctionForPosition( 9 );
    assertEquals( "SUM", fn.getCanonicalName() );

    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, doc.getText() );

    assertEquals( "=IF(;SUM(AND(1; (1 + 1)));)", editResult.text );
    assertEquals( 24, editResult.caretPositionAfterEdit );
  }

  public void testCatchAllParameterReplacementFixedPart() {
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=MESSAGE(\"Test\"; 1 ; 2)" );
    final FunctionInformation fn = doc.getFunctionForPosition( 1 );

    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 0, "[Some parameter]", true );
    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, doc.getText() );

    assertEquals( "=MESSAGE([Some parameter]; 1 ; 2)", editResult.text );
    assertEquals( 25, editResult.caretPositionAfterEdit );
  }

  public void testCatchAllParameterReplacementSoftPart() {
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=MESSAGE(\"Test\"; 1 ; 2)" );
    final FunctionInformation fn = doc.getFunctionForPosition( 1 );

    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 2, "[Some parameter]", true );
    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, doc.getText() );

    assertEquals( "=MESSAGE(\"Test\"; 1 ;[Some parameter])", editResult.text );
    assertEquals( 36, editResult.caretPositionAfterEdit );
  }

  public void testCatchAllParameterReplacementSoftPart2() {
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=MESSAGE(\"Test\"; 1 ; 2)" );
    final FunctionInformation fn = doc.getFunctionForPosition( 1 );

    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 3, "[Some parameter]", true );
    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, doc.getText() );

    assertEquals( "=MESSAGE(\"Test\"; 1 ; 2;[Some parameter])", editResult.text );
    assertEquals( 39, editResult.caretPositionAfterEdit );
  }

  public void testOptimizeCatchAll() {
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=MESSAGE(\"Test\";;;;)" );
    final FunctionInformation fn = doc.getFunctionForPosition( 1 );

    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 3, "[Some parameter]", true );
    String text = doc.getText();
    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, text );

    assertEquals( "=MESSAGE(\"Test\";;;[Some parameter])", editResult.text );
    assertEquals( 34, editResult.caretPositionAfterEdit );
  }

  public void testOptimizeCatchAll2() {
    final FormulaDocument doc = new FormulaDocument();
    doc.setText( "=MESSAGE(\"Test\";;;;;;)" );
    final FunctionInformation fn = doc.getFunctionForPosition( 1 );

    final ParameterUpdateEvent event = new ParameterUpdateEvent( this, 3, "[Some parameter]", true );
    String text = doc.getText();
    final FunctionParameterEditHelper.EditResult editResult =
      FunctionParameterEditHelper.buildFormulaText( event, fn, text );

    assertEquals( "=MESSAGE(\"Test\";;;[Some parameter])", editResult.text );
    assertEquals( 34, editResult.caretPositionAfterEdit );
  }
}
