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


package org.pentaho.reporting.libraries.css.parser.stylehandler.border;

import org.pentaho.reporting.libraries.css.keys.border.BackgroundOrigin;
import org.pentaho.reporting.libraries.css.parser.stylehandler.ListOfConstantsReadHandler;

/**
 * Creation-Date: 25.11.2005, 18:00:23
 *
 * @author Thomas Morgner
 */
public class BackgroundOriginReadHandler extends ListOfConstantsReadHandler {
  public BackgroundOriginReadHandler() {
    super( false );
    addValue( BackgroundOrigin.BORDER );
    addValue( BackgroundOrigin.CONTENT );
    addValue( BackgroundOrigin.PADDING );
  }

}
