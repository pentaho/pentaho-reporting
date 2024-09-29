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


package org.pentaho.reporting.libraries.css.parser.stylehandler.line;

import org.pentaho.reporting.libraries.css.keys.line.LineStackingRuby;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 28.11.2005, 18:02:53
 *
 * @author Thomas Morgner
 */
public class LineStackingRubyReadHandler extends OneOfConstantsReadHandler {
  public LineStackingRubyReadHandler() {
    super( true );
    addValue( LineStackingRuby.EXCLUDE_RUBY );
    addValue( LineStackingRuby.INCLUDE_RUBY );
  }
}
