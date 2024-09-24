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
import org.pentaho.reporting.engine.classic.extensions.modules.sparklines.LineSparklineType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

/**
 * A read handler that produces line-sparkline elements. As the attributes and style is already handled in the abstract
 * super-class, there is no need to add any other implementation here.
 *
 * @author Thomas Morgner
 */
public class LineSparklineElementReadHandler extends AbstractElementReadHandler {
  public LineSparklineElementReadHandler() throws ParseException {
    super( new LineSparklineType() );
  }
}
