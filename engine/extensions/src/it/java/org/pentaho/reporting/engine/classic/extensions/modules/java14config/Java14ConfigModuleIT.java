/*! ******************************************************************************
 *
 * Pentaho
 *
 * Copyright (C) 2024 by Hitachi Vantara, LLC : http://www.pentaho.com
 *
 * Use of this software is governed by the Business Source License included
 * in the LICENSE.TXT file.
 *
 * Change Date: 2028-08-13
 ******************************************************************************/

package org.pentaho.reporting.engine.classic.extensions.modules.java14config;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder;
import static org.junit.Assert.assertThat;

import org.junit.BeforeClass;
import org.junit.Test;
import org.pentaho.reporting.engine.classic.core.ClassicEngineBoot;
import org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigFactory;
import org.pentaho.reporting.libraries.base.boot.ModuleInfo;

public class Java14ConfigModuleIT {

  private static final String[] MODULE_REQUIRED_CLASSESS = new String[] {
    "org.pentaho.reporting.engine.classic.core.ClassicEngineCoreModule",
    "org.pentaho.reporting.engine.classic.core.modules.misc.configstore.base.ConfigStoreBaseModule" };

  @BeforeClass
  public static void setUp() {
    ClassicEngineBoot.getInstance().start();
  }

  @Test
  public void testInitialize() throws Exception {
    Java14ConfigModule module = new Java14ConfigModule();
    module.initialize( null );

    assertThat( module.getDescription(), is( equalTo( "Initialializer to support external configuration storages." ) ) );
    assertThat( module.getMajorVersion(), is( equalTo( "3" ) ) );
    assertThat( module.getMinorVersion(), is( equalTo( "5" ) ) );
    assertThat( module.getPatchLevel(), is( equalTo( "0" ) ) );
    assertThat( module.getName(), is( equalTo( "misc-configstore-jdk-prefs" ) ) );
    assertThat( module.getProducer(), is( equalTo( "The Pentaho Reporting Project" ) ) );
    assertThat( module.getSubSystem(), is( equalTo( "configstore" ) ) );

    ModuleInfo[] requiredModules = module.getRequiredModules();
    assertThat( requiredModules.length, is( equalTo( 2 ) ) );
    assertThat( new String[] { requiredModules[0].getModuleClass(), requiredModules[1].getModuleClass() },
        is( arrayContainingInAnyOrder( MODULE_REQUIRED_CLASSESS ) ) );

    assertThat( ConfigFactory.getInstance().getUserStorage(), is( not( instanceOf( Java14ConfigStorage.class ) ) ) );
    assertThat( ConfigFactory.getInstance().getSystemStorage(), is( not( instanceOf( Java14ConfigStorage.class ) ) ) );
  }
}
