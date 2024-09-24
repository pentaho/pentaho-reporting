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

package org.pentaho.reporting.engine.classic.core.filter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * The URLFilter forms URLs from Strings ,Files and URLs. If an URL is relative, the missing contents can be obtained by
 * a default url, called the baseURL.
 * <p/>
 *
 * @author Thomas Morgner
 */
public class URLFilter implements DataFilter {
  private static final Log logger = LogFactory.getLog( URLFilter.class );
  /**
   * The datasource used to form the urls. This datasource should return strings, files or urls
   */
  private DataSource source;

  /**
   * The base url is used to form the complete url if the given url is relative.
   *
   * @see java.net.URL#URL(java.net.URL, java.lang.String)
   */
  private URL baseURL;

  /**
   * DefaultConstructor.
   */
  public URLFilter() {
  }

  /**
   * Returns the data source for the filter.
   *
   * @return The data source.
   */
  public DataSource getDataSource() {
    return source;
  }

  /**
   * Sets the data source.
   *
   * @param ds
   *          The data source.
   */
  public void setDataSource( final DataSource ds ) {
    if ( ds == null ) {
      throw new NullPointerException();
    }

    source = ds;
  }

  /**
   * Tries to form a url from the object returned from the datasource. This function will return null if the datasource
   * is null or returned null when getValue was called.
   * <p/>
   * Null is also returned if the datasources value is not an url, a String or a file. If the creation of the url failed
   * with an MalformedURLException or the datasource returned a file which is not readable, also null is returned.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return created url or null if something went wrong on url creation.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    if ( getDataSource() == null ) {
      return null;
    }

    final Object o = getDataSource().getValue( runtime, element );
    if ( o == null ) {
      return null;
    }
    if ( o instanceof URL ) {
      return o;
    }

    try {
      if ( o instanceof File ) {
        final File f = (File) o;
        if ( f.canRead() ) {
          return f.toURL();
        }
      } else if ( o instanceof String ) {
        if ( getBaseURL() == null ) {
          return new URL( (String) o );
        } else {
          return new URL( getBaseURL(), (String) o );
        }
      }
    } catch ( MalformedURLException mfe ) {
      URLFilter.logger.info( "URLFilter.getValue(): MalformedURLException!" );
    }
    return null;

  }

  /**
   * Gets the base url used to make relative URLs absolute.
   *
   * @return the base url used to complete relative urls.
   */
  public URL getBaseURL() {
    return baseURL;
  }

  /**
   * Defines the base url used to complete relative urls.
   *
   * @param baseURL
   *          the base URL.
   */
  public void setBaseURL( final URL baseURL ) {
    this.baseURL = baseURL;
  }

  /**
   * Creates a clone of the URL filter.
   *
   * @return A clone.
   * @throws CloneNotSupportedException
   *           should never happen.
   */
  public URLFilter clone() throws CloneNotSupportedException {
    final URLFilter f = (URLFilter) super.clone();
    if ( source != null ) {
      f.source = source.clone();
    }
    return f;
  }

}
