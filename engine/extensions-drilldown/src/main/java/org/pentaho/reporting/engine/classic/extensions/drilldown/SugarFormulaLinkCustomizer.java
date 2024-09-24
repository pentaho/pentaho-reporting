/*!
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
* Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
*/

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
