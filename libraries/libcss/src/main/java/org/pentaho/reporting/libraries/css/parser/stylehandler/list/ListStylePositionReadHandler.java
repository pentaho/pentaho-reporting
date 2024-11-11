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


package org.pentaho.reporting.libraries.css.parser.stylehandler.list;

import org.pentaho.reporting.libraries.css.keys.list.ListStylePosition;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 01.12.2005, 19:21:45
 *
 * @author Thomas Morgner
 */
public class ListStylePositionReadHandler extends OneOfConstantsReadHandler {
  public ListStylePositionReadHandler() {
    super( false );
    addValue( ListStylePosition.INSIDE );
    addValue( ListStylePosition.OUTSIDE );
  }
}
