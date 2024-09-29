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
