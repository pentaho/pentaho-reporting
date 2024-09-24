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

package org.pentaho.reporting.engine.classic.core.filter.templates;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.ComponentDrawableFilter;
import org.pentaho.reporting.engine.classic.core.filter.DataRowDataSource;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

/**
 * An drawable field template. The drawable content will be read from the datarow.
 *
 * @author Thomas Morgner
 */
public class ComponentFieldTemplate extends AbstractTemplate {
  /**
   * The data row reader.
   */
  private DataRowDataSource dataRowDataSource;

  /**
   * The filter that converts the AWT-Components into Drawables.
   */
  private ComponentDrawableFilter drawableFilter;

  /**
   * Creates a new image field template.
   */
  public ComponentFieldTemplate() {
    dataRowDataSource = new DataRowDataSource();
    drawableFilter = new ComponentDrawableFilter();
    drawableFilter.setDataSource( dataRowDataSource );
  }

  /**
   * Returns the field name.
   *
   * @return The field name.
   */
  public String getField() {
    return dataRowDataSource.getDataSourceColumnName();
  }

  /**
   * Sets the field name.
   *
   * @param field
   *          the field name.
   */
  public void setField( final String field ) {
    dataRowDataSource.setDataSourceColumnName( field );
  }

  /**
   * Returns the formula used to compute the value of the data source.
   *
   * @return the formula.
   */
  public String getFormula() {
    return dataRowDataSource.getFormula();
  }

  /**
   * Defines the formula used to compute the value of this data source.
   *
   * @param formula
   *          the formula for the data source.
   */
  public void setFormula( final String formula ) {
    dataRowDataSource.setFormula( formula );
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return the value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    return drawableFilter.getValue( runtime, element );
  }

  /**
   * Clones the template.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public ComponentFieldTemplate clone() throws CloneNotSupportedException {
    final ComponentFieldTemplate template = (ComponentFieldTemplate) super.clone();
    template.drawableFilter = template.drawableFilter.clone();
    template.dataRowDataSource = (DataRowDataSource) template.drawableFilter.getDataSource();
    return template;
  }

  /**
   * Returns the datarow data source used in this template.
   *
   * @return the datarow data source.
   */
  protected DataRowDataSource getDataRowDataSource() {
    return dataRowDataSource;
  }

  /**
   * Returns the filter that converts the components into Drawable objects.
   *
   * @return the drawable filter.
   */
  protected ComponentDrawableFilter getDrawableFilter() {
    return drawableFilter;
  }
}
