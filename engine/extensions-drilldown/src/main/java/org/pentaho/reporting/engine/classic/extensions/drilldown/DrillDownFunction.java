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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.function.Function;
import org.pentaho.reporting.libraries.formula.function.ParameterCallback;
import org.pentaho.reporting.libraries.formula.lvalues.TypeValuePair;
import org.pentaho.reporting.libraries.formula.typing.ArrayCallback;
import org.pentaho.reporting.libraries.formula.typing.Sequence;
import org.pentaho.reporting.libraries.formula.typing.TypeRegistry;
import org.pentaho.reporting.libraries.formula.typing.TypeUtil;
import org.pentaho.reporting.libraries.formula.typing.coretypes.TextType;

import java.util.Collection;

public class DrillDownFunction implements Function {
  private static final Log logger = LogFactory.getLog( DrillDownFunction.class );

  public DrillDownFunction() {
  }

  public String getCanonicalName() {
    return "DRILLDOWN";
  }

  public TypeValuePair evaluate( final FormulaContext context,
                                 final ParameterCallback parameters ) throws EvaluationException {
    if ( parameters.getParameterCount() != 3 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    // 0: Configuration-indicator (gives the pattern indirectly) never the pattern itself
    // 1: the report-path
    // 2: the parameter as 2d-array (name value pairs)

    final TypeRegistry typeRegistry = context.getTypeRegistry();
    final String configIndicator = typeRegistry.convertToText( parameters.getType( 0 ), parameters.getValue( 0 ) );
    String path;
    try {
      path = typeRegistry.convertToText( parameters.getType( 1 ), parameters.getValue( 1 ) );
    } catch ( EvaluationException ee ) {
      if ( LibFormulaErrorValue.ERROR_NA_VALUE.equals( ee.getErrorValue() ) ) {
        path = null;
      } else {
        throw ee;
      }
    }
    final ArrayCallback parameter = typeRegistry.convertToArray( parameters.getType( 2 ), parameters.getValue( 2 ) );

    final LinkCustomizer pattern = createLinkCustomizer( configIndicator );
    return new TypeValuePair( TextType.TYPE, pattern.format( context, configIndicator, path,
      computeParameterEntries( parameter, typeRegistry ) ) );
  }

  private ParameterEntry[] computeParameterEntries( final ArrayCallback paraCallback,
                                                    final TypeRegistry typeRegistry ) throws EvaluationException {
    if ( paraCallback.getColumnCount() == 0 ) {
      return new ParameterEntry[ 0 ];
    }
    if ( paraCallback.getColumnCount() != 2 ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_ARGUMENTS_VALUE );
    }

    final int rowCount = paraCallback.getRowCount();
    final ParameterEntry[] retval = new ParameterEntry[ rowCount ];
    for ( int r = 0; r < rowCount; r++ ) {
      final String name = typeRegistry.convertToText( paraCallback.getType( r, 0 ), paraCallback.getValue( r, 0 ) );
      try {
        final Object value = paraCallback.getValue( r, 1 );
        if ( value instanceof ArrayCallback ) {
          retval[ r ] = new ParameterEntry( name, TypeUtil.normalize( (ArrayCallback) value ) );
        } else if ( value instanceof Sequence ) {
          retval[ r ] = new ParameterEntry( name, TypeUtil.normalize( (Sequence) value ) );
        } else if ( value instanceof Collection ) {
          final Collection c = (Collection) value;
          retval[ r ] = new ParameterEntry( name, c.toArray() );
        } else {
          retval[ r ] = new ParameterEntry( name, value );
        }
      } catch ( EvaluationException ee ) {
        if ( ee.getErrorValue() == LibFormulaErrorValue.ERROR_NA_VALUE ) {
          retval[ r ] = new ParameterEntry( name, null );
        } else {
          throw ee;
        }
      }
    }
    return retval;
  }

  private LinkCustomizer createLinkCustomizer( final String configIndicator ) throws EvaluationException {
    final DrillDownProfile downProfile = DrillDownProfileMetaData.getInstance().getDrillDownProfile( configIndicator );
    if ( downProfile == null ) {
      logger.warn( String.format( "Referenced drilldown profile %s does not exist.", configIndicator ) );
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    }
    try {
      return (LinkCustomizer) downProfile.getLinkCustomizerType().newInstance();
    } catch ( InstantiationException e ) {
      logger.warn( String.format( "Failed to instantiate profile %s.", configIndicator ), e );
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    } catch ( IllegalAccessException e ) {
      logger.warn( String.format( "Failed to instantiate profile %s.", configIndicator ), e );
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    }
  }
}
