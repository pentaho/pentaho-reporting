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

import org.pentaho.reporting.libraries.css.keys.font.FontEmphasizeStyle;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 17:50:19
 *
 * @author Thomas Morgner
 */
public class FontEmphasizeStyleReadHandler extends OneOfConstantsReadHandler {
  public FontEmphasizeStyleReadHandler() {
    super( false );
    addValue( FontEmphasizeStyle.ACCENT );
    addValue( FontEmphasizeStyle.CIRCLE );
    addValue( FontEmphasizeStyle.DISC );
    addValue( FontEmphasizeStyle.DOT );
    addValue( FontEmphasizeStyle.NONE );
  }

}
