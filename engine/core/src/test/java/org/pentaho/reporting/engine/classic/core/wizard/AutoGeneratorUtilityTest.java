/*!
 * This program is free software; you can redistribute it and/or modify it under the
 * terms of the GNU Lesser General Public License, version 2.1 as published by the Free Software
 * Foundation.
 *
 * You should have received a copy of the GNU Lesser General Public License along with this
 * program; if not, you can obtain a copy at http://www.gnu.org/licenses/old-licenses/lgpl-2.1.html
 * or from the Free Software Foundation, Inc.,
 * 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Lesser General Public License for more details.
 *
 * Copyright (c) 2002-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.wizard;

import junit.framework.TestCase;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;

public class AutoGeneratorUtilityTest extends TestCase {
  public AutoGeneratorUtilityTest() {
  }

  public AutoGeneratorUtilityTest( final String s ) {
    super( s );
  }

  protected void setUp() throws Exception {
    ClassicEngineBoot.getInstance().start();
  }

  public void testComputeFieldWidths() {
    final Float[] allEmpty = new Float[5];
    final float[] floats = AutoGeneratorUtility.computeFieldWidths( allEmpty, 500 );
    for ( int i = 0; i < floats.length; i++ ) {
      final float aFloat = floats[i];
      assertEquals( -20, aFloat, 0 );
    }

    final Float[] allRelative = new Float[5];
    allRelative[0] = (float) -20;
    allRelative[1] = (float) -20;
    allRelative[2] = (float) -20;
    allRelative[3] = (float) -20;
    allRelative[4] = (float) -20;
    final float[] result2 = AutoGeneratorUtility.computeFieldWidths( allRelative, 500 );
    for ( int i = 0; i < result2.length; i++ ) {
      final float aFloat = result2[i];
      assertEquals( -20, aFloat, 0 );
    }

    final Float[] allStatic = new Float[5];
    allStatic[0] = (float) 100;
    allStatic[1] = (float) 100;
    allStatic[2] = (float) 100;
    allStatic[3] = (float) 100;
    allStatic[4] = (float) 100;
    final float[] result3 = AutoGeneratorUtility.computeFieldWidths( allStatic, 500 );
    for ( int i = 0; i < result3.length; i++ ) {
      final float aFloat = result3[i];
      assertEquals( -20, aFloat, 0 );
    }

    final Float[] allMixed = new Float[5];
    allMixed[0] = (float) 100;
    allMixed[1] = (float) 100;
    allMixed[2] = (float) -20;
    allMixed[3] = (float) -20;
    allMixed[4] = null;
    final float[] result4 = AutoGeneratorUtility.computeFieldWidths( allMixed, 500 );
    for ( int i = 0; i < result4.length; i++ ) {
      final float aFloat = result4[i];
      assertEquals( -20, aFloat, 0 );
    }

  }

  public void testComputeFieldWidthsNoSpaceLeft() {
    final Float[] allEmpty = new Float[5];
    final float[] floats = AutoGeneratorUtility.computeFieldWidths( allEmpty, 400 );
    for ( int i = 0; i < floats.length; i++ ) {
      final float aFloat = floats[i];
      assertEquals( -20, aFloat, 0 );
    }

    final Float[] allRelative = new Float[5];
    allRelative[0] = (float) -20;
    allRelative[1] = (float) -20;
    allRelative[2] = (float) -20;
    allRelative[3] = (float) -20;
    allRelative[4] = (float) -20;
    final float[] result2 = AutoGeneratorUtility.computeFieldWidths( allRelative, 400 );
    for ( int i = 0; i < result2.length; i++ ) {
      final float aFloat = result2[i];
      assertEquals( -20, aFloat, 0 );
    }

    final Float[] allStatic = new Float[5];
    allStatic[0] = (float) 100;
    allStatic[1] = (float) 100;
    allStatic[2] = (float) 100;
    allStatic[3] = (float) 100;
    allStatic[4] = (float) 100;
    final float[] result3 = AutoGeneratorUtility.computeFieldWidths( allStatic, 400 );
    for ( int i = 0; i < result3.length; i++ ) {
      final float aFloat = result3[i];
      assertEquals( -25, aFloat, 0 );
    }

    final Float[] allMixed = new Float[5];
    allMixed[0] = (float) 100;
    allMixed[1] = (float) 100;
    allMixed[2] = (float) -20;
    allMixed[3] = (float) -20;
    allMixed[4] = null;
    final float[] result4 = AutoGeneratorUtility.computeFieldWidths( allMixed, 400 );
    assertEquals( ( -25 ), result4[0], 0 );
    assertEquals( ( -25 ), result4[1], 0 );
    assertEquals( ( -20 ), result4[2], 0 );
    assertEquals( ( -20 ), result4[3], 0 );
    assertEquals( ( -10 ), result4[4], 0 );

  }
}
