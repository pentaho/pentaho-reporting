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

import javax.swing.*;
import javax.swing.event.ChangeListener;
import java.awt.Component;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterAttributeNames;
import org.pentaho.reporting.engine.classic.core.parameters.ParameterContext;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;

public class DatePickerParameterComponentTest {

  private static final String ENTRY_NAME = "entry_name";
  private static final String EXPECTED_DATE_VALUE = "11.09.2015";

  private DatePickerParameterComponent datePickerParameterComponent;
  private ParameterUpdateContext updateContext;
  private PlainParameter entry;
  private ParameterContext parameterContext;

  @Before
  public void setUp() {
    entry = mock( PlainParameter.class );
    parameterContext = mock( ParameterContext.class );
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

    datePickerParameterComponent = new DatePickerParameterComponent( entry, parameterContext, updateContext, false );

    verify( updateContext ).addChangeListener( any( ChangeListener.class ) );
    assertThat( datePickerParameterComponent.getComponentCount(), is( equalTo( 1 ) ) );
    assertThat( datePickerParameterComponent.getComponents()[0], is( instanceOf( JPanel.class ) ) );
  }

  @Test
  public void testInitializeWithDateValue() throws Exception {
    Calendar calendar = Calendar.getInstance();
    calendar.set( 2015, 8, 11 );
    doReturn( calendar.getTime() ).when( updateContext ).getParameterValue( ENTRY_NAME );

    datePickerParameterComponent.initialize();

    JTextField dateField = getDateField(datePickerParameterComponent);
    assertThat( dateField, is( notNullValue() ) );
    assertThat( dateField.getText(), is( equalTo( EXPECTED_DATE_VALUE ) ) );
  }

  @Test
  public void testInitializeWithDateTimeValue() throws Exception {
    doReturn( "dd.MM.yyyy HH:mm:ss" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
            ParameterAttributeNames.Core.DATA_FORMAT, parameterContext );
    doReturn( "dd.MM.yyyy HH:mm:ss" ).when( entry ).getTranslatedParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
            ParameterAttributeNames.Core.DATA_FORMAT, parameterContext );
    Calendar calendar = Calendar.getInstance();
    calendar.set( 2015, 8, 11, 14, 30, 45 );
    doReturn( calendar.getTime() ).when( updateContext ).getParameterValue( ENTRY_NAME );

    DatePickerParameterComponent dateTimePickerComponent = new DatePickerParameterComponent( entry, parameterContext, updateContext, true );
    dateTimePickerComponent.initialize();

    JTextField dateField = getDateField( dateTimePickerComponent );
    assertThat( dateField, is( notNullValue() ) );
    assertThat( dateField.getText(), is( equalTo( "11.09.2015 14:30:45" ) ) );

    JPanel panel = (JPanel) dateTimePickerComponent.getComponent( 0 );
    JSpinner hourSpinner = null;
    JSpinner minuteSpinner = null;
    JSpinner secondSpinner = null;
    JComboBox amPmComboBox = null;

    for ( Component component : panel.getComponents() ) {
      if ( component instanceof JSpinner ) {
        if (hourSpinner == null) {
          hourSpinner = (JSpinner) component;
        } else if (minuteSpinner == null) {
          minuteSpinner = (JSpinner) component;
        } else {
          secondSpinner = (JSpinner) component;
        }
      } else if ( component instanceof JComboBox ) {
        amPmComboBox = (JComboBox) component;
      }
    }

    assertThat( "Hour spinner should be present", hourSpinner, is( notNullValue() ) );
    assertThat( "Minute spinner should be present", minuteSpinner, is( notNullValue() ) );
    assertThat( "Second spinner should be present", secondSpinner, is( notNullValue() ) );
    assertThat( "AM/PM combo box should be present", amPmComboBox, is( notNullValue() ) );

    assertThat( hourSpinner.getValue(), is( equalTo( 2 ) ) );  // 14:00 in 12-hour format is 2:00 PM
    assertThat( minuteSpinner.getValue(), is( equalTo( 30 ) ) );
    assertThat( secondSpinner.getValue(), is( equalTo( 45 ) ) );
    assertThat( amPmComboBox.getSelectedIndex(), is( equalTo( 1 ) ) );  // PM = index 1
  }

  @Test
  public void testUpdateTime() throws Exception {
    // Setup time format
    doReturn( "dd.MM.yyyy HH:mm:ss" ).when( entry ).getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
            ParameterAttributeNames.Core.DATA_FORMAT, parameterContext );
    doReturn( "dd.MM.yyyy HH:mm:ss" ).when( entry ).getTranslatedParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
            ParameterAttributeNames.Core.DATA_FORMAT, parameterContext );

    // Initialize with a known date
    Calendar initialCalendar = Calendar.getInstance();
    initialCalendar.set(2015, 8, 11, 10, 30, 0); // September 11, 2015, 10:30:00 AM
    doReturn(initialCalendar.getTime()).when(updateContext).getParameterValue(ENTRY_NAME);

    // Create component with time selector enabled
    DatePickerParameterComponent dateTimePickerComponent = new DatePickerParameterComponent(entry, parameterContext, updateContext, true);
    dateTimePickerComponent.initialize();

    // Simulate time changes
    JPanel panel = (JPanel) dateTimePickerComponent.getComponent(0);
    JSpinner hourSpinner = null;
    JSpinner minuteSpinner = null;
    JSpinner secondSpinner = null;
    JComboBox amPmComboBox = null;

    for (Component component : panel.getComponents()) {
      if (component instanceof JSpinner) {
        if (hourSpinner == null) {
          hourSpinner = (JSpinner) component;
        } else if (minuteSpinner == null) {
          minuteSpinner = (JSpinner) component;
        } else {
          secondSpinner = (JSpinner) component;
        }
      } else if (component instanceof JComboBox) {
        amPmComboBox = (JComboBox) component;
      }
    }

    // Change time to 2:45:30 PM
    hourSpinner.setValue(2);
    minuteSpinner.setValue(45);
    secondSpinner.setValue(30);
    amPmComboBox.setSelectedIndex(1); // PM

    // Verify the text field has been updated with the correct format
    JTextField dateField = getDateField(dateTimePickerComponent);
    assertThat(dateField.getText(), is(equalTo("11.09.2015 14:45:30")));
  }

  @Test
  public void testSetDateWithNull() throws ReportDataFactoryException {
    datePickerParameterComponent.initialize(); // This calls setDate internally

    JTextField dateField = getDateField(datePickerParameterComponent);
    assertThat(dateField, is(notNullValue()));
    assertThat(dateField.getText(), is(equalTo("")));
  }

  @Test
  public void testSetDateWithEmptyString() throws ReportDataFactoryException {
    doReturn("").when(updateContext).getParameterValue(ENTRY_NAME);
    datePickerParameterComponent.initialize(); // This calls setDate internally

    JTextField dateField = getDateField(datePickerParameterComponent);
    assertThat(dateField, is(notNullValue()));
    assertThat(dateField.getText(), is(equalTo("")));
  }

  @Test
  public void testSetDateWithInvalidString() throws ReportDataFactoryException {
    doReturn("invalid-date").when(updateContext).getParameterValue(ENTRY_NAME);
    datePickerParameterComponent.initialize(); // This calls setDate internally

    JTextField dateField = getDateField(datePickerParameterComponent);
    assertThat(dateField, is(notNullValue()));
    assertThat(dateField.getText(), is(equalTo("")));
  }

  @Test
  public void testSetDateWithValidDate() throws ReportDataFactoryException {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2015, 8, 11); // September 11, 2015
    Date testDate = calendar.getTime();
    doReturn(testDate).when(updateContext).getParameterValue(ENTRY_NAME);

    datePickerParameterComponent.initialize(); // This calls setDate internally

    JTextField dateField = getDateField(datePickerParameterComponent);
    assertThat(dateField, is(notNullValue()));
    assertThat(dateField.getText(), is(equalTo("11.09.2015")));
  }

  @Test
  public void testSetDateWithTimeComponents() throws ReportDataFactoryException {
    // Setup time format
    doReturn("dd.MM.yyyy HH:mm:ss").when(entry).getParameterAttribute(
            ParameterAttributeNames.Core.NAMESPACE,
            ParameterAttributeNames.Core.DATA_FORMAT,
            parameterContext);
    doReturn("dd.MM.yyyy HH:mm:ss").when(entry).getTranslatedParameterAttribute(
            ParameterAttributeNames.Core.NAMESPACE,
            ParameterAttributeNames.Core.DATA_FORMAT,
            parameterContext);

    Calendar calendar = Calendar.getInstance();
    calendar.set(2015, 8, 11, 14, 30, 45); // September 11, 2015 14:30:45
    Date testDate = calendar.getTime();
    doReturn(testDate).when(updateContext).getParameterValue(ENTRY_NAME);

    DatePickerParameterComponent dateTimeComponent =
            new DatePickerParameterComponent(entry, parameterContext, updateContext, true);
    dateTimeComponent.initialize(); // This calls setDate internally

    JTextField dateField = getDateField(dateTimeComponent);
    assertThat(dateField, is(notNullValue()));
    assertThat(dateField.getText(), is(equalTo("11.09.2015 14:30:45")));
  }

  @Test
  public void testInitializeWithStringDateValue() throws Exception {
    doReturn( "2015-09-11T10:15:30.000UTC" ).when( updateContext ).getParameterValue( ENTRY_NAME );

    datePickerParameterComponent.initialize();

    JTextField dateField = getDateField(datePickerParameterComponent);
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
    DateFormat dateFormat = datePickerParameterComponent.createDateFormat( null, Locale.getDefault(), TimeZone.getDefault() );
    verifyCommonDateFormats( dateFormat );
    assertThat( dateFormat, instanceOf( DateFormat.class ) );
  }

  @Test
  public void testCreateDateFormat_ddMMyyyy() {
    DateFormat dateFormat = datePickerParameterComponent.createDateFormat( "dd.MM.yyyy", Locale.getDefault(), TimeZone.getDefault() );
    verifyCommonDateFormats( dateFormat );
    assertThat( dateFormat, instanceOf( SimpleDateFormat.class ) );
  }

  private void verifyCommonDateFormats( DateFormat dateFormat ) {
    assertThat( dateFormat, is( notNullValue() ) );
    assertFalse( dateFormat.isLenient() );
    assertEquals( TimeZone.getDefault(), dateFormat.getTimeZone() );
  }
}
