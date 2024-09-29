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


package org.pentaho.reporting.engine.classic.core.parameters;

import org.pentaho.reporting.engine.classic.core.ReportDataFactoryException;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * @deprecated This class is not used anywhere and you can get the same functionality via a default-list-parameter and a
 *             table-datasource.
 */
@Deprecated
public class StaticListParameter extends AbstractParameter implements ListParameter {
  private static class StaticParameterValues implements ParameterValues {
    private ArrayList<Object[]> backend;

    public StaticParameterValues() {
      backend = new ArrayList<Object[]>();
    }

    public void add( final Object key, final Object value ) {
      backend.add( new Object[] { key, value } );
    }

    public int getRowCount() {
      return backend.size();
    }

    public Object getKeyValue( final int row ) {
      final Object[] o = backend.get( row );
      return o[0];
    }

    public Object getTextValue( final int row ) {
      final Object[] o = backend.get( row );
      return o[1];
    }
  }

  private boolean strictValueCheck;
  private boolean allowMultiSelection;
  private StaticParameterValues parameterValues;

  public StaticListParameter( final String name, final boolean allowMultiSelection, final boolean strictValueCheck,
      final Class valueType ) {
    super( name, valueType );
    this.allowMultiSelection = allowMultiSelection;
    this.strictValueCheck = strictValueCheck;
    this.parameterValues = new StaticParameterValues();
  }

  public void addValues( final Object key, final Object value ) {
    parameterValues.add( key, value );
  }

  public boolean isAllowMultiSelection() {
    return allowMultiSelection;
  }

  public boolean isStrictValueCheck() {
    return strictValueCheck;
  }

  public ParameterValues getValues( final ParameterContext context ) throws ReportDataFactoryException {
    if ( context == null ) {
      throw new NullPointerException();
    }

    return parameterValues;
  }

  private boolean isParameterAutoSelectFirstValue( final ParameterContext parameterContext ) {
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
        } else {
          values.getKeyValue( 0 );
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
