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


package org.pentaho.reporting.libraries.css.counter.numeric;

public class BengaliCounterStyle extends NumericCounterStyle {
  public BengaliCounterStyle() {
    super( 10, "." );
    setReplacementChar( '0', '\u09e6' );
    setReplacementChar( '1', '\u09e7' );
    setReplacementChar( '2', '\u09e8' );
    setReplacementChar( '3', '\u09e9' );
    setReplacementChar( '4', '\u09ea' );
    setReplacementChar( '5', '\u09eb' );
    setReplacementChar( '6', '\u09ec' );
    setReplacementChar( '7', '\u09ed' );
    setReplacementChar( '8', '\u09ee' );
    setReplacementChar( '9', '\u09ef' );
  }


}
