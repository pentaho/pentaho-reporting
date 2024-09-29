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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.CrosstabHeader;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabHeaderType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

public class CrosstabHeaderBandReadHandler extends BandReadHandler {
  public CrosstabHeaderBandReadHandler() throws ParseException {
    super( CrosstabHeaderType.INSTANCE );
  }

  public CrosstabHeader getElement() {
    return (CrosstabHeader) super.getElement();
  }
}
