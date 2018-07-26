/*
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
 * Copyright (c) 2001 - 2018 Object Refinery Ltd, Hitachi Vantara and Contributors..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.core.parameters;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ReportEnvironment;

import java.util.Locale;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ParameterUtilsTest {

  private ReportEnvironment re = mock( ReportEnvironment.class );

  @Test
  public void getLocale_NullTest() {
    when( re.getLocale() ).thenReturn( null );
    Locale result = ParameterUtils.getLocale( re );
    assertNotNull( result );
  }

  @Test
  public void getLocale_ValidTest() {
    when( re.getLocale() ).thenReturn( new Locale( "just.for.test" ) );
    Locale result = ParameterUtils.getLocale( re );
    assertEquals( "just.for.test", result.getLanguage() );
  }
}
