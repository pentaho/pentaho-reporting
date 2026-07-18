/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 - 2026 by Pentaho Canada Inc. : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2030-06-15
 ******************************************************************************/



package org.pentaho.reporting.libraries.css.resolver.values.computed.text;

import org.pentaho.reporting.libraries.css.keys.text.BlockProgression;
import org.pentaho.reporting.libraries.css.resolver.values.computed.ConstantsResolveHandler;

/**
 * Creation-Date: 21.12.2005, 15:07:34
 *
 * @author Thomas Morgner
 */
public class BlockProgressionResolveHandler extends ConstantsResolveHandler {
  public BlockProgressionResolveHandler() {
    addNormalizeValue( BlockProgression.LR );
    addNormalizeValue( BlockProgression.RL );
    addNormalizeValue( BlockProgression.TB );
    setFallback( BlockProgression.TB );
  }

}
