/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.writer;

import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.writer.BundleWriterException;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.EmbeddedKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaArgument;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaParameter;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactoryModule;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleTransformationProducer;
import org.pentaho.reporting.libraries.docbundle.WriteableDocumentBundle;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

public class EmdeddedTransformationProducerWriteHandlerTest {

  private EmdeddedTransformationProducerWriteHandler handler;

  @Before
  public void before() throws Exception {
    ClassicEngineBoot.getInstance().start();
    handler = new EmdeddedTransformationProducerWriteHandler();
  }

  @Test( expected = BundleWriterException.class )
  public void writeKettleRepositoryProducer_exception_on_wrong_producer_type() throws IOException,
    BundleWriterException {
    KettleTransformationProducer producer = mock( KettleTransformationProducer.class );
    handler.writeKettleRepositoryProducer( mock( WriteableDocumentBundle.class ), "TEST_FILE_NAME",
        mock( XmlWriter.class ), "TEST_QUERY_NAME", producer );
  }

  @Test
  public void writeKettleRepositoryProducer() throws IOException, BundleWriterException {
    EmbeddedKettleTransformationProducer producer = mock( EmbeddedKettleTransformationProducer.class );
    when( producer.getArguments() ).thenReturn( new FormulaArgument[] {} );
    when( producer.getParameter() ).thenReturn( new FormulaParameter[] {} );
    WriteableDocumentBundle bundle = mock( WriteableDocumentBundle.class );
    when( bundle.createEntry( anyString(), anyString() ) ).thenReturn( mock( OutputStream.class ) );
    XmlWriter xmlWriter = mock( XmlWriter.class );
    handler.writeKettleRepositoryProducer( bundle, "TEST_FILE_NAME", xmlWriter, "TEST_QUERY_NAME", producer );
    verify( xmlWriter ).writeTag( eq( KettleDataFactoryModule.NAMESPACE ), eq( "query-embedded" ),
        any( AttributeList.class ), eq( XmlWriter.OPEN ) );
    verify( xmlWriter ).writeTag( KettleDataFactoryModule.NAMESPACE, "resource", XmlWriter.OPEN );
    verify( xmlWriter ).writeText( "TEST_QUERY_NAME.ktr" );
  }

}
