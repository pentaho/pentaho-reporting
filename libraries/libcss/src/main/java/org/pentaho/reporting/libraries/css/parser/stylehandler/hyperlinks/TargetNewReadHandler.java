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


package org.pentaho.reporting.libraries.css.parser.stylehandler.hyperlinks;

import org.pentaho.reporting.libraries.css.keys.hyperlinks.TargetNew;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 19:32:18
 *
 * @author Thomas Morgner
 */
public class TargetNewReadHandler extends OneOfConstantsReadHandler {
  public TargetNewReadHandler() {
    super( false );
    addValue( TargetNew.NONE );
    addValue( TargetNew.TAB );
    addValue( TargetNew.WINDOW );
  }
}
