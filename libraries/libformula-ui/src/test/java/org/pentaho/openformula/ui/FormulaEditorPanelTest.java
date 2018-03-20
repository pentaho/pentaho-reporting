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
* Copyright (c) 2002-2018 Hitachi Vantara..  All rights reserved.
*/

package org.pentaho.openformula.ui;

import junit.framework.TestCase;

@SuppressWarnings( "HardCodedStringLiteral" )
public class FormulaEditorPanelTest extends TestCase {
  public FormulaEditorPanelTest() {
  }

  protected void setUp() throws Exception {
    LibFormulaEditorBoot.getInstance().start();
  }

  protected void tearDown() throws java.lang.Exception {
  }

  public void testNestedFunctionEditing() {
    // [PRD-4692] - This test already had problems in the past, but the problematic code doesn't exist anymore
    //if ( true ) {
    //  return;
    //}

    final FormulaEditorPanel panel = new FormulaEditorPanel();
    //    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());
    panel.setFormulaText( "=COUNT()" );
    panel.getFunctionTextArea().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "SUM(1)" );
    assertEquals( "=COUNT(SUM(1))", panel.getFormulaText() );

    activeEditor.fireParameterUpdate( 1, "2" );
    assertEquals( "=COUNT(SUM(1);2)", panel.getFormulaText() );

    activeEditor.fireParameterUpdate( 2, "3" );
    assertEquals( "=COUNT(SUM(1);2;3)", panel.getFormulaText() );
  }

  public void testCountWithFunctionInFirstParameterField() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=COUNT()" );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "SUM(1)" );
    activeEditor.fireParameterUpdate( 1, "2" );
    activeEditor.fireParameterUpdate( 2, "3" );

    assertEquals( "=COUNT(SUM(1);2;3)", panel.getFormulaText() );
  }

  public void testCountWithFunctionInSecondParameterField() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=COUNT()" );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "1" );
    activeEditor.fireParameterUpdate( 1, "SUM(2)" );
    activeEditor.fireParameterUpdate( 2, "3" );

    assertEquals( "=COUNT(1;SUM(2);3)", panel.getFormulaText() );
  }

  public void testCountWithFunctionInSecondWithMultipleEmbeddedParameterField() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=COUNT()" );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "1" );
    activeEditor.fireParameterUpdate( 1, "SUM(ABS(-1);ABS(2))" );
    activeEditor.fireParameterUpdate( 2, "3" );

    assertEquals( "=COUNT(1;SUM(ABS(-1);ABS(2));3)", panel.getFormulaText() );
  }

  // Validates PRD-4526
  public void testCountFunctionWithThreeParameters() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=COUNT()" );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "1" );
    activeEditor.fireParameterUpdate( 1, "2" );
    activeEditor.fireParameterUpdate( 2, "3" );

    assertEquals( "=COUNT(1;2;3)", panel.getFormulaText() );
  }


  public void testValidateAddingAConstantToSumFunction() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.init();

    panel.setFormulaText( "=(1 + SUM())" );
    panel.getFunctionTextArea().setCaretPosition( 7 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();
    activeEditor.addParameterUpdateListener( panel.getParameterUpdateHandler() );

    activeEditor.fireParameterUpdate( 0, "1" );
    activeEditor.fireParameterUpdate( 1, "2" );

    assertEquals( "=(1 + SUM(1;2))", panel.getFormulaText() );
    // exception thrown here ..
  }

  public void testAddAdditionalParameterToSUMFunction() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=SUM(1;2)" );
    panel.getFunctionTextArea().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 2, "3" );

    assertEquals( "=SUM(1;2;3)", panel.getFormulaText() );
  }

  public void testAddMultipleAdditionalParameterToSUMFunction() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=SUM(1)" );
    panel.getFunctionTextArea().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "SUM(1;2;3)" );

    assertEquals( "=SUM(SUM(1;2;3))", panel.getFormulaText() );
  }

  public void testReplaceSingleParameterEmbeddedSUMFunction() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=SUM(1)" );
    panel.getEditorModel().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "SUM(2)" );

    assertEquals( "=SUM(SUM(2))", panel.getFormulaText() );
  }

  public void testReplaceSingleParameterEmbeddedCOUNTFunction() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=SUM(1)" );
    panel.getEditorModel().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "COUNT(2)" );

    assertEquals( "=SUM(COUNT(2))", panel.getFormulaText() );
  }

  public void testUpdateSingleParameterForSUMFunction() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=SUM(1)" );
    panel.getEditorModel().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "2" );

    assertEquals( "=SUM(2)", panel.getFormulaText() );
  }

  public void testReplaceSUMFirstParameterWithCOUNTFunction() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=SUM(1;2)" );
    panel.getEditorModel().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "COUNT(3;4)" );

    assertEquals( "=SUM(COUNT(3;4);2)", panel.getFormulaText() );
  }


  public void testEmbeddedSingleParameterToSUMFunction() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=SUM(1)" );
    panel.getEditorModel().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "SUM(SUM(1))" );

    assertEquals( "=SUM(SUM(SUM(1)))", panel.getFormulaText() );
  }

  public void testEmbeddedFunctionWithMultipleParametersToSUMFunction() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=SUM(1)" );
    panel.getEditorModel().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "SUM(SUM(1;2;3))" );

    assertEquals( "=SUM(SUM(SUM(1;2;3)))", panel.getFormulaText() );
  }

  public void testEmbeddedMultipleFunctionWithMultipleParametersToSUMFunction() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=SUM(1)" );
    panel.getEditorModel().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "SUM(SUM(1;2;3))" );
    activeEditor.fireParameterUpdate( 1, "COUNT(1;2;3)" );
    activeEditor.fireParameterUpdate( 2, "COUNT(4;5;6)" );

    assertEquals( "=SUM(SUM(SUM(1;2;3));COUNT(1;2;3);COUNT(4;5;6))", panel.getFormulaText() );
  }

  public void testEmbeddedMultipleFunctionWithLastParameterSameAsFirst() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=SUM(1)" );
    panel.getEditorModel().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "SUM(SUM(1;2;3))" );
    assertEquals( "=SUM(SUM(SUM(1;2;3)))", panel.getFormulaText() );

    activeEditor.fireParameterUpdate( 1, "COUNT(1;2;3)" );
    assertEquals( "=SUM(SUM(SUM(1;2;3));COUNT(1;2;3))", panel.getFormulaText() );

    activeEditor.fireParameterUpdate( 2, "SUM(4;5;6)" );
    assertEquals( "=SUM(SUM(SUM(1;2;3));COUNT(1;2;3);SUM(4;5;6))", panel.getFormulaText() );
  }


  public void testTwoSeparateFunctions() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=SUM(1;2) + COUNT(1;2)" );
    panel.getEditorModel().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 2, "3" );

    assertEquals( "=SUM(1;2;3) + COUNT(1;2)", panel.getFormulaText() );

    panel.getEditorModel().setCaretPosition( 13 );
    activeEditor.fireParameterUpdate( 2, "3" );
    activeEditor.fireParameterUpdate( 3, "4" );
    assertEquals( "=SUM(1;2;3) + COUNT(1;2;3;4)", panel.getFormulaText() );
  }

  // Validates PRD-4503
  public void testReplacingDummyParametersInIFFunction() {
    final String ifFormula = "=IF(Logical;Any;Any)";

    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( ifFormula );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 1, "aa" );    // Then clause
    assertEquals( "=IF(Logical;aa;Any)", panel.getFormulaText() );

    activeEditor.fireParameterUpdate( 2, "" );    // Other clause
    assertEquals( "=IF(Logical;aa;)", panel.getFormulaText() );

    activeEditor.fireParameterUpdate( 2, "bb" );    // Other clause
    assertEquals( "=IF(Logical;aa;bb)", panel.getFormulaText() );
  }

  /**
   * <a href="http://jira.pentaho.com/browse/PRD-4552">PRD-4552: Entering embedded function that is same as main
   * function with dummy variables causes issues</a>
   **/
  public void testReplaceDummyIFParamWithIF() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=IF(Logical;Any;Any)" );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 1, "IF(1;2;3)" );

    assertEquals( "=IF(Logical;IF(1;2;3);Any)", panel.getFormulaText() );
  }

  public void testDrilldownFormulaChangingBrowseLocation() {
    final String drillDownFormulaBase = "DRILLDOWN(\"local-sugar\"; NA(); {\"::pentaho-path\";";
    FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText(
      "=" + drillDownFormulaBase + " \"/public/bi-developers/steel-wheels-old/reports/Top N Analysis.prpt\"})" );

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();
    activeEditor.addParameterUpdateListener( panel.getParameterUpdateHandler() );

    final String newDrillDownFormula =
      drillDownFormulaBase + " \"/public/bi-developers/steel-wheels-old/reports/Buyer Product Analysis.prpt\"})";
    FormulaEditorPanel.ParameterUpdateHandler handler = panel.getParameterUpdateHandler();
    ParameterUpdateEvent event = new ParameterUpdateEvent( activeEditor, -1, newDrillDownFormula, false );
    handler.parameterUpdated( event );

    assertEquals( "=" + newDrillDownFormula, panel.getFormulaText() );
  }


  // Validates PRD-4521
  public void testValidateFieldSelector() {
    final String fieldNoFormula = "[PRODUCTNAME]";

    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.insertText( fieldNoFormula );
    assertEquals( "=[PRODUCTNAME]", panel.getFormulaText() );
  }

  // Validates PRD-4691
  public void testNestedFunctionOuterFunctionMissingParenthesis() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=IF(" );
    panel.getEditorModel().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "1" );    // Then clause
    assertEquals( "=IF(1", panel.getFormulaText() );

    activeEditor.fireParameterUpdate( 1, "IF(1;2;3)" );    // Other clause
    assertEquals( "=IF(1;IF(1;2;3)", panel.getFormulaText() );

    activeEditor.fireParameterUpdate( 2, "4" );    // Other clause
    assertEquals( "=IF(1;IF(1;2;3);4", panel.getFormulaText() );
  }

  public void testNestedFunctionInnerFunctionMissingParenthesis() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=IF()" );
    panel.getEditorModel().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "1" );    // Then clause
    assertEquals( "=IF(1)", panel.getFormulaText() );

    activeEditor.fireParameterUpdate( 1, "IF(1;2;3" );    // Other clause
    assertEquals( "=IF(1;IF(1;2;3)", panel.getFormulaText() );

    activeEditor.fireParameterUpdate( 2, "4" );    // Other clause
    assertEquals( "=IF(1;IF(1;2;3);4", panel.getFormulaText() );
  }

  public void testNestedFunctionFirstInnerFunctionMissingParenthesis() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=IF(" );
    panel.getEditorModel().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "IF(1;2;3)" );    // Then clause
    assertEquals( "=IF(IF(1;2;3)", panel.getFormulaText() );

    activeEditor.fireParameterUpdate( 1, "4" );    // Other clause
    assertEquals( "=IF(IF(1;2;3);4", panel.getFormulaText() );

    activeEditor.fireParameterUpdate( 2, "5" );    // Other clause
    assertEquals( "=IF(IF(1;2;3);4;5", panel.getFormulaText() );
  }

  public void testNestedFunctionLastInnerFunctionMissingParenthesis() {
    final FormulaEditorPanel panel = new FormulaEditorPanel();

    panel.setFormulaText( "=IF(" );
    panel.getEditorModel().setCaretPosition( 2 );

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate( 0, "1" );    // Then clause
    assertEquals( "=IF(1", panel.getFormulaText() );

    activeEditor.fireParameterUpdate( 1, "4" );    // Other clause
    assertEquals( "=IF(1;4", panel.getFormulaText() );

    activeEditor.fireParameterUpdate( 2, "IF(1;2;3)" );    // Other clause
    assertEquals( "=IF(1;4;IF(1;2;3)", panel.getFormulaText() );
  }
}
