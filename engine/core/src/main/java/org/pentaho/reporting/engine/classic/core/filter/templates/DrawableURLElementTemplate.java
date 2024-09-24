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
import org.pentaho.reporting.engine.classic.core.filter.DrawableLoadFilter;
import org.pentaho.reporting.engine.classic.core.filter.StaticDataSource;
import org.pentaho.reporting.engine.classic.core.filter.URLFilter;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.net.URL;

/**
 * An image URL element template, which reads the image from a static URL.
 *
 * @author Thomas Morgner
 */
public class DrawableURLElementTemplate extends AbstractTemplate {
  /**
   * The image load filter.
   */
  private DrawableLoadFilter imageLoadFilter;

  /**
   * A static datasource.
   */
  private StaticDataSource staticDataSource;

  /**
   * A URL filter.
   */
  private URLFilter urlFilter;

  /**
   * Creates a new template.
   */
  public DrawableURLElementTemplate() {
    staticDataSource = new StaticDataSource();
    urlFilter = new URLFilter();
    urlFilter.setDataSource( staticDataSource );
    imageLoadFilter = new DrawableLoadFilter();
    imageLoadFilter.setDataSource( urlFilter );
  }

  /**
   * Sets the URL for the template.
   *
   * @param content
   *          the URL.
   */
  public void setContent( final String content ) {
    staticDataSource.setValue( content );
  }

  /**
   * Returns the URL text for the template.
   *
   * @return The URL text.
   */
  public String getContent() {
    return (String) ( staticDataSource.getValue( null, null ) );
  }

  /**
   * Returns the base URL.
   *
   * @return The URL.
   */
  public URL getBaseURL() {
    return urlFilter.getBaseURL();
  }

  /**
   * Sets the base URL.
   *
   * @param baseURL
   *          the URL.
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
  public DrawableURLElementTemplate clone() throws CloneNotSupportedException {
    final DrawableURLElementTemplate template = (DrawableURLElementTemplate) super.clone();
    template.imageLoadFilter = imageLoadFilter.clone();
    template.urlFilter = (URLFilter) template.imageLoadFilter.getDataSource();
    template.staticDataSource = (StaticDataSource) template.urlFilter.getDataSource();
    return template;
  }

}
