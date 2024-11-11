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


package org.pentaho.reporting.libraries.css.parser.stylehandler.box;

import org.pentaho.reporting.libraries.css.keys.box.BoxSizing;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

public class BoxSizingReadHandler extends OneOfConstantsReadHandler {
  public BoxSizingReadHandler() {
    super( false );
    addValue( BoxSizing.BORDER_BOX );
    addValue( BoxSizing.CONTENT_BOX );
  }


}

