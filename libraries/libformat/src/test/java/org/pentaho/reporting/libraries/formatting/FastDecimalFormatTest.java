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

import org.junit.Test;

import java.text.DecimalFormat;
import java.util.Locale;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class FastDecimalFormatTest {

  @Test
  public void formatWithoutResources() {
    FastDecimalFormat fdf = new FastDecimalFormat( FastDecimalFormat.TYPE_PERCENT, Locale.US, false );
    assertEquals( "19%", fdf.format( 0.19408569 ) );
  }

  @Test
  public void formatUsingResources() {
    FastDecimalFormat fdf = new FastDecimalFormat( FastDecimalFormat.TYPE_PERCENT, Locale.US, true );
    assertEquals( "19.41%", fdf.format( 0.19408569 ) );
  }

  @Test
  public void cloneEqualityTest() {
    FastDecimalFormat fdf = new FastDecimalFormat( FastDecimalFormat.TYPE_PERCENT, Locale.US, true );
    Object clone = fdf.clone();
    assertTrue( fdf.equals( clone ) );
  }

  @Test
  public void equalityWithNullTest() {
    FastDecimalFormat fdf = new FastDecimalFormat( FastDecimalFormat.TYPE_PERCENT, Locale.US, true );
    assertFalse( fdf.equals( null ) );
  }

  @Test
  public void equalityDifferentClassTest() {
    FastDecimalFormat fdf = new FastDecimalFormat( FastDecimalFormat.TYPE_PERCENT, Locale.US, true );
    assertFalse( fdf.equals( new DecimalFormat() ) );
  }

  @Test
  public void equalityDifferentLocaleTest() {
    FastDecimalFormat fdf = new FastDecimalFormat( FastDecimalFormat.TYPE_PERCENT, Locale.US, true );
    assertFalse( fdf.equals( new FastDecimalFormat( FastDecimalFormat.TYPE_PERCENT, Locale.UK, true ) ) );
  }

  @Test
  public void equalityDifferentFormatTest() {
    FastDecimalFormat fdf = new FastDecimalFormat( FastDecimalFormat.TYPE_PERCENT, Locale.US, true );
    assertFalse( fdf.equals( new FastDecimalFormat( FastDecimalFormat.TYPE_PERCENT, Locale.US, false ) ) );
  }
}
