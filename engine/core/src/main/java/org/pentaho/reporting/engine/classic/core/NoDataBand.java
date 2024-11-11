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

import org.pentaho.reporting.engine.classic.core.filter.types.bands.NoDataBandType;

/**
 * The No-Data-Band is printed if the current report has no data in its main data-table. It replaces the itemband for
 * such reports.
 *
 * @author Thomas Morgner
 */
public class NoDataBand extends AbstractRootLevelBand {
  /**
   * Constructs a new band.
   */
  public NoDataBand() {
    setElementType( new NoDataBandType() );
  }

  /**
   * Constructs a new band with the given pagebreak attributes. Pagebreak attributes have no effect on subbands.
   *
   * @param pagebreakAfter
   *          defines, whether a pagebreak should be done after that band was printed.
   * @param pagebreakBefore
   *          defines, whether a pagebreak should be done before that band gets printed.
   */
  public NoDataBand( final boolean pagebreakBefore, final boolean pagebreakAfter ) {
    super( pagebreakBefore, pagebreakAfter );
  }
}
