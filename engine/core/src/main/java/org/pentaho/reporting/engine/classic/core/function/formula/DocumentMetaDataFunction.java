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
