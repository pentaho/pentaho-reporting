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
 * Copyright (c) 2005-2017 Hitachi Vantara..  All rights reserved.
 */

package org.pentaho.reporting.engine.classic.extensions.modules.java14config;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigFactory;

public class Java14ConfigModuleInitializerTest {

  @Test
  public void testPerformInit() throws Exception {
    Java14ConfigModuleInitializer initializer = new Java14ConfigModuleInitializer();
    initializer.performInit();

    assertThat( ConfigFactory.getInstance().getUserStorage(), is( instanceOf( Java14ConfigStorage.class ) ) );
    assertThat( ConfigFactory.getInstance().getSystemStorage(), is( instanceOf( Java14ConfigStorage.class ) ) );
  }
}
