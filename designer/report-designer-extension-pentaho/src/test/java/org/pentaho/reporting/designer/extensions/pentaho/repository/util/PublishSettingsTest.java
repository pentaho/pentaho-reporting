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

package org.pentaho.reporting.designer.extensions.pentaho.repository.util;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

@Ignore("We cannot test PublishSettns as it is using the preferences-API and thus is not independent.")
public class PublishSettingsTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testGetInstance() {
    PublishSettings settings = PublishSettings.getInstance();
    assertNotNull( settings );
  }

  @Test
  public void testIsRememberSettings() {
    PublishSettings settings = PublishSettings.getInstance();
    settings.setRememberSettings( false );
    assertFalse( settings.isRememberSettings() );
  }

  @Test
  public void testSetRememberSettings() {
    PublishSettings settings = PublishSettings.getInstance();
    assertFalse( settings.isRememberSettings() );
    settings.setRememberSettings( true );
    assertTrue( settings.isRememberSettings() );
  }

}
