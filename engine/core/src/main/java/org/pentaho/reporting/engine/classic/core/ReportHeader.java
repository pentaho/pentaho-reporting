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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.ReportHeaderType;

/**
 * A report band that is printed once only at the beginning of the report.
 * <p/>
 * A flag can be set forcing the report generator to start a new page after printing the report header.
 * <p/>
 * Note that if there is a page header on the first page of your report, it will be printed above the report header, the
 * logic being that the page header *always* appears at the top of the page. In many cases, it makes better sense to
 * suppress the page header on the first page of the report (leaving just the report header on page 1).
 *
 * @author David Gilbert
 * @author Thomas Morgner
 */
public class ReportHeader extends AbstractRootLevelBand {
  /**
   * Constructs a report header, initially containing no elements.
   */
  public ReportHeader() {
    setElementType( new ReportHeaderType() );
  }
}
