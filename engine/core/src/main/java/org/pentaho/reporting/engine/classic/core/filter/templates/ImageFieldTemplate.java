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

package org.pentaho.reporting.engine.classic.core.filter.templates;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.DataRowDataSource;
import org.pentaho.reporting.engine.classic.core.filter.ImageLoadFilter;
import org.pentaho.reporting.engine.classic.core.filter.ImageRefFilter;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

/**
 * An image field template. The image content will be read from the datarow.
 *
 * @author Thomas Morgner
 */
public class ImageFieldTemplate extends AbstractTemplate {
  /**
   * The data row reader.
   */
  private DataRowDataSource dataRowDataSource;

  /**
   * An image reference filter.
   */
  private ImageRefFilter imageRefFilter;

  /**
   * The filter that is used to load the image from a URL, file or Blob.
   */
  private ImageLoadFilter imageLoadFilter;

  /**
   * Creates a new image field template.
   */
  public ImageFieldTemplate() {
    dataRowDataSource = new DataRowDataSource();
    imageLoadFilter = new ImageLoadFilter();
    imageLoadFilter.setDataSource( dataRowDataSource );
    imageRefFilter = new ImageRefFilter();
    imageRefFilter.setDataSource( imageLoadFilter );
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
    return imageRefFilter.getValue( runtime, element );
  }

  /**
   * Clones the template.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public ImageFieldTemplate clone() throws CloneNotSupportedException {
    final ImageFieldTemplate template = (ImageFieldTemplate) super.clone();
    template.imageRefFilter = imageRefFilter.clone();
    template.imageLoadFilter = (ImageLoadFilter) imageRefFilter.getDataSource();
    template.dataRowDataSource = (DataRowDataSource) template.imageLoadFilter.getDataSource();
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
