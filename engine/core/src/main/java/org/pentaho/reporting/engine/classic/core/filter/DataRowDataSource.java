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

package org.pentaho.reporting.engine.classic.core.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.engine.classic.core.function.FormulaExpression;

/**
 * A DataSource that can access values from the 'data-row'. The data-row contains all values from the current row of the
 * report's <code>TableModel</code>, plus the current values of the defined expressions and functions for the report.
 * <p/>
 * The DataRowDataSource can either query the data-row directly using the specified field name or it can evaluate a
 * given formula (which must be compatible to the OpenFormula specifications) to compute the value.
 * <p/>
 * Fields and formulas are mutually exclusive; defining a field name autmatically undefines the formula and vice versa.
 *
 * @author Thomas Morgner
 * @see org.pentaho.reporting.engine.classic.core.DataRow
 */
public class DataRowDataSource implements DataSource {
  private static final Log logger = LogFactory.getLog( DataRowDataSource.class );

  /**
   * The field name that should be queried.
   */
  private String field;

  /**
   * The formula-expression that computes the result value, if no field is given.
   */
  private FormulaExpression valueExpression;

  /**
   * Default constructor.
   * <p/>
   * The expression name is empty ("", not null), the value initially null.
   */
  public DataRowDataSource() {
    this( null );
  }

  /**
   * Constructs a new data source.
   *
   * @param column
   *          the name of the field, function or expression in the data-row.
   */
  public DataRowDataSource( final String column ) {
    this.field = column;
  }

  /**
   * @deprecated Required for legacy-parsing, do not use elsewhere.
   */
  public String getField() {
    return getDataSourceColumnName();
  }

  /**
   * @param field
   * @deprecated Required for legacy-parsing, do not use elsewhere.
   */
  public void setField( final String field ) {
    setDataSourceColumnName( field );
  }

  /**
   * Returns the data source column name.
   *
   * @return the column name.
   */
  public String getDataSourceColumnName() {
    return field;
  }

  /**
   * Defines the name of the column in the datarow to be queried.
   *
   * @param dataSourceColumnName
   *          the name of the column in the datarow to be queried.
   * @throws NullPointerException
   *           if the name is <code>null</code>.
   * @see org.pentaho.reporting.engine.classic.core.DataRow#get
   */
  public void setDataSourceColumnName( final String dataSourceColumnName ) {
    if ( dataSourceColumnName == null ) {
      throw new NullPointerException();
    }
    this.field = dataSourceColumnName;
    if ( valueExpression != null ) {
      this.valueExpression.setFormula( null );
    }
  }

  /**
   * Returns the formula used to compute the value of the data source.
   *
   * @return the formula.
   */
  public String getFormula() {
    if ( valueExpression == null ) {
      return null;
    }
    return valueExpression.getFormula();
  }

  /**
   * Defines the formula used to compute the value of this data source.
   *
   * @param formula
   *          the formula for the data source.
   */
  public void setFormula( final String formula ) {
    if ( formula == null ) {
      throw new NullPointerException();
    }

    this.field = null;
    if ( valueExpression == null ) {
      valueExpression = new FormulaExpression();
    }
    this.valueExpression.setFormula( formula );
    if ( "field".equals( valueExpression.getFormulaNamespace() ) ) {
      DataRowDataSource.logger
          .warn( "Encountered formula with 'field' prefix. Direct access to field-data should not be done using a formula. "
              + "Auto-Fixing." );
      this.field = valueExpression.getFormulaExpression();
      this.valueExpression.setFormula( null );
    }
  }

  /**
   * Returns the current value of the data source, obtained from a particular column in the data-row.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( runtime == null ) {
      return null;
    }

    if ( field != null ) {
      return runtime.getDataRow().get( field );
    }
    if ( valueExpression == null ) {
      return null;
    }

    valueExpression.setRuntime( runtime );
    try {
      return valueExpression.getValue();
    } catch ( Exception e ) {
      // ignore ..
      return null;
    } finally {
      valueExpression.setRuntime( null );
    }
  }

  /**
   * Clones the data source. A previously registered report definition is not inherited to the clone.
   *
   * @return a clone.
   * @throws CloneNotSupportedException
   *           if the cloning is not supported.
   */
  public DataRowDataSource clone() throws CloneNotSupportedException {
    final DataRowDataSource drs = (DataRowDataSource) super.clone();
    if ( valueExpression != null ) {
      drs.valueExpression = (FormulaExpression) valueExpression.clone();
    }
    return drs;
  }
}
