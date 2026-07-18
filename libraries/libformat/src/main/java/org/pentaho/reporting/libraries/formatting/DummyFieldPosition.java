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



package org.pentaho.reporting.libraries.formatting;

import java.text.FieldPosition;

public class DummyFieldPosition extends FieldPosition {
  public DummyFieldPosition() {
    super( 0 );
  }

  public void clear() {
    setBeginIndex( 0 );
    setEndIndex( 0 );
  }
}
