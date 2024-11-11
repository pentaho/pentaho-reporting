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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.ReportFooterType;

/**
 * A report band that appears as the very last band on the report.
 * <p/>
 * Note that if there is a page footer on the last page of your report, it will be printed below the report footer, the
 * logic being that the page footer *always* appears at the bottom of the page. In many cases, it makes better sense to
 * suppress the page footer on the last page of the report (leaving just the report footer on the final page).
 *
 * @author David Gilbert
 */
public class ReportFooter extends AbstractRootLevelBand {
  /**
   * Constructs a report footer containing no elements.
   */
  public ReportFooter() {
    setElementType( new ReportFooterType() );
  }

}
