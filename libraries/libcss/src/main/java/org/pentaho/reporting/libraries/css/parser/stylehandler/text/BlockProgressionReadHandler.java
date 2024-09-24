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

package org.pentaho.reporting.libraries.css.parser.stylehandler.text;

import org.pentaho.reporting.libraries.css.keys.text.BlockProgression;
import org.pentaho.reporting.libraries.css.parser.stylehandler.OneOfConstantsReadHandler;

/**
 * Creation-Date: 02.12.2005, 17:37:41
 *
 * @author Thomas Morgner
 */
public class BlockProgressionReadHandler extends OneOfConstantsReadHandler {
  public BlockProgressionReadHandler() {
    super( false );
    addValue( BlockProgression.LR );
    addValue( BlockProgression.RL );
    addValue( BlockProgression.TB );
  }
}
