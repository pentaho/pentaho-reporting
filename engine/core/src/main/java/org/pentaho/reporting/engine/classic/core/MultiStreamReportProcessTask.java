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

import org.pentaho.reporting.engine.classic.core.modules.output.table.html.URLRewriter;
import org.pentaho.reporting.libraries.repository.ContentLocation;
import org.pentaho.reporting.libraries.repository.NameGenerator;

/**
 * A export task that generates multiple streams, like the HTML-output does. The report document itself will go into the
 * <code>bodyLocation</code>, while any additional files generated during the export go into the bulk location. Be aware
 * that if the export generates multiple report-documents (ie. multiple HTML files) then they all go into the
 * body-location, while any image or stylesheet goes into the bulk location. </p> If no bulk location is given, then no
 * extra files shall be generated.
 *
 * @author Thomas Morgner
 */
public interface MultiStreamReportProcessTask extends ReportProcessTask {
  /**
   * Defines the bulk location, where additional content can be stored.
   *
   * @param bulkLocation
   *          the bulk location.
   */
  public void setBulkLocation( final ContentLocation bulkLocation );

  /**
   * Returns the defined bulk location for the export.
   *
   * @return the bulk location.
   */
  public ContentLocation getBulkLocation();

  /**
   * Defines the bulk file name generator that is used to generate unique names for the exported files. If a bulk
   * location is given, this property must not be null.
   *
   * @param bulkNameGenerator
   *          the name generator.
   */
  public void setBulkNameGenerator( final NameGenerator bulkNameGenerator );

  /**
   * Returns the name generator for bulk content.
   *
   * @return the bulk file name generator.
   */
  public NameGenerator getBulkNameGenerator();

  /**
   * Defines the URL rewriter that is used to link bulk items to the main document.
   *
   * @param urlRewriter
   *          the URL rewriter used in the export.
   */
  public void setUrlRewriter( final URLRewriter urlRewriter );

  /**
   * Returns the URL rewriter used during the export.
   *
   * @return the URL rewriter that is used to generate or alter URLs pointing to bulk items.
   */
  public URLRewriter getUrlRewriter();
}
