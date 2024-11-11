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


package org.pentaho.reporting.engine.classic.extensions.modules.sparklines.xml;

import org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements.AbstractElementReadHandler;
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.PieSparklineType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

public class PieSparklineElementReadHandler extends AbstractElementReadHandler {
  public PieSparklineElementReadHandler() throws ParseException {
    super( new PieSparklineType() );
  }
}
