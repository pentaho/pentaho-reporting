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



package org.pentaho.reporting.libraries.pixie.wmf;

/**
 * Defines a generic WmfObject.
 */
public interface WmfObject {
  public static final int OBJ_PEN = 1;
  public static final int OBJ_BRUSH = 2;
  public static final int OBJ_PALETTE = 3;
  public static final int OBJ_FONT = 4;
  public static final int OBJ_REGION = 5;

  public int getType();
}
