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

package org.pentaho.reporting.engine.classic.extensions.datasources.kettle.writer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;

import org.junit.Test;

import static org.mockito.Mockito.*;

import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.AbstractKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaArgument;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaParameter;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactoryModule;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

public class TransformationProducerWriteHandlerLibTest {

  @Test
  public void writeParameterAndArguments() throws IOException {
    XmlWriter xmlWriter = mock( XmlWriter.class );

    AbstractKettleTransformationProducer fileProducer = mock( AbstractKettleTransformationProducer.class );
    when( fileProducer.getArguments() ).thenReturn(
        new FormulaArgument[] { FormulaArgument.create( "TEST_REPORT_FIELD" ) } );
    when( fileProducer.getParameter() ).thenReturn(
        new FormulaParameter[] { FormulaParameter.create( "TEST_REPORT_FIELD", "TEST_TRANS_PARAMETER_NAME" ) } );
    TransformationProducerWriteHandlerLib.writeParameterAndArguments( xmlWriter, fileProducer );

    verify( xmlWriter ).writeTag( KettleDataFactoryModule.NAMESPACE, "argument", "formula", "=[TEST_REPORT_FIELD]",
        XmlWriter.CLOSE );

    verify( xmlWriter ).writeTag( eq( KettleDataFactoryModule.NAMESPACE ), eq( "variable" ),
        any( AttributeList.class ), eq( XmlWriter.CLOSE ) );
  }

}
