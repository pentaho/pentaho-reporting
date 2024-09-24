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
import org.pentaho.reporting.engine.classic.core.filter.StaticDataSource;
import org.pentaho.reporting.engine.classic.core.filter.StringFilter;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

/**
 * A label template can be used to describe static text content.
 *
 * @author Thomas Morgner
 */
public class LabelTemplate extends AbstractTemplate {
  /**
   * A static data source.
   */
  private StaticDataSource staticDataSource;

  /**
   * A string filter.
   */
  private StringFilter stringFilter;

  /**
   * Creates a new label template.
   */
  public LabelTemplate() {
    staticDataSource = new StaticDataSource();
    stringFilter = new StringFilter();
    stringFilter.setDataSource( staticDataSource );
  }

  /**
   * Sets the text for the label.
   *
   * @param content
   *          the text.
   */
  public void setContent( final String content ) {
    staticDataSource.setValue( content );
  }

  /**
   * Returns the text for the label.
   *
   * @return The text.
   */
  public String getContent() {
    return (String) ( staticDataSource.getValue( null, null ) );
  }

  /**
   * Returns the string that represents <code>null</code>.
   *
   * @return The string that represents <code>null</code>.
   */
  public String getNullValue() {
    return stringFilter.getNullValue();
  }

  /**
   * Sets the string that represents <code>null</code>.
   *
   * @param nullValue
   *          the string.
   */
  public void setNullValue( final String nullValue ) {
    stringFilter.setNullValue( nullValue );
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
    return stringFilter.getValue( runtime, element );
  }

  /**
   * Clones the template.
   *
   * @return the clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public LabelTemplate clone() throws CloneNotSupportedException {
    final LabelTemplate template = (LabelTemplate) super.clone();
    template.stringFilter = stringFilter.clone();
    template.staticDataSource = (StaticDataSource) template.stringFilter.getDataSource();
    return template;
  }

}
