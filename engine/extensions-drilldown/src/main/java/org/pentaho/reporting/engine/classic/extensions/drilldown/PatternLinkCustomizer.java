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

package org.pentaho.reporting.engine.classic.extensions.drilldown;

import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.engine.classic.core.util.beans.ConverterRegistry;
import org.pentaho.reporting.libraries.base.util.URLEncoder;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;

import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

public class PatternLinkCustomizer implements LinkCustomizer {
  public PatternLinkCustomizer() {
  }

  public String format( final FormulaContext formulaContext,
                        final String configIndicator,
                        final String reportPath,
                        final ParameterEntry[] entries ) throws EvaluationException {
    try {
      final String parameter = computeParameter( formulaContext, entries );
      final String pattern = computePattern( configIndicator, parameter );
      final MessageFormat messageFormat =
        new MessageFormat( pattern, formulaContext.getLocalizationContext().getLocale() );
      return messageFormat.format( new Object[] { reportPath, parameter } );
    } catch ( UnsupportedEncodingException e ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    } catch ( BeanException e ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    }
  }

  public static String computeParameter( final FormulaContext formulaContext, final ParameterEntry[] entries )
    throws UnsupportedEncodingException, BeanException {
    final String encoding = formulaContext.getConfiguration()
      .getConfigProperty( "org.pentaho.reporting.libraries.formula.URLEncoding", "UTF-8" );
    final StringBuilder parameter = new StringBuilder( 1000 );
    for ( int i = 0; i < entries.length; i++ ) {
      final ParameterEntry entry = entries[ i ];
      if ( i != 0 ) {
        parameter.append( '&' );
      }
      final Object value = entry.getParameterValue();
      if ( value instanceof Object[] ) {
        final Object[] array = (Object[]) value;
        for ( int j = 0; j < array.length; j++ ) {
          if ( j != 0 ) {
            parameter.append( '&' );
          }
          final Object o = array[ j ];
          parameter.append( URLEncoder.encode( entry.getParameterName(), encoding ) );
          parameter.append( '=' );
          parameter.append( URLEncoder.encode( ConverterRegistry.toAttributeValue( o ), encoding ) );
        }
      } else if ( value != null ) {
        parameter.append( URLEncoder.encode( entry.getParameterName(), encoding ) );
        parameter.append( '=' );
        parameter.append( URLEncoder.encode( ConverterRegistry.toAttributeValue( value ), encoding ) );
      } else {
        parameter.append( URLEncoder.encode( entry.getParameterName(), encoding ) );
        parameter.append( '=' );
      }
    }
    return parameter.toString();
  }

  private String computePattern( final String configIndicator, String parameter ) throws EvaluationException {
    final DrillDownProfile downProfile = DrillDownProfileMetaData.getInstance().getDrillDownProfile( configIndicator );
    String pattern = parameter == null || parameter.isEmpty() ? "patternNoAttributes" : "pattern";
    return downProfile.getAttribute( pattern );
  }

}
