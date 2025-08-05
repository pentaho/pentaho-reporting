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


package org.pentaho.reporting.engine.classic.core.parameters;

import javax.swing.table.DefaultTableModel;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.states.PerformanceMonitorContext;
import org.pentaho.reporting.libraries.base.util.PerformanceLoggingStopWatch;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

public class DefaultReportParameterValidatorTest {

  private ParameterDefinitionEntry paramDefEntryMock;
  private ListParameter listParameterMock;
  private DefaultReportParameterValidator validator;

  @Before
  public void setUp() throws Exception {
    paramDefEntryMock = mock( ParameterDefinitionEntry.class );
    listParameterMock = mock( ListParameter.class );
    validator = new DefaultReportParameterValidator();
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testSelectDefault() throws ReportProcessingException {
    final DefaultTableModel tableModel = new DefaultTableModel( new String[] { "key", "value" }, 1 );
    tableModel.setValueAt( "key-entry", 0, 0 );
    tableModel.setValueAt( "value-entry", 0, 1 );

    final DefaultListParameter listParameter =
      new DefaultListParameter( "test", "key", "value", "name", false, true, String.class );
    listParameter.setParameterAutoSelectFirstValue( true );
    listParameter.setMandatory( true );

    final DefaultParameterDefinition definition = new DefaultParameterDefinition();
    definition.addParameterDefinition( listParameter );

    final MasterReport report = new MasterReport();
    report.setParameterDefinition( definition );
    report.setDataFactory( new TableDataFactory( "test", tableModel ) );

    final DefaultParameterContext paramContext = new DefaultParameterContext( report );

    final ValidationResult result = validator.validate( new ValidationResult(), definition, paramContext );
    assertTrue( result.isEmpty() );
  }

  @Test
  public void isValueMissingForMandatoryParameterCheckTestNonMandatory() {
    when( paramDefEntryMock.isMandatory() ).thenReturn( false );
    assertFalse( validator.isValueMissingForMandatoryParameterCheck( paramDefEntryMock, "value" ) );
  }

  @Test
  public void isValueMissingForMandatoryParameterCheckTestValueMissing() {
    when( paramDefEntryMock.isMandatory() ).thenReturn( true );
    assertTrue( validator.isValueMissingForMandatoryParameterCheck( paramDefEntryMock, null ) );
  }

  @Test
  public void isValueMissingForMandatoryParameterCheckTest() {
    when( paramDefEntryMock.isMandatory() ).thenReturn( true );
    assertFalse( validator.isValueMissingForMandatoryParameterCheck( paramDefEntryMock, "value" ) );
  }

  @Test
  public void isValueMissingForMandatoryParameterCheckTestEmptyValue() {
    when( paramDefEntryMock.isMandatory() ).thenReturn( true );
    assertFalse( validator.isValueMissingForMandatoryParameterCheck( paramDefEntryMock, "" ) );
  }

  @Test
  public void isValueMissingForMandatoryParameterCheckTestListParameterNoMultipleSelection() {
    when( listParameterMock.isMandatory() ).thenReturn( true );
    when( listParameterMock.isAllowMultiSelection() ).thenReturn( false );
    assertFalse( validator.isValueMissingForMandatoryParameterCheck( listParameterMock, "value" ) );
  }

  @Test
  public void isValueMissingForMandatoryParameterCheckTestListParameterMultipleSelectionNotListValue() {
    when( listParameterMock.isMandatory() ).thenReturn( true );
    when( listParameterMock.isAllowMultiSelection() ).thenReturn( true );
    assertFalse( validator.isValueMissingForMandatoryParameterCheck( listParameterMock, "value" ) );
  }

  @Test
  public void isValueMissingForMandatoryParameterCheckTestListParameterMultipleSelectionListValueEmpty() {
    when( listParameterMock.isMandatory() ).thenReturn( true );
    when( listParameterMock.isAllowMultiSelection() ).thenReturn( true );
    assertTrue( validator.isValueMissingForMandatoryParameterCheck( listParameterMock, new Object[] { } ) );
  }

  @Test
  public void isValueMissingForMandatoryParameterCheckTestListParameterMultipleSelectionListValue() {
    when( listParameterMock.isMandatory() ).thenReturn( true );
    when( listParameterMock.isAllowMultiSelection() ).thenReturn( true );
    assertFalse( validator.isValueMissingForMandatoryParameterCheck( listParameterMock, new Object[] { "value1", "value2" } ) );
  }

  @Test
  public void testValidateDateTimeTypeUsingReflection() throws Exception {
    Method validateDateTimeTypeMethod = DefaultReportParameterValidator.class
            .getDeclaredMethod("validateDateTimeType", Class.class, Object.class);
    validateDateTimeTypeMethod.setAccessible(true);

    assertTrue((Boolean) validateDateTimeTypeMethod.invoke(validator, Date.class, new Date()));
    assertFalse((Boolean) validateDateTimeTypeMethod.invoke(validator, Date.class, "InvalidType"));
    assertTrue((Boolean) validateDateTimeTypeMethod.invoke(validator, java.sql.Date.class, new java.sql.Date(System.currentTimeMillis())));
    assertTrue((Boolean) validateDateTimeTypeMethod.invoke(validator, java.sql.Time.class, new java.sql.Time(System.currentTimeMillis())));
    assertTrue((Boolean) validateDateTimeTypeMethod.invoke(validator, java.sql.Timestamp.class, new java.sql.Timestamp(System.currentTimeMillis())));
    assertFalse((Boolean) validateDateTimeTypeMethod.invoke(validator, java.sql.Date.class, "InvalidType"));
  }

  @Test
  public void testValidateDateParameterWithTimeComponent() throws Exception {
    String parameterName = "dateParam";

    // Mock parameter definition
    ReportParameterDefinition parameterDefinition = mock(ReportParameterDefinition.class);
    PlainParameter dateParameter = mock(PlainParameter.class);
    when(dateParameter.getName()).thenReturn(parameterName);
    when(dateParameter.getValueType()).thenReturn(Date.class);
    when(parameterDefinition.getParameterDefinitions()).thenReturn(new ParameterDefinitionEntry[]{dateParameter});

    // Create date value
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date dateWithTime = dateFormat.parse("2025-08-14 10:30:45");

    // Mock data rows
    DataRow dataRow = mock(DataRow.class);
    DataRow parentDataRow = mock(DataRow.class);
    when(dataRow.get(parameterName)).thenReturn(dateWithTime);
    when(dataRow.getColumnNames()).thenReturn(new String[]{parameterName});
    when(parentDataRow.getColumnNames()).thenReturn(new String[0]);

    // Mock parameter context
    DefaultParameterContext parameterContext = mock(DefaultParameterContext.class);
    CompoundDataRow compoundDataRow = new CompoundDataRow(dataRow, parentDataRow);
    when(parameterContext.getParameterData()).thenReturn(compoundDataRow);
    ReportEnvironment environment = mock(ReportEnvironment.class);
    when(environment.getLocale()).thenReturn(Locale.US);
    when(parameterContext.getReportEnvironment()).thenReturn(environment);
    PerformanceMonitorContext perfMonitorContext = mock(PerformanceMonitorContext.class);
    PerformanceLoggingStopWatch stopWatch = mock(PerformanceLoggingStopWatch.class);
    when(parameterContext.getPerformanceMonitorContext()).thenReturn(perfMonitorContext);
    when(perfMonitorContext.createStopWatch(any(String.class))).thenReturn(stopWatch);

    ValidationResult result = validator.validate(new ValidationResult(), parameterDefinition, parameterContext);
    assertTrue(result.isEmpty());
   }

   @Test
   public void testDateArrayParameterWithTimeComponents() throws Exception {
    String parameterName = "multipleDates";

    // Mock parameter definition
    ReportParameterDefinition parameterDefinition = mock(ReportParameterDefinition.class);
    PlainParameter dateArrayParameter = mock(PlainParameter.class);
    when(dateArrayParameter.getName()).thenReturn(parameterName);
    when(dateArrayParameter.getValueType()).thenReturn(Date[].class);
    when(parameterDefinition.getParameterDefinitions()).thenReturn(new ParameterDefinitionEntry[]{dateArrayParameter});

    // Create date values
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    Date date1WithTime = dateFormat.parse("2025-08-14 09:00:00");
    Date date2WithTime = dateFormat.parse("2025-08-15 17:30:00");
    Date[] datesWithTime = new Date[]{date1WithTime, date2WithTime};

    // Mock data rows
    DataRow dataRow = mock(DataRow.class);
    DataRow parentDataRow = mock(DataRow.class);
    when(dataRow.get(parameterName)).thenReturn(datesWithTime);
    when(dataRow.getColumnNames()).thenReturn(new String[]{parameterName});
    when(parentDataRow.getColumnNames()).thenReturn(new String[0]);

    // Mock parameter context
    DefaultParameterContext parameterContext = mock(DefaultParameterContext.class);
    CompoundDataRow compoundDataRow = new CompoundDataRow(dataRow, parentDataRow);
    when(parameterContext.getParameterData()).thenReturn(compoundDataRow);
    ReportEnvironment environment = mock(ReportEnvironment.class);
    when(environment.getLocale()).thenReturn(Locale.US);
    when(parameterContext.getReportEnvironment()).thenReturn(environment);
    PerformanceMonitorContext perfMonitorContext = mock(PerformanceMonitorContext.class);
    PerformanceLoggingStopWatch stopWatch = mock(PerformanceLoggingStopWatch.class);
    when(parameterContext.getPerformanceMonitorContext()).thenReturn(perfMonitorContext);
    when(perfMonitorContext.createStopWatch(any(String.class))).thenReturn(stopWatch);

    ValidationResult result = validator.validate(new ValidationResult(), parameterDefinition, parameterContext);
    assertTrue(result.isEmpty());
  }

  @Test
  public void testValidateSqlTimeTypeCompatibility() throws Exception {
    Method validateMethod = DefaultReportParameterValidator.class
            .getDeclaredMethod("validateDateTimeType", Class.class, Object.class);
    validateMethod.setAccessible(true);

    java.sql.Time sqlTime = new java.sql.Time(System.currentTimeMillis());
    java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
    java.util.Date utilDate = new java.util.Date();
    String invalidType = "invalid";

    assertTrue((Boolean) validateMethod.invoke(validator, java.sql.Time.class, sqlTime));
    assertTrue((Boolean) validateMethod.invoke(validator, java.sql.Time.class, timestamp));
    assertTrue((Boolean) validateMethod.invoke(validator, java.sql.Time.class, utilDate));
    assertFalse((Boolean) validateMethod.invoke(validator, java.sql.Time.class, invalidType));
  }

  @Test
  public void testValidateSqlTimestampTypeCompatibility() throws Exception {
    Method validateMethod = DefaultReportParameterValidator.class
            .getDeclaredMethod("validateDateTimeType", Class.class, Object.class);
    validateMethod.setAccessible(true);

    java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
    java.util.Date utilDate = new java.util.Date();
    java.sql.Time sqlTime = new java.sql.Time(System.currentTimeMillis());
    Integer invalidType = 123;

    assertTrue((Boolean) validateMethod.invoke(validator, java.sql.Timestamp.class, timestamp));
    assertTrue((Boolean) validateMethod.invoke(validator, java.sql.Timestamp.class, utilDate));
    assertTrue((Boolean) validateMethod.invoke(validator, java.sql.Timestamp.class, sqlTime));
    assertFalse((Boolean) validateMethod.invoke(validator, java.sql.Timestamp.class, invalidType));
  }

  @Test
  public void testValidateSqlDateTypeCompatibility() throws Exception {
    Method validateMethod = DefaultReportParameterValidator.class
            .getDeclaredMethod("validateDateTimeType", Class.class, Object.class);
    validateMethod.setAccessible(true);

    java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
    java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
    java.util.Date utilDate = new java.util.Date();
    Double invalidType = 123.45;

    assertTrue((Boolean) validateMethod.invoke(validator, java.sql.Date.class, sqlDate));
    assertTrue((Boolean) validateMethod.invoke(validator, java.sql.Date.class, timestamp));
    assertTrue((Boolean) validateMethod.invoke(validator, java.sql.Date.class, utilDate));
    assertFalse((Boolean) validateMethod.invoke(validator, java.sql.Date.class, invalidType));
  }

  @Test
  public void testValidateUtilDateTypeCompatibility() throws Exception {
    Method validateMethod = DefaultReportParameterValidator.class
            .getDeclaredMethod("validateDateTimeType", Class.class, Object.class);
    validateMethod.setAccessible(true);

    java.util.Date utilDate = new java.util.Date();
    java.sql.Date sqlDate = new java.sql.Date(System.currentTimeMillis());
    java.sql.Time sqlTime = new java.sql.Time(System.currentTimeMillis());
    java.sql.Timestamp timestamp = new java.sql.Timestamp(System.currentTimeMillis());
    Long invalidType = 123L;

    assertTrue((Boolean) validateMethod.invoke(validator, java.util.Date.class, utilDate));
    assertTrue((Boolean) validateMethod.invoke(validator, java.util.Date.class, sqlDate));
    assertTrue((Boolean) validateMethod.invoke(validator, java.util.Date.class, sqlTime));
    assertTrue((Boolean) validateMethod.invoke(validator, java.util.Date.class, timestamp));
    assertFalse((Boolean) validateMethod.invoke(validator, java.util.Date.class, invalidType));
  }

  @Test
  public void testValidateDateTypeWithNonDateParameter() throws Exception {
    Method validateMethod = DefaultReportParameterValidator.class
            .getDeclaredMethod("validateDateTimeType", Class.class, Object.class);
    validateMethod.setAccessible(true);

    assertFalse((Boolean) validateMethod.invoke(validator, String.class, new java.util.Date()));
    assertFalse((Boolean) validateMethod.invoke(validator, Integer.class, new java.sql.Date(System.currentTimeMillis())));
    assertFalse((Boolean) validateMethod.invoke(validator, Double.class, new java.sql.Time(System.currentTimeMillis())));
    assertFalse((Boolean) validateMethod.invoke(validator, Long.class, new java.sql.Timestamp(System.currentTimeMillis())));
  }

}
