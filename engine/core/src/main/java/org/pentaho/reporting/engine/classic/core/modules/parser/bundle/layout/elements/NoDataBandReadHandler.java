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

import org.pentaho.reporting.engine.classic.core.NoDataBand;
import org.pentaho.reporting.engine.classic.core.filter.types.bands.NoDataBandType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

public class NoDataBandReadHandler extends AbstractRootLevelBandReadHandler {
  public NoDataBandReadHandler() throws ParseException {
    super( NoDataBandType.INSTANCE );
  }

  public NoDataBand getElement() {
    return (NoDataBand) super.getElement();
  }
}
