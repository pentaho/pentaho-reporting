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
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public class SugarFormulaLinkCustomizer extends FormulaLinkCustomizer {
  private static final String PENTAHO_PATH = "::pentaho-path";

  public SugarFormulaLinkCustomizer() {
  }

  private String computePentahoPath( final ParameterEntry[] entries ) throws EvaluationException {
    for ( int i = 0; i < entries.length; i++ ) {
      final ParameterEntry parameterEntry = entries[ i ];
      final String parameterName = parameterEntry.getParameterName();
      if ( "::pentaho-path".equals( parameterName ) ) {
        final Object o = parameterEntry.getParameterValue();
        if ( o != null ) {
          return PentahoPathNormalizerFunction.normalizePath( String.valueOf( o ) );
        }
      }
    }

    return null;
  }

  protected Map<String, Object> createParameterMap( final FormulaContext formulaContext,
                                                    final String configIndicator,
                                                    final String reportPath,
                                                    final ParameterEntry[] entries )
    throws UnsupportedEncodingException, BeanException, EvaluationException {
    final Map<String, Object> parameterMap =
      super.createParameterMap( formulaContext, configIndicator, reportPath, entries );
    parameterMap.put( PENTAHO_PATH, computePentahoPath( entries ) );
    return parameterMap;
  }

  public String format( final FormulaContext formulaContext,
                        final String configIndicator,
                        final String reportPath,
                        final ParameterEntry[] entries ) throws EvaluationException {
    return super.format( formulaContext, configIndicator, reportPath, entries );
  }

  protected boolean isFiltered( final ParameterEntry entry ) {
    if ( PENTAHO_PATH.equals( entry.getParameterName() ) ) {
      return true;
    }
    if ( "path".equals( entry.getParameterName() ) ) {
      return true;
    }
    if ( "name".equals( entry.getParameterName() ) ) {
      return true;
    }
    if ( "solution".equals( entry.getParameterName() ) ) {
      return true;
    }
    if ( "action".equals( entry.getParameterName() ) ) {
      return true;
    }
    return super.isFiltered( entry );
  }
}
