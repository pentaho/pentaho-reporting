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



package org.pentaho.reporting.engine.classic.demo.util;

/**
 * A PreviewHandler knows, what actions should be taken when previewing a report.
 *
 * @author Thomas Morgner
 */
public interface PreviewHandler
{
  public void attemptPreview();
}
