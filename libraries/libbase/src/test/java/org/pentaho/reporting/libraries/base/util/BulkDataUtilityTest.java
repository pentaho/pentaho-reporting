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

package org.pentaho.reporting.libraries.base.util;

import junit.framework.TestCase;

import java.util.Arrays;

public class BulkDataUtilityTest extends TestCase {

  public BulkDataUtilityTest() {
  }

  public BulkDataUtilityTest( final String s ) {
    super( s );
  }

  public void testPushUp() {
    final String[] vals1 = new String[] { "1", "2", "3", "4", "5", "6" };
    final String[] vals2 = new String[] { "1", "2", "3", "4", "5", "6" };
    final String[] result1 = new String[] { "1", "3", "4", "2", "6", "5" };
    final String[] result2 = new String[] { "2", "3", "1", "4", "5", "6" };
    final boolean[] sels1 = new boolean[] { true, false, true, true, false, true };
    final boolean[] sels2 = new boolean[] { false, true, true, false, false, false };
    final boolean[] ressels1 = new boolean[] { true, true, true, false, true, false };
    final boolean[] ressels2 = new boolean[] { true, true, false, false, false, false };

    BulkDataUtility.pushUp( vals1, sels1 );
    BulkDataUtility.pushUp( vals2, sels2 );

    if ( Arrays.equals( vals1, result1 ) == false ) {
      throw new NullPointerException();
    }
    if ( Arrays.equals( vals2, result2 ) == false ) {
      throw new NullPointerException();
    }
    if ( Arrays.equals( sels1, ressels1 ) == false ) {
      throw new NullPointerException();
    }
    if ( Arrays.equals( sels2, ressels2 ) == false ) {
      throw new NullPointerException();
    }
  }
}
