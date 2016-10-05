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
import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.MasterReport;
import org.pentaho.reporting.engine.classic.core.TableDataFactory;
import org.pentaho.reporting.engine.classic.core.function.ExpressionCollection;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultListParameter;
import org.pentaho.reporting.engine.classic.core.parameters.DefaultParameterDefinition;
import org.pentaho.reporting.engine.classic.core.parameters.PlainParameter;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;

import javax.swing.table.DefaultTableModel;
import java.io.ByteArrayOutputStream;

import org.junit.Assert;
import org.mockito.Mockito;

/**
 * @author Andrei Abramov
 */
public class DataDefinitionFileWriterTest {

  @Test
  public void testWriteReport() throws Exception {

    DataDefinitionFileWriter dataDefinitionFileWriter = Mockito.spy( new DataDefinitionFileWriter() );
    WriteableDocumentBundle writeableDocumentBundle = Mockito.mock( WriteableDocumentBundle.class );
    BundleWriterState bundleWriterState = Mockito.mock( BundleWriterState.class );
    MasterReport masterReport = Mockito.mock( MasterReport.class );

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

    Mockito.doReturn( masterReport ).when( bundleWriterState ).getReport();
    Mockito.doReturn( new DefaultParameterDefinition() ).when( masterReport ).getParameterDefinition();
    Mockito.doReturn( out ).when( writeableDocumentBundle )
      .createEntry( Mockito.anyString(), Mockito.eq( "text/xml" ) );
    Mockito.when( bundleWriterState.getReport().getExpressions() ).thenReturn( new ExpressionCollection() );
    Mockito.doReturn( definition ).when( masterReport ).getParameterDefinition();
    Mockito.doReturn( dataFactory ).when( masterReport ).getDataFactory();
    Mockito.doReturn( "TEST" ).when( dataDefinitionFileWriter )
      .writeDataFactoryWrapper( Mockito.any( WriteableDocumentBundle.class ), Mockito.any( BundleWriterState.class ),
        Mockito.any(
          DataFactory.class ) );

    dataDefinitionFileWriter.writeReport( writeableDocumentBundle, bundleWriterState );

    Assert.assertTrue( out.toString().contains( "limit=\"0\"" ) );
    Assert.assertTrue( out.toString().contains( "timeout=\"0\"" ) );
    Assert.assertTrue( out.toString().contains( "ref=\"TEST\"" ) );
    Assert.assertTrue( out.toString().contains( "name=\"name\"" ) );
    Assert.assertTrue( out.toString().contains( "allow-multi-selection=\"false\"" ) );
    Assert.assertTrue( out.toString().contains( "strict-values=\"true\"" ) );
    Assert.assertTrue( out.toString().contains( "mandatory=\"true\"" ) );
    Assert.assertTrue( out.toString().contains( "query=\"test\"" ) );
    Assert.assertTrue( out.toString().contains( "key-column=\"key\"" ) );
    Assert.assertTrue( out.toString().contains( "value-column=\"value\"" ) );
    Assert.assertTrue( out.toString().contains( "name=\"TestPlainParameter\"" ) );
    Assert.assertTrue( out.toString().contains( "mandatory=\"false\"" ) );

  }

}
