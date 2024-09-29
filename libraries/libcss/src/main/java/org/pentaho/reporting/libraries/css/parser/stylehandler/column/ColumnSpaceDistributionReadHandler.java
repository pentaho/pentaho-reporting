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


package org.pentaho.reporting.libraries.css.parser.stylehandler.column;

import org.pentaho.reporting.libraries.css.keys.column.ColumnSpaceDistribution;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 08.12.2005, 17:36:20
 *
 * @author Thomas Morgner
 */
public class ColumnSpaceDistributionReadHandler extends OneOfConstantsReadHandler {
  public ColumnSpaceDistributionReadHandler() {
    super( false );
    addValue( ColumnSpaceDistribution.BETWEEN );
    addValue( ColumnSpaceDistribution.END );
    addValue( ColumnSpaceDistribution.INNER );
    addValue( ColumnSpaceDistribution.OUTER );
    addValue( ColumnSpaceDistribution.START );
  }
}
