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
 *  Copyright (c) 2001 - 2016 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;

import javax.swing.table.DefaultTableModel;
import java.io.ByteArrayOutputStream;

import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * @author Andrei Abramov
 */
public class DataDefinitionFileWriterTest {

  @Test
  public void testWriteReport() throws Exception {

    DataDefinitionFileWriter dataDefinitionFileWriter = spy( new DataDefinitionFileWriter() );
    WriteableDocumentBundle writeableDocumentBundle = mock( WriteableDocumentBundle.class );
    BundleWriterState bundleWriterState = mock( BundleWriterState.class );
    MasterReport masterReport = mock( MasterReport.class );

    final ByteArrayOutputStream out = new ByteArrayOutputStream();

    final TableDataFactory dataFactory = new TableDataFactory();
    dataFactory.addTable( "default", new DefaultTableModel() );

    final DefaultListParameter listParameter =
      new DefaultListParameter( "test", "key", "value", "name", false, true, String.class );
    listParameter.setParameterAutoSelectFirstValue( true );
    listParameter.setMandatory( true );

    final PlainParameter plainParameter = new PlainParameter( "TestPlainParameter", Number.class );

    final DefaultParameterDefinition definition = new DefaultParameterDefinition();
    definition.addParameterDefinition( listParameter );
    definition.addParameterDefinition( plainParameter );

    doReturn( masterReport ).when( bundleWriterState ).getReport();
    doReturn( new DefaultParameterDefinition() ).when( masterReport ).getParameterDefinition();
    doReturn( out ).when( writeableDocumentBundle ).createEntry( any(), eq( "text/xml" ) );
    when( bundleWriterState.getReport().getExpressions() ).thenReturn( new ExpressionCollection() );
    doReturn( definition ).when( masterReport ).getParameterDefinition();
    doReturn( dataFactory ).when( masterReport ).getDataFactory();
    doReturn( "TEST" ).when( dataDefinitionFileWriter ).writeDataFactoryWrapper( any(), any(), any() );

    dataDefinitionFileWriter.writeReport( writeableDocumentBundle, bundleWriterState );

    assertTrue( out.toString().contains( "limit=\"0\"" ) );
    assertTrue( out.toString().contains( "timeout=\"0\"" ) );
    assertTrue( out.toString().contains( "ref=\"TEST\"" ) );
    assertTrue( out.toString().contains( "name=\"name\"" ) );
    assertTrue( out.toString().contains( "allow-multi-selection=\"false\"" ) );
    assertTrue( out.toString().contains( "strict-values=\"true\"" ) );
    assertTrue( out.toString().contains( "mandatory=\"true\"" ) );
    assertTrue( out.toString().contains( "query=\"test\"" ) );
    assertTrue( out.toString().contains( "key-column=\"key\"" ) );
    assertTrue( out.toString().contains( "value-column=\"value\"" ) );
    assertTrue( out.toString().contains( "name=\"TestPlainParameter\"" ) );
    assertTrue( out.toString().contains( "mandatory=\"false\"" ) );

  }

}
