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

import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.StaticDataRow;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.GenericExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.ReportFormulaContext;
import org.pentaho.reporting.engine.classic.core.function.WrapperExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.layout.output.DefaultProcessingContext;
import org.pentaho.reporting.engine.classic.core.parameters.CompoundDataRow;
import org.pentaho.reporting.engine.classic.core.util.beans.BeanException;
import org.pentaho.reporting.libraries.formula.ErrorValue;
import org.pentaho.reporting.libraries.formula.EvaluationException;
import org.pentaho.reporting.libraries.formula.Formula;
import org.pentaho.reporting.libraries.formula.FormulaContext;
import org.pentaho.reporting.libraries.formula.LibFormulaErrorValue;
import org.pentaho.reporting.libraries.formula.parser.ParseException;

import javax.swing.table.DefaultTableModel;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FormulaLinkCustomizer implements LinkCustomizer {
  private static final String TAB_ACTIVE_PARAMETER = "::TabActive";
  private static final String TAB_NAME_PARAMETER = "::TabName";

  public FormulaLinkCustomizer() {
  }

  private String computeMantleTabActive( final FormulaContext formulaContext,
                                         final ParameterEntry[] entries ) throws EvaluationException {
    for ( int i = 0; i < entries.length; i++ ) {
      final ParameterEntry parameterEntry = entries[ i ];
      final String parameterName = parameterEntry.getParameterName();
      if ( TAB_ACTIVE_PARAMETER.equals( parameterName ) ) {
        final Object o = parameterEntry.getParameterValue();
        if ( o != null ) {
          return String.valueOf( o );
        }
      }
    }

    final Object o = formulaContext.resolveReference( TAB_ACTIVE_PARAMETER );
    if ( o != null ) {
      return String.valueOf( o );
    }
    return null;
  }

  private String computeMantleTabName( final FormulaContext formulaContext,
                                       final ParameterEntry[] entries ) throws EvaluationException {
    for ( int i = 0; i < entries.length; i++ ) {
      final ParameterEntry parameterEntry = entries[ i ];
      final String parameterName = parameterEntry.getParameterName();
      if ( TAB_NAME_PARAMETER.equals( parameterName ) ) {
        final Object o = parameterEntry.getParameterValue();
        if ( o != null ) {
          return String.valueOf( o );
        }
      }
    }

    final Object o = formulaContext.resolveReference( TAB_NAME_PARAMETER );
    if ( o != null ) {
      return String.valueOf( o );
    }
    return null;
  }

  private ParameterEntry[] filterEntries( final ParameterEntry[] entries ) {
    final ArrayList<ParameterEntry> list = new ArrayList<ParameterEntry>();
    for ( int i = 0; i < entries.length; i++ ) {
      final ParameterEntry entry = entries[ i ];
      if ( isFiltered( entry ) ) {
        continue;
      }

      list.add( entry );
    }
    return list.toArray( new ParameterEntry[ list.size() ] );
  }


  protected boolean isFiltered( final ParameterEntry entry ) {
    if ( TAB_NAME_PARAMETER.equals( entry.getParameterName() ) ) {
      return true;
    }
    if ( TAB_ACTIVE_PARAMETER.equals( entry.getParameterName() ) ) {
      return true;
    }
    return false;
  }

  private Object[][] createEntryTable( final ParameterEntry[] entries ) {
    final Object[][] values = new Object[ entries.length ][ 2 ];
    for ( int i = 0; i < entries.length; i++ ) {
      final ParameterEntry entry = entries[ i ];
      values[ i ][ 0 ] = ( entry.getParameterName() );
      values[ i ][ 1 ] = ( entry.getParameterValue() );
    }
    return values;
  }

  public String format( final FormulaContext formulaContext,
                        final String configIndicator,
                        final String reportPath,
                        final ParameterEntry[] entries ) throws EvaluationException {
    try {
      final Map<String, Object> parameterValues =
        createParameterMap( formulaContext, configIndicator, reportPath, entries );
      final StaticDataRow staticDataRow = new StaticDataRow( parameterValues );

      final ExpressionRuntime expressionRuntime;
      if ( formulaContext instanceof ReportFormulaContext ) {
        final ReportFormulaContext rfc = (ReportFormulaContext) formulaContext;
        expressionRuntime = new WrapperExpressionRuntime( staticDataRow, rfc.getRuntime() );
      } else {
        expressionRuntime = new GenericExpressionRuntime
          ( new CompoundDataRow( staticDataRow, createDataRow( entries ) ),
            new DefaultTableModel(), -1, new DefaultProcessingContext() );
      }


      final String formula = computeFormula( configIndicator );
      final Formula compiledFormula = new Formula( formula );
      compiledFormula.initialize( new ReportFormulaContext( formulaContext, expressionRuntime ) );
      final Object o = compiledFormula.evaluate();
      if ( o instanceof ErrorValue ) {
        throw EvaluationException.getInstance( (ErrorValue) o );
      }
      if ( o == null ) {
        throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_NA_VALUE );
      }
      return String.valueOf( o );
    } catch ( final UnsupportedEncodingException e ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    } catch ( final BeanException e ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    } catch ( ParseException e ) {
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    } catch ( EvaluationException e ) {
      throw e;
    } catch ( Exception e ) {
      e.printStackTrace();
      throw EvaluationException.getInstance( LibFormulaErrorValue.ERROR_UNEXPECTED_VALUE );
    }
  }

  protected Map<String, Object> createParameterMap( final FormulaContext formulaContext,
                                                    final String configIndicator,
                                                    final String reportPath,
                                                    final ParameterEntry[] entries )
    throws UnsupportedEncodingException, BeanException, EvaluationException {
    final String parameter = PatternLinkCustomizer.computeParameter( formulaContext, filterEntries( entries ) );
    final HashMap<String, Object> parameterValues = new HashMap<String, Object>();
    if ( reportPath == null || reportPath.endsWith( "/" ) ) {
      parameterValues.put( "::path", reportPath );
    } else {
      // make sure the path ends in slash for consistency
      parameterValues.put( "::path", reportPath + "/" );
    }
    parameterValues.put( "::parameter", parameter );
    parameterValues.put( "::config", configIndicator );
    parameterValues.put( "::entries", createEntryTable( entries ) );
    parameterValues.put( TAB_NAME_PARAMETER, computeMantleTabName( formulaContext, entries ) );
    parameterValues.put( TAB_ACTIVE_PARAMETER, computeMantleTabActive( formulaContext, entries ) );
    return parameterValues;
  }

  private String computeFormula( final String configIndicator ) throws EvaluationException {
    final DrillDownProfile downProfile = DrillDownProfileMetaData.getInstance().getDrillDownProfile( configIndicator );
    return downProfile.getAttribute( "formula" );
  }

  private DataRow createDataRow( final ParameterEntry[] parameterEntries ) {
    final String[] parameterNames = new String[ parameterEntries.length ];
    final Object[] parameterValues = new Object[ parameterEntries.length ];
    for ( int i = 0; i < parameterEntries.length; i++ ) {
      final ParameterEntry entry = parameterEntries[ i ];
      parameterNames[ i ] = entry.getParameterName();
      parameterValues[ i ] = entry.getParameterValue();
    }
    return new StaticDataRow( parameterNames, parameterValues );
  }

}
