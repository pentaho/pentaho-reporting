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


package org.pentaho.reporting.libraries.css.parser.stylehandler.box;

import org.pentaho.reporting.libraries.css.keys.box.IndentEdgeReset;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 16:03:59
 *
 * @author Thomas Morgner
 */
public class IndentEdgeResetReadHandler extends OneOfConstantsReadHandler {
  public IndentEdgeResetReadHandler() {
    super( false );
    addValue( IndentEdgeReset.BORDER_EDGE );
    addValue( IndentEdgeReset.CONTENT_EDGE );
    addValue( IndentEdgeReset.MARGIN_EDGE );
    addValue( IndentEdgeReset.NONE );
    addValue( IndentEdgeReset.PADDING_EDGE );
  }
}
