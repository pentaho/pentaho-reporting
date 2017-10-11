/*
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
 * Copyright (c) 2001 - 2013 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.function.formula;

import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.libraries.docbundle.DocumentMetaData;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.coretypes.AnyType;

public class DocumentMetaDataFunction implements Function {
  public DocumentMetaDataFunction() {
  }

  public String getCanonicalName() {
    return "DOCUMENTMETADATA";
  }

  public TypeValuePair evaluate( final FormulaContext context, final ParameterCallback parameters )
    throws EvaluationException {
    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 1 || parameterCount > 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }
    if ( context instanceof ReportFormulaContext == false ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
    }
    final ReportFormulaContext rfc = (ReportFormulaContext) context;
    final DocumentMetaData data = rfc.getProcessingContext().getDocumentMetaData();

    if ( parameterCount == 1 ) {
      final String metaName =
          context.getTypeRegistry().convertToText( parameters.getType( 0 ), parameters.getValue( 0 ) );
      final String[] namespaces = data.getMetaDataNamespaces();
      for ( int i = 0; i < namespaces.length; i++ ) {
        final String namespace = namespaces[i];
        final Object attribute = data.getBundleAttribute( namespace, metaName );
        if ( attribute != null ) {
          return new TypeValuePair( AnyType.TYPE, attribute );
        }
      }
    } else {
      final String metaNamespace =
          context.getTypeRegistry().convertToText( parameters.getType( 0 ), parameters.getValue( 0 ) );
      final String metaName =
          context.getTypeRegistry().convertToText( parameters.getType( 1 ), parameters.getValue( 1 ) );
      final Object o = data.getBundleAttribute( metaNamespace, metaName );
      return new TypeValuePair( AnyType.TYPE, o );
    }

    throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
  }
}
