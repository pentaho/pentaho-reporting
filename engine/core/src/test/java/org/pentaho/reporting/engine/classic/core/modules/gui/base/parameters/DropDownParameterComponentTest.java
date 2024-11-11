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


package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.util.Date;

import javax.swing.JComboBox;
import javax.swing.event.ChangeListener;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.parameters.ListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterValues;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;

public class DropDownParameterComponentTest {

  private static final String PARAM_NAME = "param_name";

  private DropDownParameterComponent comp;
  private ParameterUpdateContext updateContext;
  private ListParameter listParameter;
  private ParameterContext parameterContext;

  @Before
  public void setUp() {
    listParameter = mock( ListParameter.class );
    parameterContext = mock( ParameterContext.class );
    updateContext = mock( ParameterUpdateContext.class );
    doReturn( PARAM_NAME ).when( listParameter ).getName();
  }

  @Test( expected = NullPointerException.class )
  public void testCreateCompWithoutParameter() {
    new DropDownParameterComponent( null, null, null );
  }

  @Test( expected = NullPointerException.class )
  public void testCreateCompWithoutUpdateContext() {
    new DropDownParameterComponent( listParameter, null, null );
  }

  @Test( expected = NullPointerException.class )
  public void testCreateCompWithoutParamContext() {
    new DropDownParameterComponent( listParameter, updateContext, null );
  }

  @Test
  public void testCreateComponent() throws Exception {
    ParameterValues paramValues = mock( ParameterValues.class );
    doReturn( "key_0" ).when( updateContext ).getParameterValue( PARAM_NAME );
    doReturn( paramValues ).when( listParameter ).getValues( parameterContext );
    doReturn( 3 ).when( paramValues ).getRowCount();
    doReturn( "key_0" ).when( paramValues ).getKeyValue( 0 );
    doReturn( new BigDecimal( 1.1 ) ).when( paramValues ).getKeyValue( 1 );
    doReturn( new Date() ).when( paramValues ).getKeyValue( 2 );
    doReturn( "val_0" ).when( paramValues ).getTextValue( 0 );
    doReturn( "val_1" ).when( paramValues ).getTextValue( 1 );
    doReturn( "val_2" ).when( paramValues ).getTextValue( 1 );

    comp = new DropDownParameterComponent( listParameter, updateContext, parameterContext );
    verify( updateContext ).addChangeListener( any( ChangeListener.class ) );

    comp.initialize();
    assertThat( (String) comp.getSelectedItem(), is( equalTo( "val_0" ) ) );

    doReturn( new BigDecimal( 1.0 ) ).when( updateContext ).getParameterValue( PARAM_NAME );
    comp.initialize();
    assertThat( (String) comp.getSelectedItem(), is( nullValue() ) );

    doReturn( new Date() ).when( updateContext ).getParameterValue( PARAM_NAME );
    comp.initialize();
    assertThat( (String) comp.getSelectedItem(), is( nullValue() ) );

    doReturn( null ).when( updateContext ).getParameterValue( PARAM_NAME );
    comp.initialize();
    assertThat( (String) comp.getSelectedItem(), is( nullValue() ) );
  }

  @SuppressWarnings( "rawtypes" )
  @Test
  public void testCheckActionListener() {
    ActionEvent e = mock( ActionEvent.class );
    JComboBox box = mock( JComboBox.class );
    KeyedComboBoxModel theModel = mock( KeyedComboBoxModel.class );
    doReturn( box ).when( e ).getSource();
    doReturn( theModel ).when( box ).getModel();
    doReturn( "key" ).when( theModel ).getSelectedKey();

    comp = new DropDownParameterComponent( listParameter, updateContext, parameterContext );
    assertThat( comp.getActionListeners().length, is( equalTo( 1 ) ) );
    ActionListener listener = comp.getActionListeners()[0];
    listener.actionPerformed( e );

    verify( updateContext ).setParameterValue( PARAM_NAME, "key" );
  }

}
