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
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.event.ChangeListener;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;

public class DatePickerParameterComponentTest {

  private static final String ENTRY_NAME = "entry_name";
  private static final String EXPECTED_DATE_VALUE = "11.09.2015";

  private DatePickerParameterComponent comp;
  private ParameterUpdateContext updateContext;

  @Before
  public void setUp() {
    PlainParameter entry = mock( PlainParameter.class );
    ParameterContext parameterContext = mock( ParameterContext.class );
    updateContext = mock( ParameterUpdateContext.class );
    ResourceBundleFactory resourceBundleFactory = mock( ResourceBundleFactory.class );
    Locale locale = new Locale( "test_test" );

    doReturn( ENTRY_NAME ).when( entry ).getName();
    doReturn( Date.class ).when( entry ).getValueType();
    doReturn( "dd.MM.yyyy" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.DATA_FORMAT, parameterContext );
    doReturn( "dd.MM.yyyy" ).when( entry ).getTranslatedParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
      ParameterAttributeNames.Core.DATA_FORMAT, parameterContext );
    doReturn( "utc" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.TIMEZONE, parameterContext );

    doReturn( resourceBundleFactory ).when( parameterContext ).getResourceBundleFactory();
    doReturn( locale ).when( resourceBundleFactory ).getLocale();
    doReturn( TimeZone.getDefault() ).when( resourceBundleFactory ).getTimeZone();

    comp = new DatePickerParameterComponent( entry, parameterContext, updateContext );

    verify( updateContext ).addChangeListener( any( ChangeListener.class ) );
    assertThat( comp.getComponentCount(), is( equalTo( 1 ) ) );
    assertThat( comp.getComponents()[0], is( instanceOf( JPanel.class ) ) );
  }

  @Test
  public void testInitializeWithDateValue() throws Exception {
    Calendar calendar = Calendar.getInstance();
    calendar.set( 2015, 8, 11 );
    doReturn( calendar.getTime() ).when( updateContext ).getParameterValue( ENTRY_NAME );

    comp.initialize();

    JTextField dateField = getDateField( comp );
    assertThat( dateField, is( notNullValue() ) );
    assertThat( dateField.getText(), is( equalTo( EXPECTED_DATE_VALUE ) ) );
  }

  @Test
  public void testInitializeWithStringDateValue() throws Exception {
    doReturn( "2015-09-11T10:15:30.000UTC" ).when( updateContext ).getParameterValue( ENTRY_NAME );

    comp.initialize();

    JTextField dateField = getDateField( comp );
    assertThat( dateField, is( notNullValue() ) );
    assertThat( dateField.getText(), is( equalTo( EXPECTED_DATE_VALUE ) ) );
  }

  private JTextField getDateField( DatePickerParameterComponent comp ) {
    JTextField field = null;
    if ( comp.getComponentCount() > 0 && comp.getComponent( 0 ) instanceof JPanel ) {
      JPanel panel = (JPanel) comp.getComponent( 0 );
      if ( panel.getComponentCount() > 0 && panel.getComponent( 0 ) instanceof JTextField ) {
        field = (JTextField) panel.getComponent( 0 );
      }
    }
    return field;
  }

  @Test
  public void testCreateDateFormat_null() {
    DateFormat dateFormat = comp.createDateFormat( null, Locale.getDefault(), TimeZone.getDefault() );
    verifyCommonDateFormats( dateFormat );
    assertThat( dateFormat, instanceOf( DateFormat.class ) );
  }

  @Test
  public void testCreateDateFormat_ddMMyyyy() {
    DateFormat dateFormat = comp.createDateFormat( "dd.MM.yyyy", Locale.getDefault(), TimeZone.getDefault() );
    verifyCommonDateFormats( dateFormat );
    assertThat( dateFormat, instanceOf( SimpleDateFormat.class ) );
  }

  private void verifyCommonDateFormats( DateFormat dateFormat ) {
    assertThat( dateFormat, is( notNullValue() ) );
    assertFalse( dateFormat.isLenient() );
    assertEquals( TimeZone.getDefault(), dateFormat.getTimeZone() );
  }
}
