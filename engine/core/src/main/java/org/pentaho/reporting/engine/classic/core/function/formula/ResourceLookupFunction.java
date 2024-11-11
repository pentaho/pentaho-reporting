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


import java.util.MissingResourceException;
import java.util.ResourceBundle;
import org.pentaho.reporting.engine.classic.core.ResourceBundleFactory;
import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.Type;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

/**
 * Created by dima.prokopenko@gmail.com on 9/29/2016.
 */
public class ResourceLookupFunction implements Function {

  public static final String NAME = "RESOURCELOOKUP";

  @Override public String getCanonicalName() {
    return NAME;
  }

  /**
   * RESOURCELOOKUP(bundleId, resourceId)
   *
   * @param context
   * @param parameters
   * @return
   * @throws EvaluationException
   */
  @Override public TypeValuePair evaluate( FormulaContext context, ParameterCallback parameters )
    throws EvaluationException {

    final int parameterCount = parameters.getParameterCount();
    if ( parameterCount < 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    Type textType = parameters.getType( 0 );
    Object textValue = parameters.getValue( 0 );
    final String bundleId = context.getTypeRegistry().convertToText( textType, textValue );

    if ( bundleId == null || bundleId.isEmpty() ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    textType = parameters.getType( 1 );
    textValue = parameters.getValue( 1 );
    String key = context.getTypeRegistry().convertToText( textType, textValue );

    if ( key == null || key.isEmpty() ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_INVALID_ARGUMENT_VALUE );
    }

    if ( context instanceof ReportFormulaContext ) {
      ReportFormulaContext fc = ReportFormulaContext.class.cast( context );
      try {
        ResourceBundleFactory factory = fc.getProcessingContext().getResourceBundleFactory();
        ResourceBundle bundle = factory.getResourceBundle( bundleId );
        if ( bundle != null && bundle.containsKey( key ) ) {
          key = bundle.getString( key );
        }
      } catch ( MissingResourceException | ClassCastException e ) {
        // no op
      }
    }

    return new TypeValuePair( TextType.TYPE, key );
  }
}
