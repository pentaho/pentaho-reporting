/*!
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
 * Copyright (c) 2002-2020 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.parameters;

import javax.swing.table.DefaultTableModel;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
}
