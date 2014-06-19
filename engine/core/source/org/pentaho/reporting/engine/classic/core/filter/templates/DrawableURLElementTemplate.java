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
* Copyright (c) 2001 - 2013 Object Refinery Ltd, Pentaho Corporation and Contributors..  All rights reserved.
*/

package org.pentaho.reporting.engine.classic.core.filter.templates;

import java.net.URL;

import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.filter.DrawableLoadFilter;
import org.pentaho.reporting.engine.classic.core.filter.StaticDataSource;
import org.pentaho.reporting.engine.classic.core.filter.URLFilter;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

/**
 * An image URL element template, which reads the image from a static URL.
 *
 * @author Thomas Morgner
 */
public class DrawableURLElementTemplate extends AbstractTemplate
{
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
  public DrawableURLElementTemplate()
  {
    staticDataSource = new StaticDataSource();
    urlFilter = new URLFilter();
    urlFilter.setDataSource(staticDataSource);
    imageLoadFilter = new DrawableLoadFilter();
    imageLoadFilter.setDataSource(urlFilter);
  }

  /**
   * Sets the URL for the template.
   *
   * @param content the URL.
   */
  public void setContent(final String content)
  {
    staticDataSource.setValue(content);
  }

  /**
   * Returns the URL text for the template.
   *
   * @return The URL text.
   */
  public String getContent()
  {
    return (String) (staticDataSource.getValue(null, null));
  }

  /**
   * Returns the base URL.
   *
   * @return The URL.
   */
  public URL getBaseURL()
  {
    return urlFilter.getBaseURL();
  }

  /**
   * Sets the base URL.
   *
   * @param baseURL the URL.
   */
  public void setBaseURL(final URL baseURL)
  {
    urlFilter.setBaseURL(baseURL);
  }

  /**
   * Returns the current value for the data source.
   *
   * @param runtime the expression runtime that is used to evaluate formulas and expressions when computing the value of
   *                this filter.
   * @param element
   * @return the value.
   */
  public Object getValue(final ExpressionRuntime runtime, final ReportElement element)
  {
    return imageLoadFilter.getValue(runtime, element);
  }

  /**
   * Clones the template.
   *
   * @return the clone.
   * @throws CloneNotSupportedException this should never happen.
   */
  public DrawableURLElementTemplate clone()
      throws CloneNotSupportedException
  {
    final DrawableURLElementTemplate template = (DrawableURLElementTemplate) super.clone();
    template.imageLoadFilter = imageLoadFilter.clone();
    template.urlFilter = (URLFilter) template.imageLoadFilter.getDataSource();
    template.staticDataSource = (StaticDataSource) template.urlFilter.getDataSource();
    return template;
  }

}
