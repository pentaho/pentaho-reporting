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

package org.pentaho.reporting.engine.classic.extensions.modules.mailer;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.libraries.base.boot.ModuleInfo;

public class MailModuleIT {

  @BeforeClass
  public static void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testInitialize() throws Exception {
    MailModule module = new MailModule();
    module.initialize( null );

    assertThat( module.getDescription(), is( equalTo( "Classes to support the generation and sending of e-mail" ) ) );
    assertThat( module.getMajorVersion(), is( equalTo( "3" ) ) );
    assertThat( module.getMinorVersion(), is( equalTo( "5" ) ) );
    assertThat( module.getPatchLevel(), is( equalTo( "0" ) ) );
    assertThat( module.getName(), is( equalTo( "ext-mail-support" ) ) );
    assertThat( module.getProducer(), is( equalTo( "The Pentaho Reporting Project" ) ) );

    ModuleInfo[] requiredModules = module.getRequiredModules();
    assertThat( requiredModules.length, is( equalTo( 1 ) ) );
    ModuleInfo requiredModule = requiredModules[0];
    assertThat( requiredModule.getModuleClass(),
        is( equalTo( "org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule" ) ) );
    assertThat( requiredModule.getMinorVersion(), is( equalTo( "5" ) ) );
    assertThat( requiredModule.getMajorVersion(), is( equalTo( "3" ) ) );
    assertThat( requiredModule.getPatchLevel(), is( equalTo( "0" ) ) );
  }
}
