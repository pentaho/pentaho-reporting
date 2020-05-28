/*
 * This program is free software; you can redistribute it and/or modify it under the
 *  terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 *  Foundation.
 *
 *  You should have received a copy of the GNU Lesser General Public License along with this
 *  program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 *  or from the Free Software Foundation, Inc.,
 *  51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 *  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 *  without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 *  See the GNU Lesser General Public License for more details.
 *
 *  Copyright (c) 2006 - 2020 Hitachi Vantara..  All rights reserved.
 */
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
