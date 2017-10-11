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

/**
 * Tests for the <code>StringUtils</code> class.
 *
 * @author David Kincade
 */
public class StringUtilsTest extends TestCase {
  public void testIsEmpty() {
    // TRUE
    assertTrue( StringUtils.isEmpty( null ) );
    assertTrue( StringUtils.isEmpty( "" ) );
    assertTrue( StringUtils.isEmpty( "    " ) );
    assertTrue( StringUtils.isEmpty( "\t\n \t" ) );
    assertTrue( StringUtils.isEmpty( null, true ) );
    assertTrue( StringUtils.isEmpty( "", true ) );
    assertTrue( StringUtils.isEmpty( "    ", true ) );
    assertTrue( StringUtils.isEmpty( "\t\n \t", true ) );
    assertTrue( StringUtils.isEmpty( null, false ) );
    assertTrue( StringUtils.isEmpty( "", false ) );

    // FALSE
    assertTrue( !StringUtils.isEmpty( "test" ) );
    assertTrue( !StringUtils.isEmpty( "     test    " ) );
    assertTrue( !StringUtils.isEmpty( "test", true ) );
    assertTrue( !StringUtils.isEmpty( "     test    ", true ) );
    assertTrue( !StringUtils.isEmpty( "    ", false ) );
    assertTrue( !StringUtils.isEmpty( "\t\n \t", false ) );
    assertTrue( !StringUtils.isEmpty( "test", false ) );
    assertTrue( !StringUtils.isEmpty( "     test    ", false ) );
  }

  public void testToBoolean() {
    // TRUE
    assertTrue( StringUtils.toBoolean( "true" ) );
    assertTrue( StringUtils.toBoolean( "on" ) );
    assertTrue( StringUtils.toBoolean( "yes" ) );
    assertTrue( StringUtils.toBoolean( "TRUE" ) );
    assertTrue( StringUtils.toBoolean( "ON" ) );
    assertTrue( StringUtils.toBoolean( "YES" ) );
    assertTrue( StringUtils.toBoolean( "tRUe" ) );
    assertTrue( StringUtils.toBoolean( "oN" ) );
    assertTrue( StringUtils.toBoolean( "yEs" ) );
    assertTrue( StringUtils.toBoolean( " true " ) );
    assertTrue( StringUtils.toBoolean( "\t on \t" ) );
    assertTrue( StringUtils.toBoolean( " \nyes\n " ) );
    assertTrue( StringUtils.toBoolean( " TRUE " ) );
    assertTrue( StringUtils.toBoolean( "\t ON \t" ) );
    assertTrue( StringUtils.toBoolean( " \fYES\f " ) );
    assertTrue( StringUtils.toBoolean( " tRUe " ) );
    assertTrue( StringUtils.toBoolean( "\t oN \t" ) );
    assertTrue( StringUtils.toBoolean( " \u0009yEs\u0009 " ) );
    assertTrue( StringUtils.toBoolean( "true", false ) );
    assertTrue( StringUtils.toBoolean( "on", false ) );
    assertTrue( StringUtils.toBoolean( "yes", false ) );
    assertTrue( StringUtils.toBoolean( "TRUE", false ) );
    assertTrue( StringUtils.toBoolean( "ON", false ) );
    assertTrue( StringUtils.toBoolean( "YES", false ) );
    assertTrue( StringUtils.toBoolean( "tRUe", false ) );
    assertTrue( StringUtils.toBoolean( "oN", false ) );
    assertTrue( StringUtils.toBoolean( "yEs", false ) );
    assertTrue( StringUtils.toBoolean( " true ", false ) );
    assertTrue( StringUtils.toBoolean( "\t on \t", false ) );
    assertTrue( StringUtils.toBoolean( " \nyes\n ", false ) );
    assertTrue( StringUtils.toBoolean( " TRUE ", false ) );
    assertTrue( StringUtils.toBoolean( "\t ON \t", false ) );
    assertTrue( StringUtils.toBoolean( " \fYES\f ", false ) );
    assertTrue( StringUtils.toBoolean( " tRUe ", false ) );
    assertTrue( StringUtils.toBoolean( "\t oN \t", false ) );
    assertTrue( StringUtils.toBoolean( " \u0009yEs\u0009 ", false ) );
    assertTrue( StringUtils.toBoolean( "true", true ) );
    assertTrue( StringUtils.toBoolean( "on", true ) );
    assertTrue( StringUtils.toBoolean( "yes", true ) );
    assertTrue( StringUtils.toBoolean( "TRUE", true ) );
    assertTrue( StringUtils.toBoolean( "ON", true ) );
    assertTrue( StringUtils.toBoolean( "YES", true ) );
    assertTrue( StringUtils.toBoolean( "tRUe", true ) );
    assertTrue( StringUtils.toBoolean( "oN", true ) );
    assertTrue( StringUtils.toBoolean( "yEs", true ) );
    assertTrue( StringUtils.toBoolean( " true ", true ) );
    assertTrue( StringUtils.toBoolean( "\t on \t", true ) );
    assertTrue( StringUtils.toBoolean( " \nyes\n ", true ) );
    assertTrue( StringUtils.toBoolean( " TRUE ", true ) );
    assertTrue( StringUtils.toBoolean( "\t ON \t", true ) );
    assertTrue( StringUtils.toBoolean( " \fYES\f ", true ) );
    assertTrue( StringUtils.toBoolean( " tRUe ", true ) );
    assertTrue( StringUtils.toBoolean( "\t oN \t", true ) );
    assertTrue( StringUtils.toBoolean( " \u0009yEs\u0009 ", true ) );
    assertTrue( StringUtils.toBoolean( null, true ) );

    // FALSE
    assertTrue( !StringUtils.toBoolean( null ) );
    assertTrue( !StringUtils.toBoolean( "false" ) );
    assertTrue( !StringUtils.toBoolean( "off" ) );
    assertTrue( !StringUtils.toBoolean( "no" ) );
    assertTrue( !StringUtils.toBoolean( "FALSE" ) );
    assertTrue( !StringUtils.toBoolean( "NO" ) );
    assertTrue( !StringUtils.toBoolean( "OFF" ) );
    assertTrue( !StringUtils.toBoolean( "fALSe" ) );
    assertTrue( !StringUtils.toBoolean( "oFf" ) );
    assertTrue( !StringUtils.toBoolean( "nO" ) );
    assertTrue( !StringUtils.toBoolean( "  false  " ) );
    assertTrue( !StringUtils.toBoolean( " \t off \t " ) );
    assertTrue( !StringUtils.toBoolean( "\n no \n" ) );
    assertTrue( !StringUtils.toBoolean( "true.", true ) );
    assertTrue( !StringUtils.toBoolean( "onn", true ) );
    assertTrue( !StringUtils.toBoolean( "yes!", true ) );
    assertTrue( !StringUtils.toBoolean( "false", true ) );
    assertTrue( !StringUtils.toBoolean( "off", true ) );
    assertTrue( !StringUtils.toBoolean( "no", true ) );
    assertTrue( !StringUtils.toBoolean( "FALSE", true ) );
    assertTrue( !StringUtils.toBoolean( "NO", true ) );
    assertTrue( !StringUtils.toBoolean( "OFF", true ) );
    assertTrue( !StringUtils.toBoolean( "fALSe", true ) );
    assertTrue( !StringUtils.toBoolean( "oFf", true ) );
    assertTrue( !StringUtils.toBoolean( "nO", true ) );
    assertTrue( !StringUtils.toBoolean( "  false  ", true ) );
    assertTrue( !StringUtils.toBoolean( " \t off \t ", true ) );
    assertTrue( !StringUtils.toBoolean( "\n no \n", true ) );
    assertTrue( !StringUtils.toBoolean( "true.", false ) );
    assertTrue( !StringUtils.toBoolean( "onn", false ) );
    assertTrue( !StringUtils.toBoolean( "yes!", false ) );
    assertTrue( !StringUtils.toBoolean( "false", false ) );
    assertTrue( !StringUtils.toBoolean( "off", false ) );
    assertTrue( !StringUtils.toBoolean( "no", false ) );
    assertTrue( !StringUtils.toBoolean( "FALSE", false ) );
    assertTrue( !StringUtils.toBoolean( "NO", false ) );
    assertTrue( !StringUtils.toBoolean( "OFF", false ) );
    assertTrue( !StringUtils.toBoolean( "fALSe", false ) );
    assertTrue( !StringUtils.toBoolean( "oFf", false ) );
    assertTrue( !StringUtils.toBoolean( "nO", false ) );
    assertTrue( !StringUtils.toBoolean( "  false  ", false ) );
    assertTrue( !StringUtils.toBoolean( " \t off \t ", false ) );
    assertTrue( !StringUtils.toBoolean( "\n no \n", false ) );
    assertTrue( !StringUtils.toBoolean( "true.", false ) );
    assertTrue( !StringUtils.toBoolean( "onn", false ) );
    assertTrue( !StringUtils.toBoolean( "yes!", false ) );
    assertTrue( !StringUtils.toBoolean( null, false ) );
  }
}

