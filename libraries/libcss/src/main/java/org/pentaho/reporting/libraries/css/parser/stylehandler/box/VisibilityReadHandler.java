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

import org.pentaho.reporting.libraries.css.keys.box.Visibility;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 16:13:54
 *
 * @author Thomas Morgner
 */
public class VisibilityReadHandler extends OneOfConstantsReadHandler {
  public VisibilityReadHandler() {
    super( false );
    addValue( Visibility.COLLAPSE );
    addValue( Visibility.HIDDEN );
    addValue( Visibility.VISIBLE );
  }
}
