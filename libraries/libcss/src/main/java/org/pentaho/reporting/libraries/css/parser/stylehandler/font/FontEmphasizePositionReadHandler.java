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


package org.pentaho.reporting.libraries.css.parser.stylehandler.font;

import org.pentaho.reporting.libraries.css.keys.font.FontEmphasizePosition;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 17:50:19
 *
 * @author Thomas Morgner
 */
public class FontEmphasizePositionReadHandler extends OneOfConstantsReadHandler {
  public FontEmphasizePositionReadHandler() {
    super( false );
    addValue( FontEmphasizePosition.AFTER );
    addValue( FontEmphasizePosition.ABOVE );
    addValue( FontEmphasizePosition.BELOW );
    addValue( FontEmphasizePosition.BEFORE );
  }

}
