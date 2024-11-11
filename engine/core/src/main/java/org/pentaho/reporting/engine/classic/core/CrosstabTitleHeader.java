/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.engine.classic.core;

import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabTitleHeaderType;
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
public class CrosstabTitleHeader extends Band implements RootLevelBand {
  /**
   * A helper array to prevent unnecessary object creation.
   */
  private static final SubReport[] EMPTY_SUB_REPORTS = new SubReport[0];

  /**
   * Constructs a new band (initially empty).
   */
  public CrosstabTitleHeader() {
    setElementType( new CrosstabTitleHeaderType() );
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
    return CrosstabTitleHeader.EMPTY_SUB_REPORTS;
  }

  public ElementStyleSheet getDefaultStyleSheet() {
    return BandDefaultStyleSheet.getBandDefaultStyle();
  }

}
