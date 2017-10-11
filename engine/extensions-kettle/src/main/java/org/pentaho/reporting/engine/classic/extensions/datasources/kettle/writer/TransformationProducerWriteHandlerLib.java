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
