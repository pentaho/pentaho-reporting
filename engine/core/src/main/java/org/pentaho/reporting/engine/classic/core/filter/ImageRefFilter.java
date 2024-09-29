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

import java.awt.Image;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DefaultImageReference;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;

/**
 * A filter that converts an Image to an ImageReference. The DataSource is expected to contain an java.awt.Image, the
 * image is then wrapped into an ImageReference and this ImageReference is returned to the caller.
 *
 * @author Thomas Morgner
 */
public class ImageRefFilter implements DataFilter {
  private static final Log logger = LogFactory.getLog( ImageRefFilter.class );

  /**
   * Default constructor.
   */
  public ImageRefFilter() {
  }

  /**
   * The data source.
   */
  private DataSource dataSource;

  /**
   * Returns the data source for the filter.
   *
   * @return The data source.
   */
  public DataSource getDataSource() {
    return dataSource;
  }

  /**
   * Sets the data source for the filter.
   *
   * @param dataSource
   *          The data source.
   */
  public void setDataSource( final DataSource dataSource ) {
    this.dataSource = dataSource;
  }

  /**
   * Returns the current value for the data source.
   * <P>
   * The returned object, unless it is null, will be an instance of ImageReference.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return The value.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final DataSource ds = getDataSource();
    if ( ds == null ) {
      return null;
    }
    final Object o = ds.getValue( runtime, element );
    if ( o instanceof ImageContainer ) {
      return o;
    }
    if ( o == null || ( o instanceof Image ) == false ) {
      return null;
    }

    try {
      return new DefaultImageReference( (Image) o );
    } catch ( IOException e ) {
      ImageRefFilter.logger.warn( "Unable to fully load a given image.", e );
      return null;
    }
  }

  /**
   * Clones the filter.
   *
   * @return A clone of this filter.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public ImageRefFilter clone() throws CloneNotSupportedException {
    final ImageRefFilter r = (ImageRefFilter) super.clone();
    if ( dataSource != null ) {
      r.dataSource = dataSource.clone();
    }
    return r;
  }

}
