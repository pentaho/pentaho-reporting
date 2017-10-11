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

package org.pentaho.reporting.designer.extensions.pentaho.drilldown;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;

import org.junit.Before;
import org.junit.Test;
import org.pentaho.reporting.libraries.base.boot.ModuleInitializeException;
import org.pentaho.reporting.libraries.base.boot.SubSystem;

public class PentahoDrillDownModuleTest {

  SubSystem subSystem;

  @Before
  public void setUp() throws Exception {
    subSystem = mock( SubSystem.class );
  }

  @Test
  public void testPentahoDrillDownModule() {
    try {
      PentahoDrillDownModule module = new PentahoDrillDownModule();
      assertNotNull( module );
    } catch ( ModuleInitializeException e ) {
      e.printStackTrace();
    }
  }

  @Test
  public void testInitialize() {
    try {
      PentahoDrillDownModule module = new PentahoDrillDownModule();
      assertNotNull( module );
      module.initialize( subSystem );
    } catch ( ModuleInitializeException e ) {
      e.printStackTrace();
    }
  }

}
