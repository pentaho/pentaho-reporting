/*
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
 * Copyright (c) 2000 - 2024 Hitachi Vantara, Simba Management Limited and Contributors...  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.gui.base.parameters;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import java.awt.Component;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JList;
import javax.swing.JViewport;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.parameters.ListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterValues;
import org.pentaho.reporting.libraries.designtime.swing.KeyedComboBoxModel;

@SuppressWarnings( "rawtypes" )
public class ListParameterComponentTest {

  private static final String PARAM_NAME = "param_name";

  private ListParameterComponent comp;
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

  @Test
  public void testCreateListComponent() {
    doReturn( false ).when( listParameter ).isAllowMultiSelection();
    doReturn( "horizontal" ).when( listParameter ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.LAYOUT, parameterContext );
    doReturn( "10" ).when( listParameter ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.VISIBLE_ITEMS, parameterContext );

    comp = new ListParameterComponent( listParameter, updateContext, parameterContext );

    verify( updateContext ).addChangeListener( any( ChangeListener.class ) );
    assertThat( comp.getComponentCount(), is( equalTo( 3 ) ) );

    JList list = findView( comp );
    assertThat( list, is( notNullValue() ) );
    assertThat( list.getSelectionMode(), is( equalTo( ListSelectionModel.SINGLE_SELECTION ) ) );
    assertThat( list.getListSelectionListeners().length, is( equalTo( 1 ) ) );
    assertThat( list.getLayoutOrientation(), is( equalTo( JList.HORIZONTAL_WRAP ) ) );
    assertThat( list.getVisibleRowCount(), is( equalTo( 1 ) ) );
  }

  @Test
  public void testCreateMultipleListComponent() {
    doReturn( true ).when( listParameter ).isAllowMultiSelection();
    doReturn( "vertical" ).when( listParameter ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.LAYOUT, parameterContext );
    doReturn( "10" ).when( listParameter ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.VISIBLE_ITEMS, parameterContext );

    comp = new ListParameterComponent( listParameter, updateContext, parameterContext );

    verify( updateContext ).addChangeListener( any( ChangeListener.class ) );
    assertThat( comp.getComponentCount(), is( equalTo( 3 ) ) );
    JList list = findView( comp );
    assertThat( list, is( notNullValue() ) );
    assertThat( list.getSelectionMode(), is( equalTo( ListSelectionModel.MULTIPLE_INTERVAL_SELECTION ) ) );
    assertThat( list.getListSelectionListeners().length, is( equalTo( 1 ) ) );
    assertThat( list.getLayoutOrientation(), is( equalTo( JList.VERTICAL ) ) );
    assertThat( list.getVisibleRowCount(), is( equalTo( 10 ) ) );
  }

  @Test
  public void testInitialize() throws Exception {
    ParameterValues paramValues = mock( ParameterValues.class );
    Calendar cal = Calendar.getInstance();
    cal.set( 2015, 5, 10 );
    Set<Object> keys = new HashSet<Object>();
    keys.add( "key_0" );
    keys.add( new BigDecimal( 1.0 ) );
    keys.add( cal.getTime() );
    doReturn( keys ).when( updateContext ).getParameterValue( PARAM_NAME );
    doReturn( paramValues ).when( listParameter ).getValues( parameterContext );
    doReturn( 3 ).when( paramValues ).getRowCount();
    doReturn( "key_0" ).when( paramValues ).getKeyValue( 0 );
    doReturn( new BigDecimal( 1.1 ) ).when( paramValues ).getKeyValue( 1 );
    cal.add( Calendar.DAY_OF_MONTH, 2 );
    doReturn( cal.getTime() ).when( paramValues ).getKeyValue( 2 );
    doReturn( "val_0" ).when( paramValues ).getTextValue( 0 );
    doReturn( "val_1" ).when( paramValues ).getTextValue( 1 );
    doReturn( "val_2" ).when( paramValues ).getTextValue( 2 );

    testCreateListComponent();
    comp.initialize();

    JList list = findView( comp );
    assertThat( list, is( notNullValue() ) );
    assertThat( list.getModel(), is( instanceOf( KeyedComboBoxModel.class ) ) );
    assertThat( (String) list.getSelectedValue(), is( equalTo( "val_0" ) ) );

    ListSelectionEvent evt = mock( ListSelectionEvent.class );
    doReturn( true ).when( evt ).getValueIsAdjusting();

    ListSelectionListener listener = list.getListSelectionListeners()[0];
    listener.valueChanged( evt );
    verify( updateContext, never() ).setParameterValue( anyString(), any() );

    doReturn( false ).when( evt ).getValueIsAdjusting();

    listener.valueChanged( evt );
    verify( updateContext ).setParameterValue( PARAM_NAME, "key_0" );

    doReturn( "key_incorrect" ).when( paramValues ).getKeyValue( 0 );
    comp.initialize();
    list = findView( comp );
    assertThat( list, is( notNullValue() ) );
    assertThat( list.getModel(), is( instanceOf( KeyedComboBoxModel.class ) ) );
    assertThat( (String) list.getSelectedValue(), is( nullValue() ) );
    listener = list.getListSelectionListeners()[0];
    listener.valueChanged( evt );
    verify( updateContext ).setParameterValue( PARAM_NAME, null );
  }

  @Test
  public void testInitializeMultiple() throws Exception {
    ParameterValues paramValues = mock( ParameterValues.class );
    Calendar cal = Calendar.getInstance();
    cal.set( 2015, 5, 10 );
    Set<Object> keys = new HashSet<Object>();
    keys.add( "key_0" );
    keys.add( new BigDecimal( 1.0 ) );
    keys.add( cal.getTime() );
    keys.add( "key_3" );
    doReturn( keys ).when( updateContext ).getParameterValue( PARAM_NAME );
    doReturn( paramValues ).when( listParameter ).getValues( parameterContext );
    doReturn( 3 ).when( paramValues ).getRowCount();
    doReturn( "key_0" ).when( paramValues ).getKeyValue( 0 );
    doReturn( new BigDecimal( 1.1 ) ).when( paramValues ).getKeyValue( 1 );
    cal.add( Calendar.DAY_OF_MONTH, 2 );
    doReturn( cal.getTime() ).when( paramValues ).getKeyValue( 2 );
    doReturn( "val_0" ).when( paramValues ).getTextValue( 0 );
    doReturn( "val_1" ).when( paramValues ).getTextValue( 1 );
    doReturn( "val_2" ).when( paramValues ).getTextValue( 2 );

    testCreateMultipleListComponent();
    comp.initialize();

    JList list = findView( comp );
    assertThat( list, is( notNullValue() ) );
    assertThat( list.getModel(), is( instanceOf( KeyedComboBoxModel.class ) ) );
    assertThat( list.getSelectedValuesList().size(), is( equalTo( 1 ) ) );
    assertThat( (String) list.getSelectedValue(), is( equalTo( "val_0" ) ) );

    ListSelectionEvent evt = mock( ListSelectionEvent.class );
    doReturn( true ).when( evt ).getValueIsAdjusting();
    ListSelectionListener listener = list.getListSelectionListeners()[0];
    listener.valueChanged( evt );
    verify( updateContext, never() ).setParameterValue( anyString(), any() );

    doReturn( false ).when( evt ).getValueIsAdjusting();

    listener.valueChanged( evt );
    verify( updateContext ).setParameterValue( PARAM_NAME, new String[] { "key_0" } );
  }

  private JList findView( ListParameterComponent comp ) {
    JList result = null;
    if ( comp.getComponentCount() > 0 ) {
      for ( int i = 0; i < comp.getComponentCount(); i++ ) {
        if ( comp.getComponent( i ) instanceof JViewport ) {
          JViewport view = (JViewport) comp.getComponent( i );
          Component list = view.getView();
          result = (JList) list;
          break;
        }
      }
    }
    return result;
  }
}
