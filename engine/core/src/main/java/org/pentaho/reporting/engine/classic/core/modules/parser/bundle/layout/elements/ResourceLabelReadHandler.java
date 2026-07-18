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

import org.pentaho.reporting.engine.classic.core.filter.types.ResourceLabelType;
import org.pentaho.reporting.libraries.xmlns.parser.ParseException;

@Deprecated
public class ResourceLabelReadHandler extends AbstractElementReadHandler {
  public ResourceLabelReadHandler() throws ParseException {
    super( ResourceLabelType.INSTANCE );
  }
}
