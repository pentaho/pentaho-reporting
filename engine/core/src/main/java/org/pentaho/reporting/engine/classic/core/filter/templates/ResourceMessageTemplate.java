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
import org.pentaho.reporting.engine.classic.core.filter.ResourceMessageFormatFilter;
import org.pentaho.reporting.engine.classic.core.filter.StringFilter;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.util.MissingResourceException;

/**
 * A field template that formats values from the datarow using a message format object. The message format string is
 * looked up from a Resource-Bundle.
 *
 * @author Thomas Morgner
 * @since 2006-01-24
 */
public class ResourceMessageTemplate extends AbstractTemplate {
  /**
   * A string filter.
   */
  private StringFilter stringFilter;

  /**
   * A resource file filter.
   */
  private ResourceMessageFormatFilter resourceFilter;

  /**
   * Creates a new template.
   */
  public ResourceMessageTemplate() {
    resourceFilter = new ResourceMessageFormatFilter();
    stringFilter = new StringFilter();
    stringFilter.setDataSource( resourceFilter );
  }

  /**
   * Returns the key that is used to lookup the format string used in the message format in the resource bundle.
   *
   * @return the resource bundle key.
   */
  public String getFormatKey() {
    return resourceFilter.getFormatKey();
  }

  /**
   * Defines the key that is used to lookup the format string used in the message format in the resource bundle.
   *
   * @param formatKey
   *          a resourcebundle key for the message format lookup.
   */
  public void setFormatKey( final String formatKey ) {
    resourceFilter.setFormatKey( formatKey );
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
   *          the resource class name.
   * @throws MissingResourceException
   *           if the resource is missing.
   */
  public void setResourceIdentifier( final String resourceClassName ) throws MissingResourceException {
    resourceFilter.setResourceIdentifier( resourceClassName );
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
   *          the string that represents a <code>null</code> value.
   */
  public void setNullValue( final String nullValue ) {
    stringFilter.setNullValue( nullValue );
    resourceFilter.setNullString( nullValue );
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
  public ResourceMessageTemplate clone() throws CloneNotSupportedException {
    final ResourceMessageTemplate template = (ResourceMessageTemplate) super.clone();
    template.stringFilter = stringFilter.clone();
    template.resourceFilter = (ResourceMessageFormatFilter) template.stringFilter.getDataSource();
    return template;
  }

}
