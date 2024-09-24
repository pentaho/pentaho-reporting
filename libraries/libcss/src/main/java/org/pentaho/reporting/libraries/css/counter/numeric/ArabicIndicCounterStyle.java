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

public class ArabicIndicCounterStyle extends NumericCounterStyle {
  public ArabicIndicCounterStyle() {
    super( 10, "." );
    setReplacementChar( '0', '\u0660' );
    setReplacementChar( '1', '\u0661' );
    setReplacementChar( '2', '\u0662' );
    setReplacementChar( '3', '\u0663' );
    setReplacementChar( '4', '\u0664' );
    setReplacementChar( '5', '\u0665' );
    setReplacementChar( '6', '\u0666' );
    setReplacementChar( '7', '\u0667' );
    setReplacementChar( '8', '\u0668' );
    setReplacementChar( '9', '\u0669' );
  }


}
