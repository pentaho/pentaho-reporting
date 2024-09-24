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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.bugs;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.ReportParameterValidationException;
import org.pentaho.reporting.engine.classic.core.modules.output.pageable.plaintext.PlainTextReportUtil;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriter;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.StaticListParameter;
import org.pentaho.reporting.engine.classic.core.testsupport.gold.GoldTestBase;
import org.pentaho.reporting.libraries.base.util.NullOutputStream;
import org.pentaho.reporting.libraries.docbundle.BundleUtilities;
import org.pentaho.reporting.libraries.docbundle.DocumentBundle;
import org.pentaho.reporting.libraries.docbundle.MemoryDocumentBundle;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceKey;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

import java.io.File;
import java.lang.reflect.Array;
import java.util.Arrays;

public class Prd3795IT extends TestCase {
  public Prd3795IT() {
  }

  public Prd3795IT( final String name ) {
    super( name );
  }

  public void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  /**
   * tests save and reopen of a report containing a multi-value parameter without a query (a
   * <code>StaticListParameter</code>. Verifies that <code>DataDefinitionFileWriter</code> and
   * <code>ListParameterReadHandler</code> correctly handle such parameters.
   */
  public void testSaveAndLoadOfMultivalueParameterWithoutQuery() throws Exception {

    final String[] defaultValue = { "item 1", "item 2" };
    final Class valueType = Array.newInstance( String.class, 0 ).getClass();

    MasterReport report = createMultiValueParamReport( valueType, defaultValue, false );

    final File testReport = File.createTempFile( "prd-3795-", ".prpt" );
    saveReport( report, testReport );

    final MasterReport reopenedReport = GoldTestBase.parseReport( testReport );
    final StaticListParameter param =
        (StaticListParameter) reopenedReport.getParameterDefinition().getParameterDefinition( 0 );

    assertEquals( "Parameter type should be String array", valueType, param.getValueType() );
    assertEquals( "name", param.getName() );
    assertTrue( "Default values of reloaded report do not match", Arrays.equals( defaultValue, (String[]) param
        .getDefaultValue() ) );
    testReport.delete();
  }

  public void testValidationWithDifferentParameterValuesAndTypes() throws Exception {
    final Object[][] values =
        new Object[][] { { "item 1", "item 2" }, { 1, 2 }, { 1.1, 2.2 }, { "item 1", 2 }, { null }, { 1, null }, {} };
    final Class[] types =
        new Class[] { Array.newInstance( String.class, 0 ).getClass(), Array.newInstance( Integer.class, 0 ).getClass() };
    for ( Class type : types ) {
      for ( Object[] value : values ) {
        for ( boolean mandatory : new boolean[] { false, true } ) {
          MasterReport report = createMultiValueParamReport( type, value, mandatory );
          if ( typeMatchesValue( type, value, mandatory ) ) {
            assertReportRuns( "Valid parameters. Report should run.", report );
          } else {
            assertReportThrows( "Invalid parameters.  Report should fail.", report,
                ReportParameterValidationException.class );
          }
        }
      }
    }
    // verify single values cause validation failure
    assertReportThrows( "Single value should not be allowed with array type in param.", createMultiValueParamReport(
        types[1], 1010, true ), ReportParameterValidationException.class );
  }

  /**
   * Returns false if the type of any of the objects in values do not match type, or if mandatory==true and values is
   * either null or empty.
   */
  private boolean typeMatchesValue( final Class type, final Object[] values, final boolean mandatory ) {
    if ( mandatory && ( values == null || values.length == 0 ) ) {
      return false;
    }
    for ( Object value : values ) {
      if ( value != null && value.getClass() != type.getComponentType() ) {
        return false;
      }
    }
    return true;
  }

  /**
   * Verifies report can run without an exception being thrown.
   */
  private void assertReportRuns( String message, MasterReport report ) {
    try {
      PlainTextReportUtil.createPlainText( report, new NullOutputStream(), 10, 15 );
    } catch ( Exception e ) {
      fail( message + "\n" + e.getMessage() );
    }
  }

  /**
   * Will fail if the specified exception is not thrown while executing report.
   */
  private void assertReportThrows( String message, MasterReport report, final Class exceptionType ) throws Exception {
    try {
      PlainTextReportUtil.createPlainText( report, new NullOutputStream(), 10, 15 );
    } catch ( Exception e ) {
      if ( !( e.getClass() == exceptionType ) ) {
        fail( message + "\nExpected exception did not occur.  Expected: " + exceptionType.toString() + ",\n but got: "
            + e.getClass().toString() + "\n" + e.getMessage() );
      }
      return;
    }
    fail( "Expected exception did not occur\n" + message );
  }

  /**
   * This method does what the report designer does on save.
   */
  private void saveReport( final MasterReport report, final File file ) throws Exception {
    BundleWriter.writeReportToZipFile( report, file );
    final ResourceManager resourceManager = report.getResourceManager();
    final Resource bundleResource = resourceManager.createDirectly( file, DocumentBundle.class );
    final DocumentBundle bundle = (DocumentBundle) bundleResource.getResource();
    final ResourceKey bundleKey = bundle.getBundleKey();

    final MemoryDocumentBundle mem = new MemoryDocumentBundle();
    BundleUtilities.copyStickyInto( mem, bundle );
    BundleUtilities.copyMetaData( mem, bundle );
    report.setBundle( mem );
    report.setContentBase( mem.getBundleMainKey() );
    report.setDefinitionSource( bundleKey );
  }

  /**
   * Create a report with a StaticListParameter containing specified defaultValue.
   */
  private MasterReport createMultiValueParamReport( final Class valueType, final Object defaultValue,
      final boolean mandatory ) {
    final StaticListParameter listParameter = new StaticListParameter( "name", true, false, valueType );
    listParameter.setMandatory( mandatory );
    listParameter.setDefaultValue( defaultValue );
    final DefaultParameterDefinition parameterDefinition = new DefaultParameterDefinition();
    parameterDefinition.addParameterDefinition( listParameter );

    final MasterReport report = new MasterReport();
    report.setParameterDefinition( parameterDefinition );

    return report;
  }

}
