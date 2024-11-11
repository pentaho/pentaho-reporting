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


package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.CrosstabTitleHeader;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.CrosstabTitleHeaderType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

public class CrosstabTitleHeaderBandReadHandler extends BandReadHandler {
  public CrosstabTitleHeaderBandReadHandler() throws ParseException {
    super( CrosstabTitleHeaderType.INSTANCE );
  }

  public CrosstabTitleHeader getElement() {
    return (CrosstabTitleHeader) super.getElement();
  }
}
