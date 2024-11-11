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


package org.pentaho.reporting.engine.classic.core.filter.templates;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.AnchorFilter;
import org.pentaho.reporting.engine.classic.core.filter.DataRowDataSource;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

/**
 * The anchor-field template provides the default field for assigning anchors to an location within the document.
 *
 * @author Thomas Morgner
 * @deprecated the Anchor-Field has been deprecated. Use the style-key "anchor" instead.
 */
public class AnchorFieldTemplate extends AbstractTemplate {
  /**
   * The data-row data source.
   */
  private DataRowDataSource dataRowDataSource;

  /**
   * A string filter.
   */
  private AnchorFilter anchorFilter;

  /**
   * Creates a new string field template.
   */
  public AnchorFieldTemplate() {
    dataRowDataSource = new DataRowDataSource();
    anchorFilter = new AnchorFilter();
    anchorFilter.setDataSource( dataRowDataSource );
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
    return anchorFilter.getValue( runtime, element );
  }

  /**
   * Clones the template.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public AnchorFieldTemplate clone() throws CloneNotSupportedException {
    final AnchorFieldTemplate template = (AnchorFieldTemplate) super.clone();
    template.anchorFilter = anchorFilter.clone();
    template.dataRowDataSource = (DataRowDataSource) template.anchorFilter.getDataSource();
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
}
