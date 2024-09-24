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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabHeaderType;
import org.pentaho.reporting.engine.classic.core.style.BandDefaultStyleSheet;
import org.pentaho.reporting.engine.classic.core.style.ElementStyleSheet;

/**
 * A details header is printed between the last group-header and the first detail band. The header is printed on the
 * itemsStarted(..) event. A details-header cannot carry subreports.
 * <p/>
 * This behavior can be easily changed with a style-expression on the visible-style-property. ("=NOT(ISEMPTYDATA())"
 *
 * @author Thomas Morgner
 */
public class CrosstabHeader extends Band implements RootLevelBand {
  /**
   * A helper array to prevent unnecessary object creation.
   */
  private static final SubReport[] EMPTY_SUB_REPORTS = new SubReport[0];

  /**
   * Constructs a new band (initially empty).
   */
  public CrosstabHeader() {
    setElementType( new CrosstabHeaderType() );
  }

  /**
   * Returns the number of subreports on this band. This returns zero, as page-bands cannot have subreports.
   *
   * @return the subreport count.
   */
  public final int getSubReportCount() {
    return 0;
  }

  /**
   * Throws an IndexOutOfBoundsException as page-footer cannot have sub-reports.
   *
   * @param index
   *          the index.
   * @return nothing, as an exception is thrown instead.
   */
  public final SubReport getSubReport( final int index ) {
    throw new IndexOutOfBoundsException( "DetailsHeader cannot have subreports" );
  }

  /**
   * Returns an empty array, as page-footer cannot have subreports.
   *
   * @return the sub-reports as array.
   */
  public SubReport[] getSubReports() {
    return CrosstabHeader.EMPTY_SUB_REPORTS;
  }

  public ElementStyleSheet getDefaultStyleSheet() {
    return BandDefaultStyleSheet.getBandDefaultStyle();
  }
}
