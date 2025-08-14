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


package org.pentaho.reporting.designer.core.editor.parameters;

import java.awt.GraphicsEnvironment;
import java.lang.reflect.Method;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import static org.junit.Assert.*;
import static org.junit.Assume.assumeFalse;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.designer.core.util.FormulaEditorPanel;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;

/**
 * Created by dima.prokopenko@gmail.com on 10/26/2016.
 */
public class ParameterDialogTest {

  private ReportDesignerContext context;
  private ParameterDefinitionEntry parameter;
  private ParameterType parameterType;
  private ParameterDialog dialog;
  private ParameterContext parameterContext;

  @Before
  public void before() {
    assumeFalse( GraphicsEnvironment.isHeadless() );

    context = mock( ReportDesignerContext.class );
    parameter = mock( ParameterDefinitionEntry.class );
    when( parameter.getValueType() ).thenReturn( String.class );
    parameterType = mock( ParameterType.class );

    parameterContext = mock( ParameterContext.class );

    dialog = new ParameterDialog( context );

    dialog.dataFormatField = mock( JTextField.class );
    dialog.dataFormatFormula = mock( FormulaEditorPanel.class );
    dialog.labelTextField = mock( JTextField.class );
    dialog.labelFormula = mock( FormulaEditorPanel.class );
  }

  @Test
  public void testCreateDialogJustLabel() {
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.LABEL
    ), any() ) ).thenReturn( "val" );
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.LABEL_FORMULA
    ), any() ) ).thenReturn( null );

    dialog.updateFromParameter( parameter );

    verify( dialog.labelTextField, times( 1 ) ).setText( eq( "val" ) );
    verify( dialog.labelFormula, times( 1 ) ).setFormula( null );
  }

  @Test
  public void testCreateDialogLabelAndFormula() {
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.LABEL
    ), any() ) ).thenReturn( "val" );
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.LABEL_FORMULA
    ), any() ) ).thenReturn( "=TRUE()" );

    dialog.updateFromParameter( parameter );

    verify( dialog.labelTextField, times( 1 ) ).setText( eq( "val" ) );
    verify( dialog.labelFormula, times( 1 ) ).setFormula( "=TRUE()" );
  }

  @Test
  public void testCreateDialogLabelAndFormulaAreSame() {
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.LABEL
    ), any() ) ).thenReturn( "=TRUE()" );
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.LABEL_FORMULA
    ), any() ) ).thenReturn( "=TRUE()" );

    dialog.updateFromParameter( parameter );

    verify( dialog.labelTextField, times( 1 ) ).setText( eq( "" ) );
    verify( dialog.labelFormula, times( 1 ) ).setFormula( "=TRUE()" );
  }

  @Test
  public void testCreateDialogDataFormat() {
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.DATA_FORMAT
    ), any() ) ).thenReturn( "yyyy" );
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.DATA_FORMAT_FORMULA
    ), any() ) ).thenReturn( null );

    dialog.updateFromParameter( parameter );

    verify( dialog.dataFormatField, times( 1 ) ).setText( eq( "yyyy" ) );
    verify( dialog.dataFormatFormula, times( 1 ) ).setFormula( null );
  }

  @Test
  public void testCreateDialogDataFormatAndFormula() {
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.DATA_FORMAT
    ), any() ) ).thenReturn( "yyyy" );
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.DATA_FORMAT_FORMULA
    ), any() ) ).thenReturn( "=ERROR()" );

    dialog.updateFromParameter( parameter );

    verify( dialog.dataFormatField, times( 1 ) ).setText( eq( "yyyy" ) );
    verify( dialog.dataFormatFormula, times( 1 ) ).setFormula( "=ERROR()" );
  }

  @Test
  public void testCreateDialogDataFormatAndFormulaSame() {
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.DATA_FORMAT
    ), any() ) ).thenReturn( "=ERROR()" );
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.DATA_FORMAT_FORMULA
    ), any() ) ).thenReturn( "=ERROR()" );

    dialog.updateFromParameter( parameter );

    verify( dialog.dataFormatField, times( 1 ) ).setText( eq( "" ) );
    verify( dialog.dataFormatFormula, times( 1 ) ).setFormula( "=ERROR()" );
  }

  @Test
  public void testSaveParameterLabel() {
    when( dialog.labelFormula.getFormula() ).thenReturn( null );
    when( dialog.dataFormatFormula.getFormula() ).thenReturn( null );

    ParameterDefinitionEntry entry = dialog.createQuerylessParameter( "paramName", "paramLabel",
      new Object(), "yyy", false, parameterType, false );

    final String actualDataFormat = entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.DATA_FORMAT, parameterContext );
    final String actualLabel = entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.LABEL, parameterContext );

    assertEquals( "paramLabel", actualLabel );
    assertEquals( "yyy", actualDataFormat );

    final String labelFormula = entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.LABEL_FORMULA, parameterContext );
    final String dataFormatFormula = entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.DATA_FORMAT_FORMULA, parameterContext );

    assertEquals( null, labelFormula );
    assertEquals( null, dataFormatFormula );
  }

  @Test
  public void testSaveParameterLabelFormulaRepalce() {
    when( dialog.labelFormula.getFormula() ).thenReturn( "=FOR1" );
    when( dialog.dataFormatFormula.getFormula() ).thenReturn( "=FOR2" );

    ParameterDefinitionEntry entry = dialog.createQuerylessParameter( "paramName", "",
      new Object(), "", false, parameterType, false );

    final String actualDataFormat = entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.DATA_FORMAT, parameterContext );
    final String actualLabel = entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.LABEL, parameterContext );

    assertEquals( "=FOR1", actualLabel );
    assertEquals( "=FOR2", actualDataFormat );

    final String labelFormula = entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.LABEL_FORMULA, parameterContext );
    final String dataFormatFormula = entry.getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.DATA_FORMAT_FORMULA, parameterContext );

    assertEquals( "=FOR1", labelFormula );
    assertEquals( "=FOR2", dataFormatFormula );
  }


  /**
   * Test the usage of the hidden checkbox and that it's checked
   */
  @Test
  public void testCreateDialogHidden() {
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.HIDDEN
    ), any() ) ).thenReturn( "true" );
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.HIDDEN_FORMULA
    ), any() ) ).thenReturn( null );

    dialog.updateFromParameter( parameter );

    verify( dialog.hiddenCheckBox, times( 1 ) ).setSelected( eq( true ) );
    verify( dialog.hiddenFormula, times( 1 ) ).setFormula( null );

  }

  /*
    Test the usage of the hidden checkbox (checked) and the hidden formula
   */
  @Test
  public void testCreateDialogHiddenAndFormula() {
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.HIDDEN
    ), any() ) ).thenReturn( "true" );
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.HIDDEN_FORMULA
    ), any() ) ).thenReturn( "=DUMMY()" );

    dialog.updateFromParameter( parameter );

    verify( dialog.hiddenCheckBox, times( 1 ) ).setSelected( eq( true ) );
    verify( dialog.hiddenFormula, times( 1 ) ).setFormula( "=DUMMY()" );
  }

  /**
   * Test the usage of the hidden checkbox (incorrect value = false) and the hidden formula
   */
  @Test
  public void testCreateDialogHiddenAndFormulaSame() {
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.HIDDEN
    ), any() ) ).thenReturn( "=DUMMY()" );
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.HIDDEN_FORMULA
    ), any() ) ).thenReturn( "=DUMMY()" );

    dialog.updateFromParameter( parameter );

    verify( dialog.hiddenCheckBox, times( 1 ) ).setSelected( eq( false ) );
    verify( dialog.hiddenFormula, times( 1 ) ).setFormula( "=DUMMY()" );
  }

  /**
   * Tests the usage of false value in the attribute value
   */
  @Test
  public void testCreateDialogHiddenFalse() {
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.HIDDEN
    ), any() ) ).thenReturn( "false" );
    dialog.updateFromParameter( parameter );
    verify( dialog.hiddenCheckBox, times( 1 ) ).setSelected( eq( false ) );
  }

  /**
   * Tests the usage of any non "true" value in the attribute value
   */
  @Test
  public void testCreateDialogHiddenAnyNonTrueValue() {
    when( parameter.getParameterAttribute( anyString(), eq(
      ParameterAttributeNames.Core.HIDDEN
    ), any() ) ).thenReturn( "any value" );
    dialog.updateFromParameter( parameter );
    verify( dialog.hiddenCheckBox, times( 1 ) ).setSelected( eq( false ) );
  }

  @Test
  public void testDisplayTimeSelectorCheckBox() {
    // Create the check box
    JCheckBox displayTimeSelectorCheckBox = new JCheckBox(Messages.getString("ParameterDialog.DisplayTimeSelector"));

    // Test initialization
    assertNotNull(displayTimeSelectorCheckBox);
    assertEquals(Messages.getString("ParameterDialog.DisplayTimeSelector"), displayTimeSelectorCheckBox.getText());

    // Test border properties
    Border border = displayTimeSelectorCheckBox.getBorder();
    assertTrue(border instanceof EmptyBorder);
    EmptyBorder emptyBorder = (EmptyBorder) border;

    // Verify border insets (top=3, left=0, bottom=0, right=0)
    assertEquals(3, emptyBorder.getBorderInsets().top);
    assertEquals(0, emptyBorder.getBorderInsets().left);
    assertEquals(0, emptyBorder.getBorderInsets().bottom);
    assertEquals(0, emptyBorder.getBorderInsets().right);
  }

  @Test
  public void testTimeSelectorApplicabilityForDateType() throws Exception {
      Method createParameterResultMethod = ParameterDialog.class.getDeclaredMethod("createParameterResult");
      createParameterResultMethod.setAccessible(true);

      when(parameter.getValueType()).thenReturn(Date.class);
      when(parameter.getParameterAttribute(
              ParameterAttributeNames.Core.NAMESPACE,
              ParameterAttributeNames.Core.DISPLAY_TIME_SELECTOR,
              parameterContext))
              .thenReturn("true");

      dialog.updateFromParameter(parameter);

      ParameterDefinitionEntry result =
              (ParameterDefinitionEntry) createParameterResultMethod.invoke(dialog);

      assertEquals("true", result.getParameterAttribute(
              ParameterAttributeNames.Core.NAMESPACE,
              ParameterAttributeNames.Core.DISPLAY_TIME_SELECTOR,
              parameterContext));
  }

  @Test
  public void testTimeSelectorApplicabilityForNonDateType() throws Exception {
    Method createParameterResultMethod = ParameterDialog.class.getDeclaredMethod("createParameterResult");
    createParameterResultMethod.setAccessible(true);

    when(parameter.getValueType()).thenReturn(String.class);

    dialog.updateFromParameter(parameter);

    ParameterDefinitionEntry result =
            (ParameterDefinitionEntry) createParameterResultMethod.invoke(dialog);

    assertEquals("false", result.getParameterAttribute(
            ParameterAttributeNames.Core.NAMESPACE,
            ParameterAttributeNames.Core.DISPLAY_TIME_SELECTOR,
            parameterContext));
  }

  @Test
  public void testDefaultTimeSelectorState() throws Exception {
    Method createParameterResultMethod = ParameterDialog.class.getDeclaredMethod("createParameterResult");
    createParameterResultMethod.setAccessible(true);

    dialog.updateFromParameter(null);

    ParameterDefinitionEntry result =
            (ParameterDefinitionEntry) createParameterResultMethod.invoke(dialog);

    assertEquals("false", result.getParameterAttribute(
            ParameterAttributeNames.Core.NAMESPACE,
            ParameterAttributeNames.Core.DISPLAY_TIME_SELECTOR,
            parameterContext));
  }

}
