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
import org.pentaho.reporting.engine.classic.core.filter.ResourceFileFilter;
import org.pentaho.reporting.engine.classic.core.filter.StaticDataSource;
import org.pentaho.reporting.engine.classic.core.filter.StringFilter;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.util.MissingResourceException;

/**
 * A resource label template.
 *
 * @author Thomas Morgner
 */
public class ResourceLabelTemplate extends AbstractTemplate {
  /**
   * A static datasource.
   */
  private StaticDataSource staticDataSource;

  /**
   * A string filter.
   */
  private StringFilter stringFilter;

  /**
   * A resource file filter.
   */
  private ResourceFileFilter resourceFilter;

  /**
   * Creates a new template.
   */
  public ResourceLabelTemplate() {
    staticDataSource = new StaticDataSource();
    resourceFilter = new ResourceFileFilter();
    resourceFilter.setDataSource( staticDataSource );
    stringFilter = new StringFilter();
    stringFilter.setDataSource( resourceFilter );
  }

  /**
   * Returns the resource class name.
   *
   * @return The resource class name.
   */
  public String getResourceIdentifier() {
    return resourceFilter.getResourceIdentifier();
  }

  /**
   * Sets the resource class name.
   *
   * @param resourceClassName
   *          the class name.
   * @throws MissingResourceException
   *           if the resource is missing.
   * @throws NullPointerException
   *           if the resource class name is null.
   */
  public void setResourceIdentifier( final String resourceClassName ) throws MissingResourceException {
    resourceFilter.setResourceIdentifier( resourceClassName );
  }

  /**
   * Sets the content.
   *
   * @param content
   *          the content.
   */
  public void setContent( final String content ) {
    staticDataSource.setValue( content );
  }

  /**
   * Returns the content.
   *
   * @return The content.
   */
  public String getContent() {
    return (String) ( staticDataSource.getValue( null, null ) );
  }

  /**
   * Returns the string that represents a <code>null</code> value.
   *
   * @return The string that represents a <code>null</code> value.
   */
  public String getNullValue() {
    return stringFilter.getNullValue();
  }

  /**
   * Sets the string that represents a <code>null</code> value.
   *
   * @param nullValue
   *          The string that represents a <code>null</code> value.
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
  public ResourceLabelTemplate clone() throws CloneNotSupportedException {
    final ResourceLabelTemplate template = (ResourceLabelTemplate) super.clone();
    template.stringFilter = stringFilter.clone();
    template.resourceFilter = (ResourceFileFilter) template.stringFilter.getDataSource();
    template.staticDataSource = (StaticDataSource) template.resourceFilter.getDataSource();
    return template;
  }
}
