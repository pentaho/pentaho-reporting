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


package org.pentaho.reporting.engine.classic.core.wizard;

import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessorMetaData;

import java.util.Locale;

public interface DataAttributeContext {
  public Locale getLocale();

  public OutputProcessorMetaData getOutputProcessorMetaData();
}
