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

package org.pentaho.reporting.engine.classic.core.parameters;

import org.pentaho.reporting.engine.classic.core.DataFactory;
import org.pentaho.reporting.engine.classic.core.DataRow;
import org.pentaho.reporting.engine.classic.core.PerformanceTags;
import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;
import org.pentaho.reporting.engine.classic.core.ReportEnvironmentDataRow;
import org.pentaho.reporting.engine.classic.core.ReportProcessingException;
import org.pentaho.reporting.libraries.base.util.FormattedMessage;
import org.pentaho.reporting.libraries.base.util.PerformanceLoggingStopWatch;
import org.pentaho.reporting.libraries.base.util.StringUtils;

import javax.swing.table.TableModel;
import java.lang.reflect.Array;

public class DefaultListParameter extends AbstractParameter implements ListParameter {
  private String queryName;
  private String keyColumn;
  private String textColumn;
  private boolean strictValueCheck;
  private boolean allowMultiSelection;

  public DefaultListParameter( final String query, final String keyColumn, final String textColumn, final String name,
      final boolean allowMultiSelection, final boolean strictValueCheck, final Class valueType ) {
    super( name, valueType );
    if ( query == null ) {
      throw new NullPointerException();
    }
    if ( keyColumn == null ) {
      throw new NullPointerException();
    }
    if ( textColumn == null ) {
      throw new NullPointerException();
    }

    this.queryName = query;
    this.keyColumn = keyColumn;
    this.textColumn = textColumn;
    this.allowMultiSelection = allowMultiSelection;
    this.strictValueCheck = strictValueCheck;
  }

  public boolean isAllowMultiSelection() {
    return allowMultiSelection;
  }

  public String getKeyColumn() {
    return keyColumn;
  }

  public String getTextColumn() {
    return textColumn;
  }

  public String getQueryName() {
    return queryName;
  }

  public boolean isStrictValueCheck() {
    return strictValueCheck;
  }

  public ParameterValues getValues( final ParameterContext context ) throws ReportDataFactoryException {
    if ( context == null ) {
      throw new NullPointerException();
    }

    final DataRow parameterData = context.getParameterData();
    final ReportEnvironmentDataRow envDataRow = new ReportEnvironmentDataRow( context.getReportEnvironment() );
    final DataFactory dataFactory = context.getDataFactory();
    PerformanceLoggingStopWatch sw =
        context.getPerformanceMonitorContext().createStopWatch( PerformanceTags.REPORT_PARAMETER_QUERY,
            new FormattedMessage( "query={%s}", getQueryName() ) );
    try {
      sw.start();
      final TableModel tableModel =
          dataFactory.queryData( getQueryName(), new CompoundDataRow( envDataRow, parameterData ) );

      final String formula =
          getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
              ParameterAttributeNames.Core.DISPLAY_VALUE_FORMULA, context );
      if ( StringUtils.isEmpty( formula, true ) ) {
        return new DefaultParameterValues( tableModel, getKeyColumn(), getTextColumn() );
      }

      try {
        return new ComputedParameterValues( tableModel, getKeyColumn(), getTextColumn(), formula, context );
      } catch ( ReportProcessingException e ) {
        throw new ReportDataFactoryException( "Failed to initialize parameter-value-collection", e );
      }
    } finally {
      sw.close();
    }
  }

  public void setParameterAutoSelectFirstValue( final boolean autoSelect ) {
    setParameterAttribute( ParameterAttributeNames.Core.NAMESPACE, ParameterAttributeNames.Core.AUTOFILL_SELECTION,
        String.valueOf( autoSelect ) );
  }

  public boolean isParameterAutoSelectFirstValue() {
    return ( "true".equals( getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.AUTOFILL_SELECTION ) ) );
  }

  private boolean isParameterAutoSelectFirstValue( final ParameterContext parameterContext ) {
    if ( "true".equals( getParameterAttribute( ParameterAttributeNames.Core.NAMESPACE,
        ParameterAttributeNames.Core.AUTOFILL_SELECTION, parameterContext ) ) ) {
      return true;
    }
    return ( "true".equals( parameterContext.getConfiguration().getConfigProperty(
        "org.pentaho.reporting.engine.classic.core.ParameterAutoFillsSelection" ) ) );
  }

  public Object getDefaultValue( final ParameterContext context ) throws ReportDataFactoryException {
    final Object o = super.getDefaultValue( context );
    if ( o != null ) {
      return o;
    }

    if ( isParameterAutoSelectFirstValue( context ) ) {
      final ParameterValues values = getValues( context );
      if ( values.getRowCount() > 0 ) {
        if ( allowMultiSelection ) {
          final Object array;
          final Class valueType1 = getValueType();
          if ( valueType1.isArray() ) {
            array = Array.newInstance( valueType1.getComponentType(), 1 );
          } else {
            array = Array.newInstance( valueType1, 1 );
          }
          Array.set( array, 0, values.getKeyValue( 0 ) );
          return array;
        } else {
          return values.getKeyValue( 0 );
        }
      }
    }

    if ( allowMultiSelection ) {
      final Class valueType1 = getValueType();
      if ( valueType1.isArray() ) {
        return Array.newInstance( valueType1.getComponentType(), 0 );
      } else {
        return Array.newInstance( valueType1, 0 );
      }
    }
    return null;
  }
}
