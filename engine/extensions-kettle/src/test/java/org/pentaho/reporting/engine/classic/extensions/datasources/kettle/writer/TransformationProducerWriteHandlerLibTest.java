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
 *  Copyright (c) 2006 - 2017 Hitachi Vantara..  All rights reserved.
 */
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
