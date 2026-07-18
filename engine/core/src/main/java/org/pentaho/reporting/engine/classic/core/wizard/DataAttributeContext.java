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



package org.pentaho.reporting.engine.classic.core.wizard;

import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;

import java.util.Locale;

public interface DataAttributeContext {
  public Locale getLocale();

  public OutputProcessorMetaData getOutputProcessorMetaData();
}
