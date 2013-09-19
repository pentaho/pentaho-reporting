/*
 *
 *  * This program is free software; you can redistribute it and/or modify it under the
 *  * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  * Foundation.
 *  *
 *  * You should have received a copy of the GNU Lesser General Public License along with this
 *  * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  * or from the Free Software Foundation, Inc.,
 *  * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *  *
 *  * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  * See the GNU Lesser General Public License for more details.
 *  *
 *  * Copyright (c) 2006 - 2009 Pentaho Corporation..  All rights reserved.
 *
 */

package org.pentaho.openformula.ui;

import junit.framework.TestCase;

public class FormulaEditorPanelTest extends TestCase
{
  public FormulaEditorPanelTest()
  {
  }

  protected void setUp() throws Exception
  {
    LibFormulaEditorBoot.getInstance().start();
  }

  protected void tearDown() throws java.lang.Exception
  {
  }


  // TODO: This test case fails randomly
  public void testNestedFunctionEditing()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.setFormulaText("=COUNT()");

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "SUM(1)");
    assertEquals("=COUNT(SUM(1))", panel.getFormulaText());

    activeEditor.fireParameterUpdate(1, "2");
    assertEquals("=COUNT(SUM(1);2)", panel.getFormulaText());

    activeEditor.fireParameterUpdate(2, "3");
    assertEquals("=COUNT(SUM(1);2;3)", panel.getFormulaText());
  }

  public void testCountWithFunctionInFirstParameterField()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=COUNT()");

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "SUM(1)");
    activeEditor.fireParameterUpdate(1, "2");
    activeEditor.fireParameterUpdate(2, "3");

    assertEquals("=COUNT(SUM(1);2;3)", panel.getFormulaText());
  }

  public void testCountWithFunctionInSecondParameterField()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=COUNT()");

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "1");
    activeEditor.fireParameterUpdate(1, "SUM(2)");
    activeEditor.fireParameterUpdate(2, "3");

    assertEquals("=COUNT(1;SUM(2);3)", panel.getFormulaText());
  }

  public void testCountWithFunctionInSecondWithMultipleEmbeddedParameterField()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=COUNT()");

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "1");
    activeEditor.fireParameterUpdate(1, "SUM(ABS(-1);ABS(2))");
    activeEditor.fireParameterUpdate(2, "3");

    assertEquals("=COUNT(1;SUM(ABS(-1);ABS(2));3)", panel.getFormulaText());
  }

  // Validates PRD-4526
  public void testCountFunctionWithThreeParameters()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=COUNT()");

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "1");
    activeEditor.fireParameterUpdate(1, "2");
    activeEditor.fireParameterUpdate(2, "3");

    assertEquals("=COUNT(1;2;3)", panel.getFormulaText());
  }


  public void testValidateAddingAConstantToSumFunction()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=(1 + SUM())");
    panel.getEditorModel().setCaretPosition(7);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();
    activeEditor.addParameterUpdateListener(panel.getParameterUpdateHandler());

    activeEditor.fireParameterUpdate(0, "1");
    activeEditor.fireParameterUpdate(1, "2");

    assertEquals("=(1 + SUM(1;2))", panel.getFormulaText());
    // exception thrown here ..
  }

  public void testAddAdditionalParameterToSUMFunction()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=SUM(1;2)");
    panel.getEditorModel().setCaretPosition(2);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(2, "3");

    assertEquals("=SUM(1;2;3)", panel.getFormulaText());
  }

  public void testAddMultipleAdditionalParameterToSUMFunction()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=SUM(1)");
    panel.getEditorModel().setCaretPosition(2);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "SUM(1;2;3)");

    assertEquals("=SUM(SUM(1;2;3))", panel.getFormulaText());
  }

  public void testReplaceSingleParameterEmbeddedSUMFunction()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=SUM(1)");
    panel.getEditorModel().setCaretPosition(2);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "SUM(2)");

    assertEquals("=SUM(SUM(2))", panel.getFormulaText());
  }

  public void testReplaceSingleParameterEmbeddedCOUNTFunction()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=SUM(1)");
    panel.getEditorModel().setCaretPosition(2);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "COUNT(2)");

    assertEquals("=SUM(COUNT(2))", panel.getFormulaText());
  }

  public void testUpdateSingleParameterForSUMFunction()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=SUM(1)");
    panel.getEditorModel().setCaretPosition(2);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "2");

    assertEquals("=SUM(2)", panel.getFormulaText());
  }

  public void testReplaceSUMFirstParameterWithCOUNTFunction()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=SUM(1;2)");
    panel.getEditorModel().setCaretPosition(2);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "COUNT(3;4)");

    assertEquals("=SUM(COUNT(3;4);2)", panel.getFormulaText());
  }


  public void testEmbeddedSingleParameterToSUMFunction()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=SUM(1)");
    panel.getEditorModel().setCaretPosition(2);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "SUM(SUM(1))");

    assertEquals("=SUM(SUM(SUM(1)))", panel.getFormulaText());
  }

  public void testEmbeddedFunctionWithMultipleParametersToSUMFunction()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=SUM(1)");
    panel.getEditorModel().setCaretPosition(2);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "SUM(SUM(1;2;3))");

    assertEquals("=SUM(SUM(SUM(1;2;3)))", panel.getFormulaText());
  }

  public void testEmbeddedMultipleFunctionWithMultipleParametersToSUMFunction()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=SUM(1)");
    panel.getEditorModel().setCaretPosition(2);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "SUM(SUM(1;2;3))");
    activeEditor.fireParameterUpdate(1, "COUNT(1;2;3)");
    activeEditor.fireParameterUpdate(2, "COUNT(4;5;6)");

    assertEquals("=SUM(SUM(SUM(1;2;3));COUNT(1;2;3);COUNT(4;5;6))", panel.getFormulaText());
  }

  public void testEmbeddedMultipleFunctionWithLastParameterSameAsFirst()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=SUM(1)");
    panel.getEditorModel().setCaretPosition(2);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "SUM(SUM(1;2;3))");
    assertEquals("=SUM(SUM(SUM(1;2;3)))", panel.getFormulaText());

    activeEditor.fireParameterUpdate(1, "COUNT(1;2;3)");
    assertEquals("=SUM(SUM(SUM(1;2;3));COUNT(1;2;3))", panel.getFormulaText());

    activeEditor.fireParameterUpdate(2, "SUM(4;5;6)");
    assertEquals("=SUM(SUM(SUM(1;2;3));COUNT(1;2;3);SUM(4;5;6))", panel.getFormulaText());
  }



  public void testTwoSeparateFunctions()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=SUM(1;2) + COUNT(1;2)");
    panel.getEditorModel().setCaretPosition(2);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(2, "3");

    assertEquals("=SUM(1;2;3) + COUNT(1;2)", panel.getFormulaText());

    panel.getEditorModel().setCaretPosition(13);
    activeEditor.fireParameterUpdate(2, "3");
    activeEditor.fireParameterUpdate(3, "4");
    assertEquals("=SUM(1;2;3) + COUNT(1;2;3;4)", panel.getFormulaText());
  }

  // Validates PRD-4503
  public void testReplacingDummyParametersInIFFunction()
  {
    final String ifFormula = "=IF(Logical;Any;Any)";

    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText(ifFormula);

    final MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    final DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(1, "aa");    // Then clause
    assertEquals("=IF(Logical;aa;Any)", panel.getFormulaText());

    activeEditor.fireParameterUpdate(2, "");    // Other clause
    assertEquals("=IF(Logical;aa;)", panel.getFormulaText());

    activeEditor.fireParameterUpdate(2, "bb");    // Other clause
    assertEquals("=IF(Logical;aa;bb)", panel.getFormulaText());
  }

  /**
   * <a href="http://jira.pentaho.com/browse/PRD-4552">PRD-4552:
   * Entering embedded function that is same as main function with dummy variables causes issues</a>
   **/
  public void testReplaceDummyIFParamWithIF() {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=IF(Logical;Any;Any)");

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(1, "IF(1;2;3)");

    assertEquals("=IF(Logical;IF(1;2;3);Any)", panel.getFormulaText());
  }

  public void testDrilldownFormulaChangingBrowseLocation()
  {
    final String drillDownFormulaBase = "DRILLDOWN(\"local-sugar\"; NA(); {\"::pentaho-path\";";
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=" + drillDownFormulaBase + " \"/public/bi-developers/steel-wheels-old/reports/Top N Analysis.prpt\"})");

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();
    activeEditor.addParameterUpdateListener(panel.getParameterUpdateHandler());

    final String newDrillDownFormula = drillDownFormulaBase + " \"/public/bi-developers/steel-wheels-old/reports/Buyer Product Analysis.prpt\"})";
    FormulaEditorPanel.ParameterUpdateHandler handler = panel.getParameterUpdateHandler();
    ParameterUpdateEvent event = new ParameterUpdateEvent(activeEditor, -1, newDrillDownFormula, false);
    handler.parameterUpdated(event);

    assertEquals("=" + newDrillDownFormula, panel.getFormulaText());
  }


  // Validates PRD-4521
  public void testValidateFieldSelector()
  {
    final String fieldNoFormula = "[PRODUCTNAME]";

    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.insertText(fieldNoFormula);
    assertEquals("=[PRODUCTNAME]", panel.getFormulaText());
  }
}
