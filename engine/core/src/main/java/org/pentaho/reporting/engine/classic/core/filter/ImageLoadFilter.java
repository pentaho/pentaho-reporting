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

package org.pentaho.reporting.engine.classic.core.filter;

import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
import java.sql.Blob;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.pentaho.reporting.engine.classic.core.DefaultImageReference;
import org.pentaho.reporting.engine.classic.core.ImageContainer;
import org.pentaho.reporting.engine.classic.core.ReportElement;
import org.pentaho.reporting.engine.classic.core.function.ExpressionRuntime;
import org.pentaho.reporting.libraries.base.util.IOUtils;
import org.pentaho.reporting.libraries.resourceloader.Resource;
import org.pentaho.reporting.libraries.resourceloader.ResourceException;
import org.pentaho.reporting.libraries.resourceloader.ResourceManager;

/**
 * The image load filter is used to load images during the report generation process. This filter expects its datasource
 * to return a java.net.URL. If the datasource does not return an URL, <code>null</code> is returned.
 * <p/>
 * This filter is mostly used in conjunction with the URLFilter, which creates URLs from Strings and files if nessesary.
 * <p/>
 * The url is used to create a new imagereference which is returned to the caller. The loaded/created imagereference is
 * stored in an internal cache.
 * <p/>
 * This filter can be used to dynamically change images of a report, a very nice feature for photo albums and catalogs
 * for instance.
 * <p/>
 * This filter will return null, if something else than an URL was retrieved from the assigned datasource
 *
 * @author Thomas Morgner
 */
public class ImageLoadFilter implements DataFilter {
  private static final Log logger = LogFactory.getLog( ImageLoadFilter.class );
  /**
   * The cache for failed images. This prevents unneccessary retries on known-to-be-buggy URLs.
   */
  private transient HashSet<String> failureCache;
  /**
   * The datasource from where to read the urls.
   */
  private DataSource source;

  /**
   * creates a new ImageLoadFilter with a cache size of 10.
   */
  public ImageLoadFilter() {
    this( 10 );
  }

  /**
   * Creates a new ImageLoadFilter with the defined cache size.
   *
   * @param cacheSize
   *          the cache size.
   */
  public ImageLoadFilter( final int cacheSize ) {
    failureCache = new HashSet<String>( cacheSize );
  }

  /**
   * Reads this filter's datasource and if the source returned an URL, tries to form a imagereference. If the image is
   * loaded in a previous run and is still in the cache, no new reference is created and the previously loaded reference
   * is returned.
   *
   * @param runtime
   *          the expression runtime that is used to evaluate formulas and expressions when computing the value of this
   *          filter.
   * @param element
   * @return the current value for this filter.
   */
  public Object getValue( final ExpressionRuntime runtime, final ReportElement element ) {
    final DataSource ds = getDataSource();
    if ( ds == null ) {
      return null;
    }
    final Object o = ds.getValue( runtime, element );
    if ( o == null ) {
      return null;
    }
    if ( o instanceof byte[] ) {
      try {
        final ResourceManager resManager = runtime.getProcessingContext().getResourceManager();
        final Resource resource = resManager.createDirectly( o, Image.class );
        return new DefaultImageReference( resource );
      } catch ( ResourceException e ) {
        if ( ImageLoadFilter.logger.isDebugEnabled() ) {
          ImageLoadFilter.logger.debug( "Error while loading the image from a blob", e );
        } else if ( ImageLoadFilter.logger.isWarnEnabled() ) {
          ImageLoadFilter.logger.warn( "Error while loading the image from a blob: " + e.getMessage() );
        }
        return null;
      }
    } else if ( o instanceof Blob ) {
      try {
        final Blob b = (Blob) o;
        final byte[] data = IOUtils.getInstance().readBlob( b );
        final ResourceManager resManager = runtime.getProcessingContext().getResourceManager();
        final Resource resource = resManager.createDirectly( data, Image.class );
        return new DefaultImageReference( resource );
      } catch ( Exception e ) {
        if ( ImageLoadFilter.logger.isDebugEnabled() ) {
          ImageLoadFilter.logger.debug( "Error while loading the image from a blob", e );
        } else if ( ImageLoadFilter.logger.isWarnEnabled() ) {
          ImageLoadFilter.logger.warn( "Error while loading the image from a blob: " + e.getMessage() );
        }
        return null;
      }
    } else if ( o instanceof URL ) {
      // a valid url is found, lookup the url in the cache, maybe the image is loaded and
      // still there.
      final URL url = (URL) o;
      final String urlText = String.valueOf( url );
      if ( failureCache.contains( urlText ) ) {
        return null;
      }
      try {
        final ResourceManager resManager = runtime.getProcessingContext().getResourceManager();
        final Resource resource = resManager.createDirectly( url, Image.class );
        return new DefaultImageReference( resource );
      } catch ( ResourceException e ) {
        if ( ImageLoadFilter.logger.isDebugEnabled() ) {
          ImageLoadFilter.logger.debug( "Error while loading the image from " + url, e );
        } else if ( ImageLoadFilter.logger.isWarnEnabled() ) {
          ImageLoadFilter.logger.warn( "Error while loading the image from " + url + ": " + e.getMessage() );
        }
        failureCache.add( urlText );
        return null;
      }
    } else if ( o instanceof Image ) {
      return o;
    } else if ( o instanceof ImageContainer ) {
      return o;
    } else {
      // invalid data or not recognized
      return null;
    }
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
   * Clones the filter.
   *
   * @return a clone.
   * @throws CloneNotSupportedException
   *           this should never happen.
   */
  public ImageLoadFilter clone() throws CloneNotSupportedException {
    final ImageLoadFilter il = (ImageLoadFilter) super.clone();
    if ( source != null ) {
      il.source = source.clone();
    }
    return il;
  }

  /**
   * A helper method that is called during the serialization process.
   *
   * @param out
   *          the serialization output stream.
   * @throws IOException
   *           if an IO error occured.
   */
  private void writeObject( final ObjectOutputStream out ) throws IOException {
    out.defaultWriteObject();
  }

  /**
   * A helper method that is called during the de-serialization process.
   *
   * @param in
   *          the serialization input stream.
   * @throws IOException
   *           if an IOError occurs.
   * @throws ClassNotFoundException
   *           if a dependent class cannot be found.
   */
  private void readObject( final ObjectInputStream in ) throws IOException, ClassNotFoundException {
    in.defaultReadObject();
    failureCache = new HashSet<String>();
  }

}
