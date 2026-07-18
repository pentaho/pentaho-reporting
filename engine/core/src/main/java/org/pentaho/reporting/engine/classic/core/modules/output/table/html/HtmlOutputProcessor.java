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



package org.pentaho.reporting.engine.classic.core.modules.output.table.html;

import org.pentaho.reporting.engine.classic.core.layout.output.OutputProcessor;

/**
 * Creation-Date: 04.05.2007, 18:56:36
 *
 * @author Thomas Morgner
 */
public interface HtmlOutputProcessor extends OutputProcessor {
  public HtmlPrinter getPrinter();

  public void setPrinter( HtmlPrinter printer );

}
