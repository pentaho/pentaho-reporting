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
