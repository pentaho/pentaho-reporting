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

package org.pentaho.reporting.libraries.css.counter.numeric;

public class DevanagariCounterStyle extends NumericCounterStyle {
  public DevanagariCounterStyle() {
    super( 10, "." );
    setReplacementChar( '0', '\u0966' );
    setReplacementChar( '1', '\u0967' );
    setReplacementChar( '2', '\u0968' );
    setReplacementChar( '3', '\u0969' );
    setReplacementChar( '4', '\u096A' );
    setReplacementChar( '5', '\u096b' );
    setReplacementChar( '6', '\u096c' );
    setReplacementChar( '7', '\u096d' );
    setReplacementChar( '8', '\u096e' );
    setReplacementChar( '9', '\u096f' );
  }


}
