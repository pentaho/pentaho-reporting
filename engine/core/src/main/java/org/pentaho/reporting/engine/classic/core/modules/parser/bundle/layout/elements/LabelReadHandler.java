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



package org.pentaho.reporting.engine.classic.core.modules.parser.bundle.layout.elements;

import org.pentaho.reporting.engine.classic.core.filter.types.LabelType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

@Deprecated
public class LabelReadHandler extends AbstractElementReadHandler {
  public LabelReadHandler() throws ParseException {
    super( LabelType.INSTANCE );
  }
}
