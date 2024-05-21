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
 *  Copyright (c) 2006 - 2024 Hitachi Vantara.  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.Answers;
import org.mockito.Mockito;
import org.pentaho.reporting.engine.classic.core.parameters.AbstractParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;

import java.awt.Component;

/**
 * Created by dima.prokopenko@gmail.com on 9/20/2016.
 */
public class ParameterReportControllerPaneTest {

  private AbstractParameter entry = new PlainParameter( "junit_parameter" );
  private ParameterReportControllerPane pane = new ParameterReportControllerPane();

  @Before
  public void before() {
    entry.setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.LABEL, "labelValue" );
  }

  @Test
  public void computeSwingLabelTest() {
    entry.setParameterAttribute( ParameterAttributeNames.Swing.NAMESPACE,
      ParameterAttributeNames.Swing.LABEL, "swingName" );
    String label = pane.computeLabel( entry );
    assertNotNull( label );
    assertEquals( "swingName", label );
  }

  @Test
  public void computeLabelTest() {
    String label = pane.computeLabel( entry );
    assertNotNull( label );
    assertEquals( "labelValue", label );
  }

  @Test
  public void computeTranslatedTest() {
    AbstractParameter entry = mock( AbstractParameter.class );

    when( entry.getParameterAttribute(
      anyString(),
      eq( ParameterAttributeNames.Swing.LABEL ),
      any()
    ) ).thenReturn( null );

    when( entry.getTranslatedParameterAttribute(
      anyString(),
      eq( ParameterAttributeNames.Core.LABEL ),
      any()
    ) ).thenReturn( "label" );

    String label = pane.computeLabel( entry );

    assertNotNull( label );
    assertEquals( "label", label );
  }

  /* Test when the formula is invalid and the checkbox is not selected */
  @Test
  public void checkParameterVisibilityInvalidFormulaCheckboxNotSelectedTest(){
    checkParameterVisibility( "ABC", "false", true);
  }


  /* Test when the formula is invalid and the checkbox is selected */
  @Test
  public void checkParameterVisibilityInvalidFormulaCheckboxSelectedTest(){
    checkParameterVisibility( "ABC", "true", false);
  }

  /* Test for the case when the formula is valid (with true value) and the checkbox is selected */
  @Test
  public void checkParameterVisibilityValidTrueFormulaCheckboxSelectedTest(){
    checkParameterVisibility( "true", "true", false);
  }

  /* Test for the case when the formula is valid (with true value) and the checkbox is not selected */
  @Test
  public void checkParameterVisibilityValidTrueFormulaCheckboxNotSelectedTest(){
    checkParameterVisibility( "true", "false", false);
  }

  /* Test for the case when the formula is valid (with false value) and the checkbox is selected */
  @Test
  public void checkParameterVisibilityValidFalseFormulaCheckboxSelectedTest(){
    checkParameterVisibility( "false", "true", true);
  }


  /* Test for the case when the formula is valid (with false value) and the checkbox is not selected */
  @Test
  public void checkParameterVisibilityValidFalseFormulaCheckboxNotSelectedTest(){
    checkParameterVisibility( "false", "false", true);
  }


  /**
   * Check the parameter visibility using mocks
   * @param formulaValue The hidden formula value
   * @param checkboxValue The hidden checkbox value
   * @param expectedVisibiltyValue expected visibility value of the parameter
   */
  private void checkParameterVisibility(String formulaValue, String checkboxValue, boolean expectedVisibiltyValue){
    ParameterDefinitionEntry entry = mock( ParameterDefinitionEntry.class );

    when( entry.getTranslatedParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.HIDDEN ),
      any()
    ) ).thenReturn ( formulaValue );

    when( entry.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.HIDDEN ),
      any()
    ) ).thenReturn( checkboxValue);


    final boolean parameterVisibility = pane.checkParameterVisibility( entry );
    assertTrue( expectedVisibiltyValue == parameterVisibility );

  }

  /**
   * Check that parameter visual components visibility are aligned with the parameter HIDDEN_FORMULA result
   */
  @Test
  public void checkRefreshPaneParametersVisibility() {

    ParameterReportControllerPane mockPane =  Mockito.spy( new ParameterReportControllerPane());

    ParameterDefinitionEntry entry1 = Mockito.spy ( new PlainParameter( "entry1"));
    ParameterDefinitionEntry entry2 = Mockito.spy ( new PlainParameter( "entry2"));
    ( (PlainParameter) entry1 ).setParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE
      , ParameterAttributeNames.Core.HIDDEN_FORMULA
      , "=ENTRY1_FORMULA()"
    );
    ( (PlainParameter) entry2 ).setParameterAttribute(
      ParameterAttributeNames.Core.NAMESPACE
      , ParameterAttributeNames.Core.HIDDEN_FORMULA
      , "=ENTRY2_FORMULA()"
    );
    Mockito.doReturn( true ).when(mockPane).checkParameterVisibility( entry1 );
    Mockito.doReturn( false ).when(mockPane).checkParameterVisibility( entry2 );

    mockPane.parametersVisualComponents.put( entry1.getName(), new ArrayList<Component>(Arrays.asList( new JLabel(), new JLabel() ) ) );
    mockPane.parametersVisualComponents.put( entry2.getName(), new ArrayList<Component>(Arrays.asList( new JLabel(), new JLabel() ) ) );
    ParameterDefinitionEntry[] entries = { entry1,entry2 };

    mockPane.refreshPaneParametersVisibility(entries);

    for ( ParameterDefinitionEntry entry : entries ) {
      final boolean parameterVisibility = mockPane.checkParameterVisibility( entry );
      entry.getName();
      mockPane.parametersVisualComponents.get( entry.getName() ).forEach(
        component -> assertTrue(
          component.isVisible() == parameterVisibility
        )
      );
    }
  }

}

