/*!
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
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.modules.output.table.html.FileSystemURLRewriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.SingleRepositoryURLRewriter;
import org.pentaho.reporting.engine.classic.core.modules.output.table.html.URLRewriter;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;
import org.pentaho.reporting.libraries.repository.Repository;
import org.pentaho.reporting.libraries.repository.UrlRepository;

/**
 * TA common base class for {@link org.pentaho.reporting.engine.classic.core.MultiStreamReportProcessTask}
 * implementations.
 *
 * @author Thomas Morgner
 */
public abstract class AbstractMultiStreamReportProcessTask extends AbstractReportProcessTask implements
    MultiStreamReportProcessTask {
  private ContentLocation bulkLocation;
  private NameGenerator bulkNameGenerator;
  private URLRewriter urlRewriter;

  /**
   * Default Constructor.
   */
  protected AbstractMultiStreamReportProcessTask() {
  }

  /**
   * Returns the defined bulk location for the export.
   *
   * @return the bulk location.
   */
  public ContentLocation getBulkLocation() {
    return bulkLocation;
  }

  /**
   * Defines the bulk location, where additional content can be stored.
   *
   * @param bulkLocation
   *          the bulk location.
   */
  public void setBulkLocation( final ContentLocation bulkLocation ) {
    this.bulkLocation = bulkLocation;
  }

  /**
   * Returns the name generator for bulk content.
   *
   * @return the bulk file name generator.
   */
  public NameGenerator getBulkNameGenerator() {
    return bulkNameGenerator;
  }

  /**
   * Defines the bulk file name generator that is used to generate unique names for the exported files. If a bulk
   * location is given, this property must not be null.
   *
   * @param bulkNameGenerator
   *          the name generator.
   */
  public void setBulkNameGenerator( final NameGenerator bulkNameGenerator ) {
    this.bulkNameGenerator = bulkNameGenerator;
  }

  /**
   * Returns the URL rewriter used during the export.
   *
   * @return the URL rewriter that is used to generate or alter URLs pointing to bulk items.
   */
  public URLRewriter getUrlRewriter() {
    return urlRewriter;
  }

  /**
   * Defines the URL rewriter that is used to link bulk items to the main document.
   *
   * @param urlRewriter
   *          the URL rewriter used in the export.
   */
  public void setUrlRewriter( final URLRewriter urlRewriter ) {
    this.urlRewriter = urlRewriter;
  }

  /**
   * A helper method that tries to come up with a reasonalbe URLrewriter for common repository configurations. If there
   * is a URLRewriter defined already, that one will be used. If both the bulk and body location point to the same
   * repository backend, the SingleRepositoryURLRewriter is used. If both repositories are different but both are
   * UrlRepositories, a FilesystemURLRewriter is used. If everything else fails, the method will fall back to a
   * SingleRepositoryURLRewriter - hoping that it will work out.
   *
   * @return the computed URL rewriter.
   */
  protected URLRewriter computeUrlRewriter() {
    final URLRewriter userRewriter = getUrlRewriter();
    if ( userRewriter != null ) {
      return ( userRewriter );
    } else {
      final ContentLocation bulkLocation = getBulkLocation();
      if ( bulkLocation == null ) {
        return ( new SingleRepositoryURLRewriter() );
      } else {
        final Repository bulkRepository = bulkLocation.getRepository();
        final Repository bodyRepository = getBodyContentLocation().getRepository();
        if ( bulkRepository == bodyRepository ) {
          return ( new SingleRepositoryURLRewriter() );
        } else if ( bulkRepository instanceof UrlRepository && bodyRepository instanceof UrlRepository ) {
          return ( new FileSystemURLRewriter() );
        } else {
          return ( new SingleRepositoryURLRewriter() );
        }
      }
    }
  }

}
