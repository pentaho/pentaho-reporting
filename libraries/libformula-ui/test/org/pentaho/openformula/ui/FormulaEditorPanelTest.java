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


  public void testNestedFunctionEditing()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.setFormulaText("=COUNT()");

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "SUM(1)");
    activeEditor.fireParameterUpdate(1, "2");
    activeEditor.fireParameterUpdate(2, "3");

    // you get =COUNT(SUM(1;2;3) instead.
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


  // TODO: Fix this test case.
  public void testValidateAddingAConstantToSumFunction()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=(1 + SUM())");
    panel.getEditorModel().setCaretPosition(7);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "SUM(1;2)");

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

    activeEditor.fireParameterUpdate(0, "SUM(1;2;3)");

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

    assertEquals("=SUM(1;2;3)", panel.getFormulaText());
  }

  public void testReplaceSingleParameterToSUMFunction()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=SUM(1)");
    panel.getEditorModel().setCaretPosition(2);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "SUM(2)");

    assertEquals("=SUM(2)", panel.getFormulaText());
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

    assertEquals("=SUM(SUM(1))", panel.getFormulaText());
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

    assertEquals("=SUM(SUM(1;2;3))", panel.getFormulaText());
  }

  public void testEmbeddedMultipleFunctionWithMultipleParametersToSUMFunction()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=SUM(1)");
    panel.getEditorModel().setCaretPosition(2);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "SUM(SUM(1;2;3);COUNT(1;2;3);SUM(4;5;6)");

    assertEquals("=SUM(SUM(1;2;3);COUNT(1;2;3);SUM(4;5;6)", panel.getFormulaText());
  }



  public void testTwoSeparateFunctions()
  {
    FormulaEditorPanel panel = new FormulaEditorPanel();
    panel.getFunctionTextArea().getDocument().removeDocumentListener(panel.getDocSyncHandler());

    panel.setFormulaText("=SUM(1;2) + COUNT(1;2)");
    panel.getEditorModel().setCaretPosition(2);

    MultiplexFunctionParameterEditor functionParameterEditor = panel.getFunctionParameterEditor();
    DefaultFunctionParameterEditor activeEditor = functionParameterEditor.getDefaultEditor();

    activeEditor.fireParameterUpdate(0, "SUM(1;2;3)");

    assertEquals("=SUM(1;2;3) + COUNT(1;2)", panel.getFormulaText());
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
