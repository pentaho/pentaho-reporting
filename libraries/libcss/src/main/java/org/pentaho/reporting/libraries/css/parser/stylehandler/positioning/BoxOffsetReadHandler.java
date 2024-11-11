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


package org.pentaho.reporting.libraries.css.parser.stylehandler.positioning;

import org.pentaho.reporting.libraries.css.parser.stylehandler.AbstractWidthReadHandler;

/**
 * Handles the 'top', 'left', 'bottom', 'right' properties, which are needed for absolutly positioned content.
 *
 * @author Thomas Morgner
 */
public class BoxOffsetReadHandler extends AbstractWidthReadHandler {
  public BoxOffsetReadHandler() {
    super( true, true );
  }


}
