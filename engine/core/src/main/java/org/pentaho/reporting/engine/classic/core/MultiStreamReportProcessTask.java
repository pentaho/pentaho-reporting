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
