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
 *  Copyright (c) 2006 - 2015 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.designer.core.editor.parameters;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.designer.core.ReportDesignerContext;
import org.pentaho.reporting.engine.classic.core.parameters.AbstractParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterDefinitionEntry;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import static org.mockito.Mockito.*;
import static org.mockito.internal.verification.VerificationModeFactory.times;

/**
 * Created by dima.prokopenko@gmail.com on 9/26/2016.
 */
public class ParameterDialogTest {

  ReportDesignerContext context = mock( ReportDesignerContext.class );
  ParameterDialog dialog;

  @Before
  public void before() {
    dialog = new ParameterDialog( context );

    dialog.queryComboBoxModel = mock( StaticTextComboBoxModel.class );
    dialog.strictValuesCheckBox = mock( JCheckBox.class );
    dialog.idComboBox = mock( JComboBox.class );
    dialog.nameTextField = mock( JTextField.class );

    dialog.translateLabel = mock( JCheckBox.class );
    dialog.translateDataFormat = mock( JCheckBox.class );
    dialog.translateValues = mock( JCheckBox.class );
    dialog.translateErrorMesage = mock( JCheckBox.class );
    dialog.resourceTextField = mock( JTextField.class );
    dialog.errorMesageTextField = mock( JTextField.class );
  }

  @Test
  public void testWhenUpdateFromParameterLoadTranslationsInfo() {
    ParameterDefinitionEntry p = new PlainParameter( "junit_param" );
    dialog = spy( dialog );
    dialog.updateFromParameter( p );
    verify( dialog, times( 1 ) ).loadParameterTranslations( p );
  }

  @Test
  public void testWhenSaveUpdateParameterQueryNullDefinition() {
    dialog = spy( dialog );

    when( dialog.nameTextField.getText() ).thenReturn( "name" );
    when( dialog.queryComboBoxModel.getSelectedItem() ).thenReturn( null );
    dialog.createParameterResult();
    verify( dialog, times( 1 ) ).createQuerylessParameter( anyString(), anyString(), any(), anyString(), anyBoolean(),
      any( ParameterType.class ) );
    verify( dialog, times( 1 ) ).updateParameterTranslation( any( AbstractParameter.class ) );
  }

  @Test
  public void testWhenSaveUpdateParameterQueryNotNullDefinition() {
    dialog = spy( dialog );

    when( dialog.queryComboBoxModel.getSelectedItem() ).thenReturn( "has some query" );
    when( dialog.strictValuesCheckBox.isSelected() ).thenReturn( true );
    when( dialog.idComboBox.getSelectedItem() ).thenReturn( "id" );
    when( dialog.nameTextField.getText() ).thenReturn( "someText" );

    dialog.createParameterResult();
    verify( dialog, times( 0 ) ).createQuerylessParameter( anyString(), anyString(), any(), anyString(), anyBoolean(),
      any( ParameterType.class ) );
    verify( dialog, times( 1 ) ).updateParameterTranslation( any( AbstractParameter.class ) );
  }

  @Test
  public void updateParameterNoTranslationTest() {
    AbstractParameter p = spy( new PlainParameter( "junit_param" ) );

    // here all mocks returns null or false.
    dialog.translateLabel = mock( JCheckBox.class );
    dialog.translateDataFormat = mock( JCheckBox.class );
    dialog.translateValues = mock( JCheckBox.class );
    dialog.translateErrorMesage = mock( JCheckBox.class );

    dialog.resourceTextField = mock( JTextField.class );
    dialog.errorMesageTextField = mock( JTextField.class );

    dialog.updateParameterTranslation( p );

    // so no interactions with parameter made
    verify( p, times( 0 ) ).setParameterAttribute(
      any(),
      any(),
      any() );
  }

  @Test
  public void updateParameterTranslationTest() {
    AbstractParameter p = spy( new PlainParameter( "junit_param" ) );

    when( dialog.translateLabel.isSelected() ).thenReturn( true );
    when( dialog.translateDataFormat.isSelected() ).thenReturn( true );
    when( dialog.translateValues.isSelected() ).thenReturn( true );
    when( dialog.translateErrorMesage.isSelected() ).thenReturn( true );

    when( dialog.resourceTextField.getText() ).thenReturn( "resource" );
    when( dialog.errorMesageTextField.getText() ).thenReturn( "error" );

    dialog.updateParameterTranslation( p );

    verify( p, times( 1 ) ).setParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_LABEL ),
      eq( "true" )
    );
    verify( p, times( 1 ) ).setParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_DATA_FORMAT ),
      eq( "true" )
    );
    verify( p, times( 1 ) ).setParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_DISPLAY_NAMES ),
      eq( "true" )
    );
    verify( p, times( 1 ) ).setParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_ERROR_MESSAGE ),
      eq( "true" )
    );

    verify( p, times( 1 ) ).setParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_RESOURCE_ID ),
      eq( "resource" )
    );
    verify( p, times( 1 ) ).setParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.ERROR_MESSAGE ),
      eq( "error" )
    );
  }

  @Test
  public void loadParameterTranslationsTest() {
    ParameterDefinitionEntry p = mock( ParameterDefinitionEntry.class );
    when( p.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_LABEL ),
      any()
    ) ).thenReturn( "true" );
    when( p.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_DATA_FORMAT ),
      any()
    ) ).thenReturn( "true" );
    when( p.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_DISPLAY_NAMES ),
      any()
    ) ).thenReturn( "true" );
    when( p.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_ERROR_MESSAGE ),
      any()
    ) ).thenReturn( "true" );
    when( p.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.TRANSLATE_RESOURCE_ID ),
      any()
    ) ).thenReturn( "translateId" );
    when( p.getParameterAttribute(
      eq( ParameterAttributeNames.Core.NAMESPACE ),
      eq( ParameterAttributeNames.Core.ERROR_MESSAGE ),
      any()
    ) ).thenReturn( "error" );

    dialog.loadParameterTranslations( p );

    verify( dialog.translateLabel, times( 1 ) ).setSelected( eq( true ) );
    verify( dialog.translateDataFormat, times( 1 ) ).setSelected( eq( true ) );
    verify( dialog.translateValues, times( 1 ) ).setSelected( eq( true ) );
    verify( dialog.translateErrorMesage, times( 1 ) ).setSelected( eq( true ) );

    verify( dialog.resourceTextField, times( 1 ) ).setText( eq( "translateId" ) );
    verify( dialog.errorMesageTextField, times( 1 ) ).setText( eq( "error" ) );
  }
}
