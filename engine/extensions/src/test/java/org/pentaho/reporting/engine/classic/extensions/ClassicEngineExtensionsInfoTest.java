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
 * Copyright (c) 2005-2015 Pentaho Corporation..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.pentaho.reporting.libraries.base.versioning.ProjectInformation;

public class ClassicEngineExtensionsInfoTest {

  @Test
  public void testInfo() {
    ProjectInformation info = ClassicEngineExtensionsInfo.getInstance();
    assertThat( info, is( notNullValue() ) );
    assertThat( info.getInfo(), is( equalTo( "http://reporting.pentaho.org/" ) ) );
    assertThat( info.getCopyright(), is( equalTo( "(C)opyright 2000-2011, by Pentaho Corp. and Contributors" ) ) );
    assertThat( info.getLicenseName(), is( equalTo( "LGPL" ) ) );
    assertThat( info.getName(), is( equalTo( "Pentaho Reporting Classic Extensions" ) ) );
  }
}
