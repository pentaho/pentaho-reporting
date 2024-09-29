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
import org.pentaho.reporting.engine.classic.core.filter.DataRowDataSource;
import org.pentaho.reporting.engine.classic.core.filter.DrawableLoadFilter;
import org.pentaho.reporting.engine.classic.core.filter.URLFilter;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.net.URL;

/**
 * An image URL field template, which reads the image from an URL supplied from a column in the DataRow.
 *
 * @author Thomas Morgner
 */
public class DrawableURLFieldTemplate extends AbstractTemplate {
  /**
   * An image load filter.
   */
  private DrawableLoadFilter imageLoadFilter;

  /**
   * A data row accessor.
   */
  private DataRowDataSource dataRowDataSource;

  /**
   * A URL filter.
   */
  private URLFilter urlFilter;

  /**
   * Creates a new template.
   */
  public DrawableURLFieldTemplate() {
    dataRowDataSource = new DataRowDataSource();
    urlFilter = new URLFilter();
    urlFilter.setDataSource( dataRowDataSource );
    imageLoadFilter = new DrawableLoadFilter();
    imageLoadFilter.setDataSource( urlFilter );
  }

  /**
   * Returns the name of the field from the data-row that the template gets images from.
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
   * Returns the base URL.
   *
   * @return The base URL.
   */
  public URL getBaseURL() {
    return urlFilter.getBaseURL();
  }

  /**
   * Sets the base URL.
   *
   * @param baseURL
   *          the base URL.
   */
  public void setBaseURL( final URL baseURL ) {
    urlFilter.setBaseURL( baseURL );
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
    return imageLoadFilter.getValue( runtime, element );
  }

  /**
   * Clones the template.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public DrawableURLFieldTemplate clone() throws CloneNotSupportedException {
    final DrawableURLFieldTemplate template = (DrawableURLFieldTemplate) super.clone();
    template.imageLoadFilter = imageLoadFilter.clone();
    template.urlFilter = (URLFilter) template.imageLoadFilter.getDataSource();
    template.dataRowDataSource = (DataRowDataSource) template.urlFilter.getDataSource();
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
