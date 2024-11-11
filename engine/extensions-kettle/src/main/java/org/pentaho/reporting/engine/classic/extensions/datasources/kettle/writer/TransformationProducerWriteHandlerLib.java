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

import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.AbstractKettleTransformationProducer;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaArgument;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.FormulaParameter;
import org.pentaho.reporting.engine.classic.extensions.datasources.kettle.KettleDataFactoryModule;
import org.pentaho.reporting.libraries.xmlns.common.AttributeList;
import org.pentaho.reporting.libraries.xmlns.writer.XmlWriter;

import java.io.IOException;

public class TransformationProducerWriteHandlerLib {
  private TransformationProducerWriteHandlerLib() {
  }

  public static void writeParameterAndArguments( final XmlWriter xmlWriter,
      final AbstractKettleTransformationProducer fileProducer ) throws IOException {
    final FormulaArgument[] definedArgumentNames = fileProducer.getArguments();
    final FormulaParameter[] parameterMappings = fileProducer.getParameter();
    for ( int i = 0; i < definedArgumentNames.length; i++ ) {
      final FormulaArgument arg = definedArgumentNames[i];
      xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "argument", "formula", arg.getFormula(), XmlWriter.CLOSE );
    }

    for ( int i = 0; i < parameterMappings.length; i++ ) {
      final FormulaParameter parameterMapping = parameterMappings[i];
      final AttributeList paramAttr = new AttributeList();
      paramAttr.setAttribute( KettleDataFactoryModule.NAMESPACE, "variable-name", parameterMapping.getName() );
      paramAttr.setAttribute( KettleDataFactoryModule.NAMESPACE, "formula", parameterMapping.getFormula() );
      xmlWriter.writeTag( KettleDataFactoryModule.NAMESPACE, "variable", paramAttr, XmlWriter.CLOSE );
    }
  }

}
