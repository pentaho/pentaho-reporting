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



package org.pentaho.reporting.engine.classic.core.layout.output;

/**
 * A listener that gets informed whenever the layoutsystem generates a pagebreak. This interface should be implemented
 * by the output-function so that it can fire the PageEvents. If you feel the urge to implement that function: GET LOST!
 *
 * @author Thomas Morgner
 */
public interface LayoutPagebreakHandler {
  public void pageFinished();

  public void pageStarted();
}
