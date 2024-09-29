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

import org.pentaho.reporting.libraries.css.parser.stylehandler.AbstractWidthReadHandler;

/**
 * Creation-Date: 27.11.2005, 21:44:40
 *
 * @author Thomas Morgner
 */
public class MaxWidthAndHeightReadHandler extends AbstractWidthReadHandler {
  public MaxWidthAndHeightReadHandler() {
    super( true, true );
  }
}
