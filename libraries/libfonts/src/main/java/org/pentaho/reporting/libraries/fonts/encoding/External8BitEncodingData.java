/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2029-07-20
 ******************************************************************************/


package org.pentaho.reporting.libraries.fonts.encoding;

/**
 * Creation-Date: 29.04.2006, 14:22:21
 *
 * @author Thomas Morgner
 */
public class External8BitEncodingData extends EncodingData {
  private static final long serialVersionUID = 9017639110342367007L;

  private int[] indexDelta;
  private int[] valueDelta;

  public External8BitEncodingData( final int[] indexDelta,
                                   final int[] valueDelta ) {
    if ( indexDelta == null ) {
      throw new NullPointerException();
    }
    if ( valueDelta == null ) {
      throw new NullPointerException();
    }

    this.indexDelta = indexDelta;
    this.valueDelta = valueDelta;
  }

  public int[] getIndexDelta() {
    return indexDelta;
  }

  public int[] getValueDelta() {
    return valueDelta;
  }

}
