/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.css.parser.stylehandler.table;

import org.pentaho.reporting.libraries.css.keys.table.BorderCollapse;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 18.07.2006, 19:00:10
 *
 * @author Thomas Morgner
 */
public class BorderCollapseReadHandler extends OneOfConstantsReadHandler {
  public BorderCollapseReadHandler() {
    super( false );
    addValue( BorderCollapse.COLLAPSE );
    addValue( BorderCollapse.SEPARATE );
  }
}
