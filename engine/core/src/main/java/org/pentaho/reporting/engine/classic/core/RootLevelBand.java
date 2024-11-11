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

/**
 * A RootLevelBand is directly connected with a report definition or a group. RootLevelBands are used as entry points
 * for the content creation.
 *
 * @author Thomas Morgner
 */
public interface RootLevelBand extends ReportElement {
  /**
   * Returns the number of subreports attached to this root level band.
   *
   * @return the number of subreports.
   */
  public int getSubReportCount();

  /**
   * Returns the subreport at the given index-position.
   *
   * @param index
   *          the index
   * @return the subreport stored at the given index.
   * @throws IndexOutOfBoundsException
   *           if there is no such subreport.
   */
  public SubReport getSubReport( int index );

  /**
   * Returns all sub-reports as array.
   *
   * @return the sub-reports as array.
   */
  public SubReport[] getSubReports();
}
